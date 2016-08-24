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
* Project File: DeviceConfigUtils.java
* Project Description: Utility files for smart mirror configs
* @author Joshua Lyons (josh@lyonsdensoftware.com)
* @version 0.0.1
*************************************************************************/

package com.lyonsdensoftware.config;

// IMPORTS
import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.json.stream.JsonGenerator;

public final class DeviceConfigUtils {
    private static String deviceConfigName = DeviceConfig.FILE_NAME;

    /**
     * Reads the {@link DeviceConfig} from disk.
     *
     * @return The configuration.
     */
    public static DeviceConfig readConfigFile() {
        return readConfigFile(DeviceConfig.FILE_NAME);
    }

    /**
     * Reads the {@link DeviceConfig} from disk. Pass in the name of the config file
     *
     * @return The configuration.
     */
    public static DeviceConfig readConfigFile(String filename) {
        FileInputStream file = null;
        try {
            deviceConfigName = filename.trim();
            file = new FileInputStream(deviceConfigName);
            JsonReader json = Json.createReader(file);
            JsonObject configObject = json.readObject();

            JsonObject wundergroundObject =
                    configObject.getJsonObject("wundergroundApi");
            String wundergroundApiKey = wundergroundObject.getString("wundergroundApiKey");
            String wundergroundStateIdentifier = wundergroundObject.getString("wundergroundStateIdentifier");
            String wundergroundCity = wundergroundObject.getString("wundergroundCity");
                        

            String productId = configObject.getString("productId");
            String dsn = configObject.getString("dsn");
            
            JsonObject pythonTCPSettings = configObject.getJsonObject("pythonTCPSettings");
            String hostname = pythonTCPSettings.getString("hostname");
            int port = pythonTCPSettings.getInt("port");
            

            DeviceConfig deviceConfig = new DeviceConfig(wundergroundApiKey, wundergroundStateIdentifier, 
                wundergroundCity, productId, dsn, hostname, port);

            return deviceConfig;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(
                    "The required file " + deviceConfigName + " could not be opened.", e);
        } finally {
            IOUtils.closeQuietly(file);
        }
    }

    /**
     * Writes the {@link DeviceConfig} back to disk.
     *
     * @param config
     */
    public static void updateConfigFile(DeviceConfig config) {
        FileOutputStream file = null;
        try {
            file = new FileOutputStream(deviceConfigName);
            StringWriter stringWriter = new StringWriter();

            Map<String, Object> properties = new HashMap<String, Object>(1);
            properties.put(JsonGenerator.PRETTY_PRINTING, true);

            JsonWriterFactory writerFactory = Json.createWriterFactory(properties);
            JsonWriter jsonWriter = writerFactory.createWriter(stringWriter);
            jsonWriter.writeObject(config.toJson());
            jsonWriter.close();

            // We have to write to a separate StringWriter and trim() it because the pretty-printing
            // generator adds a newline at the beginning of the file.
            file.write(stringWriter.toString().trim().getBytes());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(
                    "The required file " + deviceConfigName + " could not be updated.", e);
        } catch (IOException e) {
            throw new RuntimeException(
                    "The required file " + deviceConfigName + " could not be updated.", e);
        } finally {
            IOUtils.closeQuietly(file);
        }
    }
}
