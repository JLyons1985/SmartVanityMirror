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
* Project File: DeviceConfig.java
* Project Description: Handles the configuration for the smart mirror
* @author Joshua Lyons (josh@lyonsdensoftware.com)
* @version 0.0.1
*************************************************************************/

package com.lyonsdensoftware.config;

// IMPORTS
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;


public class DeviceConfig {
    
    // CLASS VARIABLES - These should match what is stored on the config file
    public static final String FILE_NAME = "mirrorConfig.json";
    
    // Wunderground Settings
    private String wundergroundApiKey;
    private String wundergroundStateIdentifier;
    private String wundergroundCity;
    
    // Program Settings
    private final String productId;
    private final String dsn;
    
    // Python TCP settings
    private final String hostname;
    private final int port; 
    
    /**
     * Constructor
     * @param wundergroundApiKey
     * @param wundergroundStateIdentifier
     * @param wundergroundCity 
     */
    public DeviceConfig(String wundergroundApiKey, String wundergroundStateIdentifier, 
            String wundergroundCity, String productId, String dsn, String hostname, int port)
    {
        this.wundergroundApiKey = wundergroundApiKey;
        this.wundergroundCity = wundergroundCity;
        this.wundergroundStateIdentifier = wundergroundStateIdentifier;
        this.productId = productId;
        this.dsn = dsn;
        this.hostname = hostname;
        this.port = port;
    }
    
    /**
     * @return the wundergroundApiKey
     */
    public String getWundergroundApiKey() {
        return wundergroundApiKey;
    }

    /**
     * @param wundergroundApiKey the wundergroundApiKey to set
     */
    public void setWundergroundApiKey(String wundergroundApiKey) {
        this.wundergroundApiKey = wundergroundApiKey;
    }

    /**
     * @return the wundergroundStateIdentifier
     */
    public String getWundergroundStateIdentifier() {
        return wundergroundStateIdentifier;
    }

    /**
     * @param wundergroundStateIdentifier the wundergroundStateIdentifier to set
     */
    public void setWundergroundStateIdentifier(String wundergroundStateIdentifier) {
        this.wundergroundStateIdentifier = wundergroundStateIdentifier;
    }

    /**
     * @return the wundergroundCity
     */
    public String getWundergroundCity() {
        return wundergroundCity;
    }

    /**
     * @param wundergroundCity the wundergroundCity to set
     */
    public void setWundergroundCity(String wundergroundCity) {
        this.wundergroundCity = wundergroundCity;
    }
    
    /**
     * Save this file back to disk.
     */
    public void saveConfig() {
        DeviceConfigUtils.updateConfigFile(this);
    }
    
    /**
     * Serialize this object to JSON.
     *
     * @return A JSON representation of this object.
     */
    public JsonObject toJson() {
        JsonObjectBuilder builder = Json
                .createObjectBuilder()
                .add("productId", productId)
                .add("dsn", dsn);

        builder.add("wundergroundApi", Json.createObjectBuilder()
            .add("wundergroundApiKey", wundergroundApiKey)
            .add("wundergroundStateIdentifier", wundergroundStateIdentifier)
            .add("wundergroundCity", wundergroundCity));
        
        builder.add("pythonTCPSettings", Json.createObjectBuilder()
            .add("hostname", getHostname())
            .add("port", getPort()));
        
        return builder.build();
    }

    /**
     * @return the hostname
     */
    public String getHostname() {
        return hostname;
    }

    /**
     * @return the port
     */
    public int getPort() {
        return port;
    }
}
