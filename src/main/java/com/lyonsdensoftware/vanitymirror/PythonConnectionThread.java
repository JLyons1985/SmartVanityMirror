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
* Project File: PythonConnectionThread.java
* Project Description: Handles the python tcp connection  and directs the trafic
*   where it should go
* @author Joshua Lyons (josh@lyonsdensoftware.com)
* @version 0.0.1
*************************************************************************/

package com.lyonsdensoftware.vanitymirror;

// IMPORTS
import com.google.gson.Gson;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.*;
import java.net.*;
import javax.json.JsonObject;
import org.json.JSONException;
import org.json.JSONObject;
import com.lyonsdensoftware.config.DeviceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PythonConnectionThread implements Runnable{
    
    // Class variables
    private boolean runThread = true;                       // Do we keep running the thread
    private Socket socket;                                  // Reference to the client socket
    private final vanityMirrorGUI mainWindow ;              // Ref to the main window
    private boolean connectedToServer;                      // Are we connected to the server
    /**
     * Logger for the vanityMirrorGUI class.
     */
    private static final Logger log = LoggerFactory.getLogger(PythonConnectionThread.class);

    /** Construct a thread
     * @param mainWindow */
    public PythonConnectionThread(vanityMirrorGUI mainWindow, Socket socket) {
       this.runThread = true;
       this.mainWindow = mainWindow;
       this.socket = socket;
    }
    
    @Override
    public void run() {
        // Try connecting to the master server
        try {

            // Create a socket to connect to the server
            socket = new Socket(this.mainWindow.getConfigFile().getHostname(), 
                this.mainWindow.getConfigFile().getPort());
            
            // Note it in the log
            log.info("LOG", "Connect to MasterServer at IP: " + socket.getInetAddress().getHostAddress() + 
                        " on PORT: " + socket.getPort());
            
            connectedToServer = true;

            // Create an input stream to receive data from the server
            mainWindow.setFromServer(new DataInputStream( socket.getInputStream() ));

            // Create an output stream to send data to the server
            mainWindow.setToServer(new DataOutputStream( socket.getOutputStream() ));
            
            // Main Loop
            while (runThread) {
                
                // Check for data from the server
                BufferedReader in = new BufferedReader(new InputStreamReader(mainWindow.getFromServer()));
                
                if (in.ready()) {
                    // Convert in data to json
                    Gson gson = new Gson();
                
                    
                    JSONObject json = new JSONObject(gson.fromJson(in.readLine(), JSONObject.class));
                                        
                    if (json.keys().hasNext()) {
                        // handle the data
                        handleDataFromServer(json);  
                    }
                    else {
                        System.out.println(json.toString());
                    }
                }
            }
            
            // Loop not running now so close connection
            socket.close();
            mainWindow.getFromServer().close();
            mainWindow.getToServer().close();
        }
        catch (IOException ex) {
            log.error("ERROR", ex.toString());
        }        
    }

    private void handleDataFromServer(JSONObject json) {
        // Variables
        String tmpMessages;
        JSONObject tmpJson;
        Gson gson;
        
        System.out.println(json.toString());
        
        // Determine how to handle the message
        switch (json.get("messageType").toString()) {
            case "Return":
                tmpJson = json.getJSONObject("Return");
                System.out.println(tmpJson.getString("message"));
                break;
        }
    }
    
    // Sends a message to the server
    public void sendMessageToServer(String messageType, String message) {
             
        // Create JSON and GSon objects
        JSONObject json = new JSONObject();
        Gson gson = new Gson();
            
        // Put message into JSON
        json.put("messageType", messageType);
        json.put("message", message);
            
        //Make printer writer
        PrintWriter pw = new PrintWriter(mainWindow.getToServer());
            
        // Send the message
        pw.println(gson.toJson(json));
        pw.flush();
                        
    }
    
}
