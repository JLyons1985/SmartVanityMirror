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
* Author: Joshua Lyons (josh@lyonsdensoftware.com)
*************************************************************************/

package com.lyonsdensoftware.vanitymirror;

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
    
    // Class Variable Declaration
    private static final Logger log = LoggerFactory.getLogger(Alexa.class);
    
    private final AVSController controller;
    private Thread autoEndpoint = null; // used to auto-endpoint while listening
    private final DeviceConfig deviceConfig;
    // minimum audio level threshold under which is considered silence
    private static final int ENDPOINT_THRESHOLD = 5;
    private static final int ENDPOINT_SECONDS = 2; // amount of silence time before endpointing
    private String accessToken;
    private static final String START_LABEL = "Start Listening";
    private static final String STOP_LABEL = "Stop Listening";
    private static final String PROCESSING_LABEL = "Processing";
    private JButton actionButton;
    private JProgressBar visualizer;
    private vanityMirrorGUI mainWindow;

    private AuthSetup authSetup;
    
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
    
    protected AVSClientFactory getAVSClientFactory(DeviceConfig config) {
        return new AVSClientFactory(config);
    }
    
    public void addActionField() {
        final RecordingRMSListener rmsListener = this;
        actionButton = mainWindow.getActionButton();
        visualizer = mainWindow.getVisualizer();
        controller.onUserActivity();
        if (mainWindow.getActionButton().getText().equals(START_LABEL)) { // if in idle mode
            mainWindow.getActionButton().setText(STOP_LABEL);
            
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
                    mainWindow.getActionButton().doClick();
                    finishProcessing();
                }
            };

            controller.startRecording(rmsListener, requestListener);
        } else { // else we must already be in listening
            mainWindow.getActionButton().setText(PROCESSING_LABEL); // go into processing mode
            mainWindow.getActionButton().setEnabled(false);
            mainWindow.getVisualizer().setIndeterminate(true);
            controller.stopRecording(); // stop the recording so the request can complete
            mainWindow.toggelPin();
        }
    }
      
    
    public void finishProcessing() {
        mainWindow.getActionButton().setText(START_LABEL);
        mainWindow.getActionButton().setEnabled(true);
        mainWindow.getVisualizer().setIndeterminate(false);
        controller.processingFinished();

    }
    
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
                            actionButton.doClick(); // hit stop if we get through the autoendpoint
                                                    // time
                        } catch (InterruptedException e) {
                            return;
                        }
                    }
                };
                autoEndpoint.start();
            }
        }

        visualizer.setValue(rms); // update the visualizer
    }

    @Override
    public void onExpectSpeechDirective() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                while (!actionButton.isEnabled() || !actionButton.getText().equals(START_LABEL)
                        || controller.isSpeaking()) {
                    try {
                        Thread.sleep(500);
                    } catch (Exception e) {
                    }
                }
                actionButton.doClick();
            }
        };
        thread.start();

    }

    public void showDialog(String message) {
        JTextArea textMessage = new JTextArea(message);
        textMessage.setEditable(false);
        JOptionPane.showMessageDialog(this.mainWindow.getContentPane(), textMessage, "Information",
                JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void displayRegCode(String regCode) {
        String regUrl =
                deviceConfig.getCompanionServiceInfo().getServiceUrl() + "/provision/" + regCode;
        showDialog("Please register your device by visiting the following website on "
                + "any system and following the instructions:\n" + regUrl
                + "\n\n Hit OK once completed.");
    }

    @Override
    public synchronized void onAccessTokenReceived(String accessToken) {
        this.accessToken = accessToken;        
    }  
    
}
