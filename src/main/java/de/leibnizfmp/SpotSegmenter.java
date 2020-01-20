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

/**
 * class implements the marker controlled watershed for the spot segmentation
 */
class SpotSegmenter {

    /**
     * spot detection for creating the marker image
     *
     * @param diffImage difference image that contains the enhanced objects of interest
     * @param simgaLoG sigma for laplacian of gaussian
     * @param prominence for maximum detection
     * @return binary image containing the marker one pixel marks the spot
     */
    ByteProcessor detectSpots(ImagePlus diffImage, double simgaLoG, double prominence) {

        IJ.log("Applying a LoG filter with sigma: " + simgaLoG);
        ImagePlus logImage = ImageScience.computeLaplacianImage(simgaLoG, diffImage);

        IJ.log("Detecting Minima with prominence: " + prominence);
        ImageProcessor getMaxima = logImage.getProcessor().convertToFloatProcessor();

        getMaxima.invert();
        MaximumFinder maxima = new MaximumFinder();
        ByteProcessor selection = maxima.findMaxima(getMaxima, prominence, 0, false);
        IJ.log("Spot detection finished");

        return selection;

    }

    /**
     * segments the spot area using a global intensity threshold
     *
     * @param image difference image
     * @param gauss sigma for gaussian blur
     * @param rolling radius for rolling ball background subtraction
     * @param threshold global intensity based threshold for segmentation
     * @param spotErosion if the generated mask should be eroded or not
     * @return binary image the contains the segmented spot area
     */
    ByteProcessor segmentSpots(ImagePlus image, double gauss, double rolling, String threshold, boolean spotErosion){

        ImageProcessor processImage = image.getProcessor().convertToShortProcessor();

        IJ.log("Applying Guassian blur with sigma: " + gauss);
        processImage.blurGaussian(gauss);

        IJ.log("Background subtraction with radius: " + rolling);
        BackgroundSubtracter backSubtract = new BackgroundSubtracter();
        backSubtract.rollingBallBackground(processImage, rolling, false, false,
                false, false, false);

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

    /**
     * generates the gradient image and performs the marker controlled watershed
     *
     * @param inputImage difference image
     * @param marker binary image marking the single spots
     * @param mask binary image containing the segmented spots
     * @param radius for the generating the gradient image
     * @return binary image containing the mask for the separated spots
     */
    ImagePlus watershed(ImagePlus inputImage, ByteProcessor marker, ByteProcessor mask, int radius){



        // generates the gradient image for the watershed
        IJ.log("Generating gradient image...");
        Strel strel = Strel.Shape.DISK.fromRadius( radius );
        ImageProcessor extGradient = externalGradient(inputImage.getProcessor(), strel);

        ImagePlus extImage = new ImagePlus("input", extGradient);
        ImagePlus markerImage = new ImagePlus("marker", marker);
        ImagePlus maskImage = new ImagePlus("mask", mask);

        IJ.log("Performing watershed object separation...");
        ImagePlus marker1 = BinaryImages.componentsLabeling(markerImage, 8, 32);
        ImagePlus resultImage = Watershed.computeWatershed(extImage, marker1, maskImage, 8, true );

        BinaryImages.removeLargestRegion(resultImage);
        IJ.log("Watershed finished.");
        return resultImage;
    }
}
