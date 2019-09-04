/*
 * To the extent possible under law, the ImageJ developers have waived
 * all copyright and related or neighboring rights to this tutorial code.
 *
 * See the CC0 1.0 Universal license for details:
 *     http://creativecommons.org/publicdomain/zero/1.0/
 */

package de.leibnizfmp;
import java.util.ArrayList;

import ij.ImageJ;
import ij.plugin.PlugIn;


/**
 * A template for processing each pixel of either
 * GRAY8, GRAY16, GRAY32 or COLOR_RGB images.
 *
 * @author Johannes Schindelin
 */
public class WorkflowStarter implements PlugIn {

	@Override
	public void run(String s) {

	}

	public void askInputOutputSettings() {
		// gui that asks input, output, settings and how to process

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

		new ImageJ();

		String testDir = "/home/schmiedc/Desktop/Projects/pHluorinPlugin_TS/Input/";

		PreviewGui guiTest = new PreviewGui();

		FileList getFileList = new FileList();
		ArrayList<String> fileList = getFileList.getFileList(testDir);

		for (String file : fileList) {
			System.out.println(file);
		}

		guiTest.setUpGui(fileList);

	}
}
