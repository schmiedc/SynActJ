package de.leibnizfmp;

import ij.IJ;
import ij.ImagePlus;
import ij.measure.Calibration;
import ij.plugin.ZProjector;

import java.io.File;

public class Image {

    String directory;
    String sizeUnit;
    String timeUnit;
    double pxSizeCalib;
    double frameRateCalib;

    public ImagePlus openImage(String inputFile) {

        // open a example pHlorin image
        System.out.println("Opening file: " + inputFile);
        ImagePlus image = IJ.openImage(directory + inputFile);

        return image;
    }

    public Calibration calibrate(){

        Calibration imageScale = new Calibration();
        imageScale.setTimeUnit(timeUnit);
        imageScale.setXUnit(sizeUnit);
        imageScale.setYUnit(sizeUnit);

        imageScale.pixelHeight = pxSizeCalib;
        imageScale.pixelWidth = pxSizeCalib;
        imageScale.frameInterval = frameRateCalib;

        return imageScale;

    }

    public ImagePlus projectImage(ImagePlus image, String method) {

        ImagePlus maxProjectImage = ZProjector.run(image, method);

        return maxProjectImage;

    }

    public Image(String inputDir, double pxSize, double frameRate){

        directory = inputDir;
        sizeUnit = "micron";
        timeUnit = "sec";
        pxSizeCalib = pxSize;
        frameRateCalib = frameRate;

    }

}
