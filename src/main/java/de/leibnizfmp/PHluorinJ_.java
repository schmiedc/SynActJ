/*
 * To the extent possible under law, the ImageJ developers have waived
 * all copyright and related or neighboring rights to this tutorial code.
 *
 * See the CC0 1.0 Universal license for details:
 *     http://creativecommons.org/publicdomain/zero/1.0/
 */

package de.leibnizfmp;

import ij.ImageJ;
import java.util.ArrayList;
import ij.plugin.PlugIn;


/**
 * pHluorin image analysis workflow
 *
 * @author Christopher Schmied
 */
public class PHluorinJ_ implements PlugIn {

	@Override
	public void run(String s) {

		String testDir = "/home/schmiedc/Desktop/Projects/pHluorinPlugin_TS/Input/";

		FileList getFileList = new FileList();

		ArrayList<String> fileList = getFileList.getFileList(testDir);

		for (String file : fileList) {
			System.out.println(file);
		}

		PreviewGui guiTest = new PreviewGui(fileList);
		guiTest.setUpGui();

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

		InputGui start = new InputGui();
		start.createWindow();

		// start imageJ
		//new ImageJ();

		//String testDir = "/home/schmiedc/Desktop/Projects/pHluorinPlugin_TS/Input/";

		//FileList getFileList = new FileList();

		//ArrayList<String> fileList = getFileList.getFileList(testDir);

		//for (String file : fileList) {
		//	System.out.println(file);
		//}

		//PreviewGui guiTest = new PreviewGui(fileList);
		//guiTest.setUpGui();

	}
}
