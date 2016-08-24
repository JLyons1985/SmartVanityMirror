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
* Project File: vanityMirrorGUI.java
* Project Description: GUI for Rachels Vanity Mirror
* @author Joshua Lyons (josh@lyonsdensoftware.com)
* @version 0.0.1
*************************************************************************/

package com.lyonsdensoftware.vanitymirror;

// IMPORTS
import com.lyonsdensoftware.config.DeviceConfig;
import com.lyonsdensoftware.config.DeviceConfigUtils;

//import com.lyonsdensoftware.facerecognition.Train;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import javax.swing.JButton;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Calendar;
import java.util.Locale;


@SuppressWarnings("serial")
public class vanityMirrorGUI extends javax.swing.JFrame {

    
    
    // CLASS VARIABLES
    private Socket socket;                                                              // Reference to the client socket
    private static DataOutputStream toServer;						// Output stream to master server or game server
    private static DataInputStream fromServer;						// Input from either the game server or master server
    private final PythonConnectionThread myPythonConnection;
    
    /**
     * Logger for the vanityMirrorGUI class.
     */
    private static final Logger log = LoggerFactory.getLogger(vanityMirrorGUI.class);
    
    /**
     * Holds the reference to the Alexa class. Interaction with that class allows one to
     * talk to Amazon's Alexa.
     */
    private final Alexa alexa;

    // Label strings for various objects on the GUI
    private static final String APP_TITLE = "Alexa Voice Service";
    private static final String START_LABEL = "Start Listening";
    private String pttState;
    
    // GPIO VARIABLES
    private final GpioController gpio = GpioFactory.getInstance();
    
    /**
     * GPIO pin that the button for the PTT is connected too.
     */
    private final GpioPinDigitalInput pttButton = gpio.provisionDigitalInputPin(RaspiPin.GPIO_02, PinPullResistance.PULL_DOWN);
    
    /**
     * GPIO pin that is connected to an LED. USed for testing
     */
    final GpioPinDigitalOutput pin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_07, "MyLED", PinState.LOW);
    
    private final DeviceConfig configFile;

    // FUNCTIONS 
    
    /**
     * Creates new form vanityMirrorGUI
     * @throws java.lang.Exception
     */
    public vanityMirrorGUI() throws Exception {
        this(DeviceConfigUtils.readConfigFile());
    }
    
    /**
     * Creates new form vanityMirrorGUI
     * @param configName is a string of the config name
     * @throws java.lang.Exception
     */
    public vanityMirrorGUI(String configName) throws Exception {
        this(DeviceConfigUtils.readConfigFile(configName));
    }
    
    /**
     * Creates new form vanityMirrorGUI
     * @param config is the device config to create the mirror gui
     * @throws java.lang.Exception
     */
    public vanityMirrorGUI(DeviceConfig config) throws Exception {
        
        // init gui components
        initComponents();
        
        this.configFile = config;
        
        // Create a new alexa
        alexa = new Alexa(this);
        
        // Create gui listeners
        createListeners();
        
        // Start GUI text updater threads
        startGUIUpdateThreads();
        
        // Connect to Python TCP Server
        this.myPythonConnection = connectPythonTCPServer();
    }
      
    /**
     * Creates the listeners for the gui
     */
    private void createListeners() {
        
        // Set the action button params, action button will be replaced bu physical btn
        pttState = START_LABEL;
        
        // Cretae physical button params
        pttButton.setShutdownOptions(true);        
        pttButton.addListener(new MyGpioPinListener(alexa));
    }   
    
    /**
     * Returns the app version
     * @return string
     */
    private String getAppVersion() {
        final Properties properties = new Properties();
        try (final InputStream stream = getClass().getResourceAsStream("/res/version.properties")) {
            properties.load(stream);
            if (properties.containsKey("version")) {
                return properties.getProperty("version");
            }
        } catch (IOException e) {
            log.warn("version.properties file not found on classpath");
        }
        return null;
    }
    
    /**
     * Returns the app title
     * @return string
     */
    private String getAppTitle() {
        String version = getAppVersion();
        String title = APP_TITLE;
        if (version != null) {
            title += " - v" + version;
        }
        return title;
    }
    
    /**
     * Returns the action button
     * @return JButton
     */
    public String getPttState () {
        return this.pttState;
    }
    
    /**
     * Returns the action button
     * @param state
     */
    public void setPttState (String state) {
        this.pttState = state;
    }
    
    /**
     * @return the toServer
     */
    public DataOutputStream getToServer() {
        return toServer;
    }

    /**
     * @return the fromServer
     */
    public DataInputStream getFromServer() {
        return fromServer;
    }
    
    /**
     * @param out
     */
    public void setToServer(DataOutputStream out) {
        toServer = out;
    }

    /**
     * @param in
     */
    public void setFromServer(DataInputStream in) {
        fromServer = in;
    }
    
    // Connects to the Python TCP Server
    public PythonConnectionThread connectPythonTCPServer() {
		
        // Connects to the master server
        // Create a handler thread
        PythonConnectionThread task = new PythonConnectionThread(this, this.socket);
                
        // Start the new thread
        new Thread(task).start();
        
        return task;
            
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTextField1 = new javax.swing.JTextField();
        mainPanel = new javax.swing.JPanel();
        timePanel = new javax.swing.JPanel();
        dateLabel = new javax.swing.JLabel();
        weatherPanel = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();

        jTextField1.setText("jTextField1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setName("mainWindow"); // NOI18N
        setPreferredSize(new java.awt.Dimension(1080, 1920));
        setResizable(false);

        mainPanel.setBackground(new java.awt.Color(0, 0, 0));

        timePanel.setBackground(new java.awt.Color(0, 0, 0));
        timePanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));

        dateLabel.setFont(new java.awt.Font("Helvetica Neue", 0, 18)); // NOI18N
        dateLabel.setForeground(new java.awt.Color(255, 255, 255));
        dateLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        dateLabel.setText("Day Month Year");

        javax.swing.GroupLayout timePanelLayout = new javax.swing.GroupLayout(timePanel);
        timePanel.setLayout(timePanelLayout);
        timePanelLayout.setHorizontalGroup(
            timePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(timePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(dateLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 377, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        timePanelLayout.setVerticalGroup(
            timePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(timePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(dateLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(304, Short.MAX_VALUE))
        );

        weatherPanel.setBackground(new java.awt.Color(0, 0, 0));
        weatherPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));

        javax.swing.GroupLayout weatherPanelLayout = new javax.swing.GroupLayout(weatherPanel);
        weatherPanel.setLayout(weatherPanelLayout);
        weatherPanelLayout.setHorizontalGroup(
            weatherPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 354, Short.MAX_VALUE)
        );
        weatherPanelLayout.setVerticalGroup(
            weatherPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 340, Short.MAX_VALUE)
        );

        jButton1.setText("Train");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addComponent(weatherPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(115, 115, 115)
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 153, Short.MAX_VALUE)
                .addComponent(timePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(timePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(weatherPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 599, Short.MAX_VALUE))
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addGap(195, 195, 195)
                .addComponent(jButton1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        this.myPythonConnection.sendMessageToServer("Request", "SomeRequest");
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     * @throws Exception
     */
    public static void main(String args[]) throws Exception{
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(vanityMirrorGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(vanityMirrorGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(vanityMirrorGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(vanityMirrorGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            if (args.length == 1)
            {
                try {
                    new vanityMirrorGUI(args[0]).setVisible(true);
                } catch (Exception ex) {
                    java.util.logging.Logger.getLogger(vanityMirrorGUI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            else
            {
                try {
                    new vanityMirrorGUI().setVisible(true);
                } catch (Exception ex) {
                    java.util.logging.Logger.getLogger(vanityMirrorGUI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }
    
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel dateLabel;
    private javax.swing.JButton jButton1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel timePanel;
    private javax.swing.JPanel weatherPanel;
    // End of variables declaration//GEN-END:variables

    /**
     * Toggles the LED
     */
    public void toggelPin() {
        pin.toggle();
    }

    /**
     * Starts different threads for the GUI elements
     */
    private void startGUIUpdateThreads() {
        
        // Thread that updates the day month year text
        Thread dateThread = new Thread() {
            @Override
            public void run() {
                while (true) {
                    
                    // Get the current calendar day
                    Calendar cal = Calendar.getInstance();
                    String month = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()); 
                    String weekDay = cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
                    cal.get(Calendar.DAY_OF_MONTH);
                    String day = Integer.toString(cal.get(Calendar.DAY_OF_MONTH));
                    String year = Integer.toString(cal.get(Calendar.YEAR));
                    
                    // Set the label
                    dateLabel.setText(weekDay + ", " + month + " " + day + ", " + year);
                            
                    try {
                        // Determin how long until the next day so we are not updating all the time
                        Calendar c = cal;
                        c.add(Calendar.DAY_OF_MONTH, 1);
                        c.set(Calendar.HOUR_OF_DAY, 0);
                        c.set(Calendar.MINUTE, 0);
                        c.set(Calendar.SECOND, 1);
                        c.set(Calendar.MILLISECOND, 0);
                        
                        long howManyMilli = (c.getTimeInMillis()-System.currentTimeMillis());
                        
                        Thread.sleep(howManyMilli);
                        
                    } catch (Exception e) {
                    }
                }
            }
        };
        dateThread.start();
    }

    /**
     * @return the configFile
     */
    public DeviceConfig getConfigFile() {
        return configFile;
    }
}

/**
 * Listener class for the JButton
 */
class MyButtonListerner implements ActionListener {
    
    // Variables
    private final Alexa privateAlexa;
    
    public MyButtonListerner(Alexa alexa) {
        privateAlexa = alexa;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        // Call the ptt pressed from Alexa
        privateAlexa.pttPressed();
    }
}

/**
 * Listener class for the physical GPIO button
 */
class MyGpioPinListener implements GpioPinListenerDigital {
    
    // Variables
    private final Alexa privateAlexa;
    
    public MyGpioPinListener(Alexa alexa) {
        privateAlexa = alexa;
    }
    
    @Override
    public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
        // Only do something when the ptt is down. Makes it so the function doesnt
        // double fire when bttn is released.
        if (event.getState() == PinState.LOW) {
            // Call the ptt pressed from Alexa
            privateAlexa.pttPressed();
        }
    }
}
