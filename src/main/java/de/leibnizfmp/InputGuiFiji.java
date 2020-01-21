package de.leibnizfmp;

import ij.IJ;
import fiji.util.gui.GenericDialogPlus;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

class InputGuiFiji {

    private static File inputDirectory = null;
    private static File outputDirectory = null;
    private static File settingsFile = null;

    String defaultInputDirectory;
    String defaultOutputDirectory;
    String defaultSettingsFile;

    InputGuiFiji() {

        defaultInputDirectory = "Choose Directory";
        defaultOutputDirectory = "Choose Directory";
        defaultSettingsFile = "Choose a File or leave empty";

    }

    InputGuiFiji(String InputDir, String OutputDir, String SettingsFile ) {

        defaultInputDirectory = InputDir;
        defaultOutputDirectory = OutputDir;
        defaultSettingsFile = SettingsFile;

    }

    /**
     * checks inputString for trailing slash if not adds the file separator to it
     *
     * @param inputString input string
     * @return input string with trailing slash for OS
     */
    private static String checkTrailingSlash(String inputString) {

        return inputString.endsWith(File.separator) ? inputString : inputString + File.separator;
    }

    /**
     * creates the setupGui dialog in the beginning
     */
    void createWindow() {

        GenericDialogPlus gdPlus = new GenericDialogPlus("Setup dialog");

        gdPlus.addDirectoryField("Input directory: ", defaultInputDirectory, 50);
        gdPlus.addDirectoryField("Output directory: ", defaultOutputDirectory, 50);
        gdPlus.addFileField("Settings File", defaultSettingsFile, 50);

        gdPlus.showDialog();

        // when canceled is pressed
        if ( gdPlus.wasCanceled() ) {

            System.out.println("Processing canceled");

        } else {

            inputDirectory = new File( defaultInputDirectory = gdPlus.getNextString() );
            outputDirectory = new File( defaultOutputDirectory = gdPlus.getNextString() );
            settingsFile = new File( defaultSettingsFile = gdPlus.getNextString() );

            // display error message if there is no input and output directory
            if (!inputDirectory.exists() || !outputDirectory.exists()) {

                System.out.println("No valid input and/or output directory selected");
                IJ.error("Please choose a valid input and/or output directory!");

                //reshow setup dialog
                this.createWindow();

            } else {

                System.out.println("Proceed with preview");
                System.out.println("Input Directory: " + inputDirectory);
                System.out.println("Output Directory: " + outputDirectory);
                System.out.println("Settings File: " + settingsFile);

                FileList getFileList = new FileList();

                String inputFileString = inputDirectory.toString();
                String outputFileString = outputDirectory.toString();

                // generates the file list that is fed to the preview GUI
                ArrayList<String> fileList = getFileList.getFileList(checkTrailingSlash(inputFileString));

                if ( settingsFile != null && settingsFile.exists() ) {

                    String settingsFileString = settingsFile.toString();

                    IJ.log("Found xml settings file: " + settingsFileString);
                    XmlHandler readMyXml = new XmlHandler();

                    try {

                        // reads settings file
                        readMyXml.xmlReader(settingsFileString);

                        // Constructs the preview GUI with the loaded settings from the settings file
                        PreviewGui previewGui = new PreviewGui(checkTrailingSlash(inputFileString),
                                checkTrailingSlash(outputFileString),
                                fileList, readMyXml.readProjMethod, readMyXml.readSigmaLoG, readMyXml.readProminence,
                                readMyXml.readSigmaSpots, readMyXml.readRollingSpots,
                                readMyXml.readThresholdSpots, readMyXml.readSpotErosion,
                                readMyXml.readRadiusGradient,
                                readMyXml.readMinSizeSpot, readMyXml.readMaxSizeSpot,
                                readMyXml.readLowCirc,readMyXml.readHighCirc,
                                readMyXml.readSigmaBackground, readMyXml.readThresholdBackground,
                                readMyXml.readMinSizeBack, readMyXml.readMaxSizeBack,
                                readMyXml.readStimFrame, readMyXml.readCalibrationSetting,
                                readMyXml.readPxSizeMicron, readMyXml.readFrameRate
                        );

                        // instantiates previewGui
                        previewGui.setUpGui();

                    } catch (ParserConfigurationException ex) {

                        ex.printStackTrace();
                        IJ.log("ERROR: XML reader, Parser Configuration exception");
                        IJ.error("Please select a valid .xml or leave empty");
                        settingsFile = null;

                    } catch (IOException ex) {

                        ex.printStackTrace();
                        IJ.log("ERROR: XML reader, IOException");
                        IJ.error("Please select a valid .xml or leave empty");
                        settingsFile = null;

                    } catch (SAXException ex) {

                        ex.printStackTrace();
                        IJ.log("ERROR: XML reader, SAXException");
                        IJ.error("Please select a valid .xml or leave empty");
                        settingsFile = null;

                    }

                } else {

                    IJ.log("Did no find xml settings file using default values");

                    // constructs previewGui from default settings since no valid settings file was given
                    PreviewGui previewGui = new PreviewGui(checkTrailingSlash(inputFileString),
                            checkTrailingSlash(outputFileString), fileList);

                    // instantiates previewGui
                    previewGui.setUpGui();

                }

            }

        }

    }
}
