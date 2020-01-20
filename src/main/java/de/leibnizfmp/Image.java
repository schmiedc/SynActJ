package de.leibnizfmp;

import ij.IJ;
import ij.ImagePlus;
import ij.measure.Calibration;
import ij.plugin.ZProjector;

import java.io.File;

/**
 * this class implements the image that is analyzed
 * an image has a source directory, px size unit, frame unit
 * a px size and a frame rate
 *
 * @author christopher schmied
 * @version 1.0.0
 */
public class Image {

    /**
     * directory : String, source directory
     */
    private String directory;

    /**
     * sizeUnit : String, unit of pixel size
     */
    private String sizeUnit;

    /**
     * timeUnit : String, unit of frame rate
     */
    private String timeUnit;

    /**
     * pxSizeCalib : double, pixel size
     */
    private double pxSizeCalib;

    /**
     * frameRateCalib : double, frame rate
     */
    private double frameRateCalib;

    /**
     * opens the image using the ImageJ default opener
     *
     * @param inputFile name of image
     * @return and ImagePlus object
     */
    ImagePlus openImage(String inputFile) {

        // open a example pHlorin image
        IJ.log("Opening file: " + inputFile);
        ImagePlus image = IJ.openImage(directory + File.separator + inputFile);
        image.setTitle(inputFile);

        return image;
    }

    /**
     * sets the calibration of the image
     *
     * @return the scale/calibration of the image
     */
    Calibration calibrate(){

        Calibration imageScale = new Calibration();
        imageScale.setTimeUnit(timeUnit);
        imageScale.setXUnit(sizeUnit);
        imageScale.setYUnit(sizeUnit);

        imageScale.pixelHeight = pxSizeCalib;
        imageScale.pixelWidth = pxSizeCalib;
        imageScale.frameInterval = frameRateCalib;

        return imageScale;

    }

    /**
     * performs a ZProjection
     * @param image on the specified image
     * @param method using the selected method
     * @return projected ImagePlus
     */
    ImagePlus projectImage(ImagePlus image, String method) {

        return ZProjector.run(image, method);

    }

    /**
     * converts SI unit size into pixels
     *
     * @param pxSize in micron
     * @param size specified size
     * @return size in pixel
     */
    static int calculateSizePx(Double pxSize, Double size) {

        double pxArea = pxSize * pxSize;

        return (int)Math.round(size / pxArea);

    }

    /**
     * Image constructor
     *
     * @param inputDir source directory
     * @param pxSize pixel size in micron
     * @param frameRate frame rate in seconds
     */
    public Image(String inputDir, double pxSize, double frameRate){

        directory = inputDir;
        sizeUnit = "micron";
        timeUnit = "sec";
        pxSizeCalib = pxSize;
        frameRateCalib = frameRate;

    }

}
