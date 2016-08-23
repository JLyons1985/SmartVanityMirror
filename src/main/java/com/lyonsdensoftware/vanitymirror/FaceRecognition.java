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
* Project File: FaceRecognition.java
* Project Description: Handles the facial recognition
* @author  Joshua Lyons (josh@lyonsdensoftware.com)
*************************************************************************/

package com.lyonsdensoftware.vanitymirror;

// IMPORTS
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.objdetect.CascadeClassifier;
import com.hopding.jrpicam.RPiCamera;


public class FaceRecognition {
    
    // CLASS VARIABLES
    private final String HAAR_FACES = getClass().getResource("/haarcascade_frontalface_alt.xml").getPath();
    
    public FaceRecognition() {
        // Load the native library.
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        
        //Highgui.imread("Test");
    }
    
    
}
