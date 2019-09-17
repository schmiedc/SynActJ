/*
 * To the extent possible under law, the ImageJ developers have waived
 * all copyright and related or neighboring rights to this tutorial code.
 *
 * See the CC0 1.0 Universal license for details:
 *     http://creativecommons.org/publicdomain/zero/1.0/
 */

package de.leibnizfmp;

import ij.IJ;
import ij.ImageJ;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import ij.plugin.PlugIn;
import org.scijava.Context;
import org.scijava.log.*;
import org.scijava.plugin.PluginInfo;

import static org.scijava.log.LogService.*;


/**
 * pHluorin image analysis workflow
 *
 * @author Christopher Schmied
 */
public class PHluorinJ_ implements PlugIn {

	@Override
	public void run(String s) {

		//System.setProperty("scijava.log.level", "debug");
		IJ.log("Starting pHlourin plugin");
		InputGui start = new InputGui();
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


		String testDir = "/home/schmiedc/Desktop/minimal-datasets_FMP/pHlorin_TS/Set2_888x880/";
		String testOut = "/home/schmiedc/Desktop/Projects/pHluorinPlugin_TS/Output/";

		FileList getFileList = new FileList();

		ArrayList<String> fileList = getFileList.getFileList(testDir);

		for (String file : fileList) {
			System.out.println(file);
		}

		PreviewGui guiTest = new PreviewGui(testDir, testOut, fileList);
		guiTest.setUpGui();

		//InputGui guiTestFull = new InputGui();
		//guiTestFull.createWindow();

	}
}
