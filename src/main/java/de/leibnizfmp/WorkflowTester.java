package de.leibnizfmp;

import ij.ImagePlus;
import ij.process.ByteProcessor;

import java.util.ArrayList;

public class WorkflowTester {

    public static void main(String[] args) throws Exception {

        String testDir = "/home/schmiedc/Desktop/Projects/pHluorinPlugin_TS/Input/";
        String projMethod = "median";

        FileList getFileList = new FileList();
        ArrayList<String> fileList = getFileList.getFileList(testDir);

        for (String file : fileList) {
            System.out.println(file);
        }

        Image previewImage = new Image(testDir);
        ImagePlus originalImage = previewImage.openImage(fileList.get(0));

        DifferenceImage processImage = new DifferenceImage(projMethod);

        int startBefore = 1;
        int endBefore = 4;
        int startAfter = 5;
        int endAfter = 10;

        ImagePlus diffImage = processImage.createDiffImage(originalImage, startBefore, endBefore, startAfter, endAfter);


        SpotSegmenter spot = new SpotSegmenter();

        // sigma LoG now based on calibration size in µm
        double simgaLoG = 0.5;
        double prominence = 0.005;

        ByteProcessor detectSpots = spot.detectSpots(diffImage, simgaLoG, prominence);

        // segment spots
        double sigmaSpots = 1.0;
        double rollingSpots = 30.0;
        String thresholdSpots = "Triangle";

        ByteProcessor segmentSpots = spot.segmentSpots(diffImage, sigmaSpots, rollingSpots, thresholdSpots);

        // Watershed
        int radiusGradient = 3;

        ImagePlus watershed = spot.watershed(diffImage, detectSpots, segmentSpots, radiusGradient);

        ImagePlus forBackSegmentation = previewImage.projectImage(originalImage, "avg");

        // segment background
        double sigmaBackground = 4;
        double rollingBackground = 30;
        String thresholdBackground = "Mean";

        BackgroundSegmenter back = new BackgroundSegmenter();
        ByteProcessor background = back.segmentBackground(forBackSegmentation, sigmaBackground, rollingBackground, thresholdBackground);

        ImagePlus testBack = new ImagePlus("test", background);
        testBack.show();
        watershed.show();


    }

}
