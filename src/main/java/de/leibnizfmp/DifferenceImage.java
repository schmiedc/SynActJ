package de.leibnizfmp;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.ImageCalculator;
import ij.plugin.ZProjector;

/**
 * This class creates a difference image to enhance objects in the movie
 * that change their intensity upon electro stimulation
 * A projection after the stimulation is divided by the projection before the stimulation.
 *
 * @author christopher schmied
 * @version 1.0.0
 */
class DifferenceImage {

    private String projMethod;
    private int frameUp;
    private int frameDown;

    /**
     * enhances objects that change intensity in movie
     *
     * @param image movie
     * @param stimulationFrame time point where stimulation happens
     * @return difference image
     */
    ImagePlus createDiffImage(ImagePlus image, int stimulationFrame) {

        int startBefore = stimulationFrame - frameDown;
        int endBefore = stimulationFrame - 1;
        int startAfter = stimulationFrame;
        int endAfter = stimulationFrame + frameUp;

        // perform z projections before and after and create difference image
        IJ.log("Creating difference image...");
        ImagePlus impBefore = ZProjector.run(image,projMethod,startBefore,endBefore);
        ImagePlus impAfter = ZProjector.run(image,projMethod,startAfter,endAfter);
        ImageCalculator ic = new ImageCalculator();
        ImagePlus impDiff = ic.run("Divide create 32-bit", impAfter, impBefore );
        IJ.log("Difference image created.");

        return impDiff;

    }

    /**
     * Difference image constructor
     *
     * @param method projection method
     */
    DifferenceImage(String method){

        projMethod = method;
        frameUp = 4;
        frameDown = 4;
        IJ.log("Projection Method set to: " + projMethod);

    }

}
