/*************************************************************************
* Copyright 2016 Joshua Lyons
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
* Project File: Alexa.java
* Project Description: Handles the higher level Alexa stuff like getting 
*  the auth token on startup an handling all the voice data.
* @author  Joshua Lyons (josh@lyonsdensoftware.com)
*************************************************************************/

package com.lyonsdensoftware.vanitymirror;

// IMPORTS
import com.amazon.alexa.avs.AVSAudioPlayerFactory;
import com.amazon.alexa.avs.AVSController;
import com.amazon.alexa.avs.AlertManagerFactory;
import com.amazon.alexa.avs.DialogRequestIdAuthority;
import com.amazon.alexa.avs.ExpectSpeechListener;
import com.amazon.alexa.avs.PlaybackAction;
import com.amazon.alexa.avs.RecordingRMSListener;
import com.amazon.alexa.avs.RequestListener;
import com.amazon.alexa.avs.auth.AccessTokenListener;
import com.amazon.alexa.avs.auth.AuthSetup;
import com.amazon.alexa.avs.auth.companionservice.RegCodeDisplayHandler;
import com.amazon.alexa.avs.config.DeviceConfig;
import com.amazon.alexa.avs.config.DeviceConfigUtils;
import com.amazon.alexa.avs.http.AVSClientFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;

public class Alexa implements ExpectSpeechListener, RecordingRMSListener,
        RegCodeDisplayHandler, AccessTokenListener {
    
    // CLASS VARIABLES
    
    /**
     * Logger for the Alexa class.
     */
    private static final Logger log = LoggerFactory.getLogger(Alexa.class);
    
    private final AVSController controller;
    private Thread autoEndpoint = null; // used to auto-endpoint while listening
    private final DeviceConfig deviceConfig;
    
    // minimum audio level threshold under which is considered silence
    private static final int ENDPOINT_THRESHOLD = 5;
    private static final int ENDPOINT_SECONDS = 2; // amount of silence time before endpointing
    
    private String accessToken;
    
    // Labels for the button, Will replaced in final version
    private static final String START_LABEL = "Start Listening";
    private static final String STOP_LABEL = "Stop Listening";
    private static final String PROCESSING_LABEL = "Processing";
    
    private String pttState;
    private final vanityMirrorGUI mainWindow;

    private final AuthSetup authSetup;
    
    /**
     * 
     * @param mainWindow
     * @throws Exception 
     */
    public Alexa(vanityMirrorGUI mainWindow) throws Exception {
        this(DeviceConfigUtils.readConfigFile(), mainWindow);
    }
    
    /**
     * 
     * @param configName
     * @param mainWindow
     * @throws Exception 
     */
    public Alexa(String configName, vanityMirrorGUI mainWindow) throws Exception {
        this(DeviceConfigUtils.readConfigFile(configName), mainWindow);
    }
    
    /**
     * Creates a new Alexa
     * @param config Config to get passed to Alexa
     * @param mainWindow Reference to the vanityMirrorGUI window
     * @throws Exception
     */
    public Alexa(DeviceConfig config, vanityMirrorGUI mainWindow) throws Exception {
        
        this.mainWindow = mainWindow;
        deviceConfig = config;
        controller = new AVSController(this, new AVSAudioPlayerFactory(), new AlertManagerFactory(),
                getAVSClientFactory(deviceConfig), DialogRequestIdAuthority.getInstance());

        authSetup = new AuthSetup(config, this);
        authSetup.addAccessTokenListener(this);
        authSetup.addAccessTokenListener(controller);
        authSetup.startProvisioningThread();     

        controller.startHandlingDirectives();
    }
    
    /**
     * 
     * @param config
     * @return AVSClientFactory
     */
    protected AVSClientFactory getAVSClientFactory(DeviceConfig config) {
        return new AVSClientFactory(config);
    }
    
    /**
     * Handles the actions when the ptt is pressed
     */
    public void pttPressed() {
        final RecordingRMSListener rmsListener = this;
        pttState = mainWindow.getPttState();
        controller.onUserActivity();
        if (mainWindow.getPttState().equals(START_LABEL)) { // if in idle mode
            mainWindow.setPttState(STOP_LABEL);
            
            mainWindow.toggelPin();

            RequestListener requestListener = new RequestListener() {

                @Override
                public void onRequestSuccess() {
                    finishProcessing();
                }

                @Override
                public void onRequestError(Throwable e) {
                    log.error("An error occured creating speech request", e);
                    JOptionPane.showMessageDialog(mainWindow.getContentPane(), e.getMessage(), "Error",
                            JOptionPane.ERROR_MESSAGE);
                    pttPressed();
                    finishProcessing();
                }
            };

            controller.startRecording(rmsListener, requestListener);
        } else { // else we must already be in listening
            mainWindow.setPttState(PROCESSING_LABEL); // go into processing mode
            controller.stopRecording(); // stop the recording so the request can complete
            mainWindow.toggelPin();
        }
    }
      
    /**
     * Finish processing the recording
     */
    public void finishProcessing() {
        mainWindow.setPttState(START_LABEL);
        controller.processingFinished();

    }
    
    /**
     * 
     * @param rms 
     */
    @Override
    public void rmsChanged(int rms) { // AudioRMSListener callback
        // if greater than threshold or not recording, kill the autoendpoint thread
        if ((rms == 0) || (rms > ENDPOINT_THRESHOLD)) {
            if (autoEndpoint != null) {
                autoEndpoint.interrupt();
                autoEndpoint = null;
            }
        } else if (rms < ENDPOINT_THRESHOLD) {
            // start the autoendpoint thread if it isn't already running
            if (autoEndpoint == null) {
                autoEndpoint = new Thread() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(ENDPOINT_SECONDS * 1000);
                            pttPressed();
                        } catch (InterruptedException e) {
                            return;
                        }
                    }
                };
                autoEndpoint.start();
            }
        }

    }

    /**
     * Handles for the Expect Speech Directive
     */
    @Override
    public void onExpectSpeechDirective() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                while (!mainWindow.getPttState().equals(START_LABEL) || controller.isSpeaking()) {
                    try {
                        Thread.sleep(500);
                    } catch (Exception e) {
                    }
                }
                pttPressed();
            }
        };
        thread.start();

    }

    /**
     * Used to show dialog messages for debugging
     * @param message 
     */
    public void showDialog(String message) {
        JTextArea textMessage = new JTextArea(message);
        textMessage.setEditable(false);
        JOptionPane.showMessageDialog(this.mainWindow.getContentPane(), textMessage, "Information",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * 
     * @param regCode 
     */
    @Override
    public void displayRegCode(String regCode) {
        String regUrl =
                deviceConfig.getCompanionServiceInfo().getServiceUrl() + "/provision/" + regCode;
        showDialog("Please register your device by visiting the following website on "
                + "any system and following the instructions:\n" + regUrl
                + "\n\n Hit OK once completed.");
    }

    /**
     * Handles when an access token is recieved.
     * @param accessToken 
     */
    @Override
    public synchronized void onAccessTokenReceived(String accessToken) {
        this.accessToken = accessToken;        
        //deviceConfig.setAccessToken(accessToken);
        //deviceConfig.saveConfig();
    }  
    
}
