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
import com.amazon.alexa.avs.config.DeviceConfig;
import com.amazon.alexa.avs.config.DeviceConfigUtils;


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


@SuppressWarnings("serial")
public class vanityMirrorGUI extends javax.swing.JFrame {
    
    // CLASS VARIABLES
    
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

    // FUNCTIONS 
    
    /**
     * Creates new form vanityMirrorGUI
     */
    public vanityMirrorGUI() throws Exception {
        this(DeviceConfigUtils.readConfigFile());
    }
    
    /**
     * Creates new form vanityMirrorGUI
     * @param configName is a string of the config name
     */
    public vanityMirrorGUI(String configName) throws Exception {
        this(DeviceConfigUtils.readConfigFile(configName));
    }
    
    /**
     * Creates new form vanityMirrorGUI
     * @param config is the device config to create the mirror gui
     */
    public vanityMirrorGUI(DeviceConfig config) throws Exception {
        
        // init gui components
        initComponents();
        
        // Create a new alexa
        alexa = new Alexa(config, this);
        
        // Create gui listeners
        createListeners();
    }
      
    /**
     * Creates the listeners for the gui
     */
    private void createListeners() {
        
        // Set the action button params, action button will be replaced bu physical btn
        actionButton.setText(START_LABEL);
        actionButton.setEnabled(true);
        actionButton.addActionListener(new MyButtonListerner(alexa));
        
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
    public JButton getActionButton () {
        return this.actionButton;
    }
    
    /**
     * Returns the visualizer
     * @return JProgress Bar
     */
    public javax.swing.JProgressBar getVisualizer() {
        return this.visualizer;
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
        visualizer = new javax.swing.JProgressBar();
        actionButton = new javax.swing.JButton();
        container = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();

        jTextField1.setText("jTextField1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        mainPanel.setName(""); // NOI18N

        actionButton.setText("jButton1");

        javax.swing.GroupLayout containerLayout = new javax.swing.GroupLayout(container);
        container.setLayout(containerLayout);
        containerLayout.setHorizontalGroup(
            containerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 962, Short.MAX_VALUE)
        );
        containerLayout.setVerticalGroup(
            containerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 157, Short.MAX_VALUE)
        );

        jLabel1.setText("Blah");

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(actionButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(container, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(visualizer, javax.swing.GroupLayout.DEFAULT_SIZE, 951, Short.MAX_VALUE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(visualizer, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addComponent(jLabel1)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(actionButton, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(83, 83, 83)
                .addComponent(container, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(296, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

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
    private javax.swing.JButton actionButton;
    private javax.swing.JPanel container;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JProgressBar visualizer;
    // End of variables declaration//GEN-END:variables

    /**
     * Toggles the LED
     */
    public void toggelPin() {
        pin.toggle();
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
