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
* Project File: Weather.java
* Project Description: Weather class that will handle grabbing all the weather info
*   and displaying it correctly.
* @author Joshua Lyons (josh@lyonsdensoftware.com)
* @version 0.0.1
*************************************************************************/

package com.lyonsdensoftware.vanitymirror;

// IMPORTS
import com.lyonsdensoftware.config.DeviceConfig;
import com.lyonsdensoftware.config.DeviceConfigUtils;
import java.io.IOException;
import javax.json.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

public class Weather {
 
    // CLASS VARIABLES
    
    final static String WG_URL = 
            "http://api.wunderground.com/api/";         // Url to wundergound main api entry
    final static JsonReader JSON_READER = 
            new JsonReader();                           // Reference to json reader class
    private final DeviceConfig deviceConfig;            // Config file
    
    private String wundergroundStateIdentifier;         // Holds the state identifier - AZ
    private String wundergroundCity;                    // Holds the city info - Phoenix
    private String wundergroundApiKey;                 // Holds the api key for wunderground
    
    /**
     * Constructors
     */
    public Weather() throws Exception {
        this(DeviceConfigUtils.readConfigFile());
    }

    /**
     * 
     * @param configName
     * @throws Exception 
     */
    public Weather(String configName) throws Exception {
        this(DeviceConfigUtils.readConfigFile(configName));
    }
    
    /**
     * 
     * @param config 
     */
    public Weather (DeviceConfig config) {
        
        // Variables
        this.deviceConfig = config;
        this.wundergroundApiKey = this.deviceConfig.getWundergroundApiKey();
        this.wundergroundCity = this.deviceConfig.getWundergroundCity();
        this.wundergroundStateIdentifier = this.deviceConfig.getWundergroundStateIdentifier();        
        
    }
    
    public JSONObject getCurrentWeatherAsJson() throws IOException {
        
        // Variables
        String fullUrl = WG_URL + wundergroundApiKey + "/conditions/q/" + 
                wundergroundStateIdentifier + "/" + wundergroundCity + ".json";
        
        return JsonReader.readJsonFromUrl(fullUrl);
    }
    
}
