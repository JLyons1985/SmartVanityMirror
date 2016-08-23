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


import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_face.*;
import static org.bytedeco.javacpp.opencv_highgui.*;
import static org.bytedeco.javacpp.opencv_imgcodecs.imread;
import static org.bytedeco.javacpp.opencv_imgproc.*;



public class Train {
    
    // CLASS VARIABLES
    private final String TRAINING_FOLDER = "/home/pi/faceRecTraining/"; // Path to the training folder
    private final String TRAINING_FILE = TRAINING_FOLDER + "training.xml";
    private final String MEAN_FILE = TRAINING_FOLDER + "mean.png";
    private final String POSITIVE_EIGENFACE_FILE = TRAINING_FOLDER + "positive_eigenface.png";
    private final String NEGATIVE_EIGENFACE_FILE = TRAINING_FOLDER + "negative_eigenface.png";
    private String[] filePaths;                                         // Path to the files requested
    private int[]  labels;
    private int pos_count = 0, neg_count = 0, fileCount = 0;
    private final int NEGATIVE_LABEL = 2;
    private FaceRecognizer model;
    private final int FACE_WIDTH  = 92, FACE_HEIGHT = 112;
    
    
    // Constructor
    public Train()
    {
        // Variables
        File root = new File(TRAINING_FOLDER);
        filePaths = new String[root.listFiles().length];
        //labels = new Vector[root.listFiles().length];
        MatVector faces = null; 
        Mat labelsList = new Mat(labels);
        
        // Read through all negative images
        String startDirectory = TRAINING_FOLDER + "negative";
        walk(startDirectory, NEGATIVE_LABEL);       
        
        // Now we need to go through the peopleAllowed.json
        
        // Now do the actual training
        for (int i = 0; i <= fileCount; i ++) {
            faces.put(prepareImage(filePaths[i])); 
            //labelsList.(labels[i]);
        }
        
        model = createEigenFaceRecognizer();
        model.train(faces, labelsList);
        model.save(TRAINING_FILE);
        
        //Mat mean = model.getMat();
        
    }
    
    private Mat prepareImage(String filename) {
        Mat newImage = new Mat();
        Size mySize = new Size(FACE_WIDTH, FACE_HEIGHT);
        resize(imread(filename, COLOR_BGRA2GRAY), 
                newImage, mySize);
        
        return newImage;
    }
    
    /**
     * Walks through the directory finding files
     * @param path Path to start looking
     * @param label Label to add to the labels
     */
    private void walk(String path, int label) {
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
                    
                    if (label == NEGATIVE_LABEL)
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
