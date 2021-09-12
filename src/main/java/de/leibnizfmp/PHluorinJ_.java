/*
 * To the extent possible under law, the ImageJ developers have waived
 * all copyright and related or neighboring rights to this tutorial code.
 *
 * See the CC0 1.0 Universal license for details:
 *     http://creativecommons.org/publicdomain/zero/1.0/
 */

package de.leibnizfmp;

import ij.IJ;

import java.util.ArrayList;

import ij.ImageJ;
import ij.plugin.PlugIn;

/**
 * ImageJ plugin for the analysis of vesicle release and recycling dynamics
 * using the Synapto-pHluorin method
 * <p>
 * Expected input files are tif files containing movies of cultured neurons
 * In one frame an electro stimulation has been applied to the cultures
 * The active neurons that show intensity change upon stimulation and are enhanced using a difference image
 * Synaptic boutons are segmented using a seeded watershed
 * fluorescent intensity traces over time are extracted to measure synaptic
 * vesicle release and recycling dynamics
 * </p>
 * @author Christopher Schmied,
 * @version 1.0.0
 */
public class PHluorinJ_ implements PlugIn {

	@Override
	public void run(String s) {

		//System.setProperty("scijava.log.level", "debug");
		IJ.log("Starting pHlourin plugin");
		//InputGui start = new InputGui();
		//start.createWindow();

		InputGuiFiji start = new InputGuiFiji();
		start.createWindow();

	}

	/**
	 * Main method for debugging.
	 *
	 * For debugging, it is convenient to have a method that starts ImageJ, loads
	 * an image and calls the plugin, e.g. after setting breakpoints.
	 *
	 * @param args unused
	 */
	public static void main(String[] args) throws Exception {
		// set the plugins.dir property to make the plugin appear in the Plugins menu
		// see: https://stackoverflow.com/a/7060464/1207769
		Class <?> main_class = PHluorinJ_.class;
		java.net.URL url = main_class.getProtectionDomain().getCodeSource().getLocation();
		java.io.File fileDir = new java.io.File(url.toURI());
		System.setProperty("plugins.dir", fileDir.getAbsolutePath());
		//IJ.runPlugIn(PHluorinJ_.class.getName(),"");

		System.setProperty("scijava.log.level", "debug");
		//InputGui start = new InputGui();
		//start.createWindow();

		// start imageJ
		new ImageJ();

		// show something in the status bar
		String testInDir = "/home/schmiedc/Desktop/Projects/pHlorin_Review_TS/New TestIn/";
		String testOutDir = "/home/schmiedc/Desktop/Projects/pHlorin_Review_TS/New TestOut/";
		String settings = "Load setting .xml or leave empty";

		FileList getFileList = new FileList();
		ArrayList<String> fileList = getFileList.getFileList(testInDir);

		for (String file : fileList) {
			System.out.println(file);
		}

		InputGuiFiji setup = new InputGuiFiji(testInDir, testOutDir, settings);
		setup.createWindow();

		//PreviewGui guiTest = new PreviewGui(testDir, testOut, fileList);
		//guiTest.setUpGui();

		//InputGui guiTestFull = new InputGui();
		//guiTestFull.createWindow();

	}
}
