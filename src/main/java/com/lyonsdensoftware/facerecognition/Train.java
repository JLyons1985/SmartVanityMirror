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
* Project File: Train.java
* Project Description: Trains the facial recognition software
* @author Joshua Lyons (josh@lyonsdensoftware.com)
* @version 0.0.1
*************************************************************************/
package com.lyonsdensoftware.facerecognition;

// IMPORT

import java.io.File;
import java.util.List;
import java.util.Vector;
import org.apache.commons.io.FilenameUtils;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;




public class Train {
    
    // CLASS VARIABLES
    private final String TRAINING_FOLDER = "/home/pi/faceRecTraining/"; // Path to the training folder
    private String[] filePaths;                                         // Path to the files requested
    private String[] labels;
    private int pos_count = 0, neg_count = 0, fileCount = 0;
    private final String NEGATIVE_LABEL = "2";
    //private FaceRecognizer model;
    
    
    // Constructor
    public Train()
    {
        // Variables
        File root = new File(TRAINING_FOLDER);
        filePaths = new String[root.listFiles().length];
        labels = new String[root.listFiles().length];
        List<Mat> faces = null; 
        List<String> labelsList = null;
        
        // Read through all negative images
        String startDirectory = TRAINING_FOLDER + "negative";
        walk(startDirectory, NEGATIVE_LABEL);       
        
        // Now we need to go through the peopleAllowed.json
        
        // Now do the actual training
        for (int i = 0; i <= fileCount; i ++) {
            faces.add(Imgcodecs.imread(filePaths[i], Imgcodecs.IMREAD_GRAYSCALE)); 
            labelsList.add(labels[i]);
        }
        
        //model = createEigenFaceRecognizer();
        
    }
    
    /**
     * Walks through the directory finding files
     * @param path Path to start looking
     * @param label Label to add to the labels
     */
    private void walk(String path, String label) {
        File root = new File (path);
        File[] fileList = root.listFiles();
        
        for (File f : fileList) {
            if ( f.isDirectory() ) {
                walk( f.getAbsolutePath(), label );
            }
            else { // Not a directory so check if it is the right filetype
                if (FilenameUtils.getExtension(f.getName()).equalsIgnoreCase("pgm")) {
                    // File is the correct type
                    filePaths[fileCount] = f.getAbsolutePath();
                    labels[fileCount] = label;
                    
                    if (label.equals(NEGATIVE_LABEL))
                        neg_count += 1;
                    else
                        pos_count += 1;
                    
                    // Increase file count
                    fileCount += 1; 
                }
            }
        }
    }
    
}
