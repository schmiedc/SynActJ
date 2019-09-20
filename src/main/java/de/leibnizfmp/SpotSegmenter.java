package de.leibnizfmp;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.BackgroundSubtracter;
import ij.plugin.filter.MaximumFinder;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import inra.ijpb.binary.BinaryImages;
import inra.ijpb.morphology.Strel;
import inra.ijpb.watershed.Watershed;
import trainableSegmentation.ImageScience;

import static inra.ijpb.morphology.Morphology.externalGradient;

public class SpotSegmenter {

    public ByteProcessor detectSpots(ImagePlus diffImage, double simgaLoG, double prominence) {

        IJ.log("Applying a LoG filter with sigma: " + Double.toString(simgaLoG));
        ImagePlus logImage = ImageScience.computeLaplacianImage(simgaLoG, diffImage);

        IJ.log("Detecting Minima with prominence: " + Double.toString(prominence));
        ImageProcessor getMaxima = logImage.getProcessor().convertToFloatProcessor();

        getMaxima.invert();
        MaximumFinder maxima = new MaximumFinder();
        ByteProcessor selection = maxima.findMaxima(getMaxima, prominence, 0, false);
        IJ.log("Spot detection finished");

        return selection;

    }

    public ByteProcessor segmentSpots(ImagePlus image, double gauss, double rolling, String threshold, boolean spotErosion){

        ImageProcessor processImage = image.getProcessor().convertToShortProcessor();

        IJ.log("Applying Guassian blur with sigma: " + Double.toString(gauss));
        processImage.blurGaussian(gauss);

        IJ.log("Background subtraction with radius: " + Double.toString(rolling));
        BackgroundSubtracter backSubtract = new BackgroundSubtracter();
        backSubtract.rollingBallBackground(processImage, rolling, false, false, false, false, false);

        IJ.log("Autothreshold with method: " + threshold);
        processImage.setAutoThreshold(threshold, true, 1);
        ByteProcessor result = processImage.createMask();

        if(spotErosion) {

            result.dilate();
            IJ.log("Applying an erosion to the mask");

        } else {

            IJ.log("No erosion is applied");

        }

        IJ.log("Spot segmentation finished.");
        // needs size and circ filter
        return result;

    }

    public ImagePlus watershed(ImagePlus inputImage, ByteProcessor marker, ByteProcessor mask, int radius){

        IJ.log("Performing watershed object separation...");
        Strel strel = Strel.Shape.DISK.fromRadius( radius );
        ImageProcessor extGradient = externalGradient(inputImage.getProcessor(), strel);
        ImagePlus extImage = new ImagePlus("input", extGradient);
        ImagePlus markerImage = new ImagePlus("marker", marker);
        ImagePlus maskImage = new ImagePlus("mask", mask);

        ImagePlus marker1 = BinaryImages.componentsLabeling(markerImage, 8, 32);
        ImagePlus resultImage = Watershed.computeWatershed(extImage, marker1, maskImage, 8, true );

        BinaryImages.removeLargestRegion(resultImage);
        IJ.log("Watershed finished.");
        return resultImage;
    }
}
