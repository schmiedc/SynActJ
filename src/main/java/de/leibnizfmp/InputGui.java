package de.leibnizfmp;

import ij.IJ;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

class InputGui {

    private static JTextField inputDir;
    private static JTextField outputDir;
    private static JTextField settingsFilePath;
    private static File inputFolder = null;
    private static File outputFolder = null;
    private static File settingsFile = null;
    private static JFrame frame;

    void createWindow() {

        frame = new JFrame("Setup dialog");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        createUI(frame);
        frame.setSize(560, 150);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

    }

    private void createUI(final JFrame frame) {

        JPanel panelChooser = new JPanel();
        LayoutManager layout = new FlowLayout();
        panelChooser.setLayout(layout);

        JPanel panelStarter = new JPanel();
        panelStarter.setLayout(layout);

        Box chooserBox = new Box(BoxLayout.Y_AXIS);

        JLabel inputLabel = new JLabel("Input directory: ");
        inputLabel.setPreferredSize(new Dimension(130,inputLabel.getMinimumSize().height));
        inputDir = new JTextField("Choose Directory");
        inputDir.setPreferredSize(new Dimension(300, inputDir.getMinimumSize().height));
        JButton inputButton = new JButton("Choose");
        inputButton.addActionListener(new InputListener());

        Box boxInput = createInputDialog(inputButton, inputLabel, inputDir);
        chooserBox.add(boxInput);

        JLabel outputLabel = new JLabel("Output directory: ");
        outputLabel.setPreferredSize(new Dimension(130, outputLabel.getMinimumSize().height));
        outputDir = new JTextField("Choose Directory");
        outputDir.setPreferredSize(new Dimension(300, outputDir.getMinimumSize().height));
        JButton outputButton = new JButton("Choose");
        outputButton.addActionListener(new OutputListener());

        Box boxOutput = createInputDialog(outputButton, outputLabel, outputDir);
        chooserBox.add(boxOutput);

        JLabel settingsLabel = new JLabel("Settings file: ");
        settingsLabel.setPreferredSize(new Dimension(130, settingsLabel.getMinimumSize().height));
        settingsFilePath = new JTextField("Choose a File");
        settingsFilePath.setPreferredSize(new Dimension(300, settingsFilePath.getMinimumSize().height));
        JButton settingButton = new JButton("Choose");
        settingButton.addActionListener(new SettingsListener());

        Box boxSettings = createInputDialog(settingButton, settingsLabel, settingsFilePath);
        chooserBox.add(boxSettings);

        panelChooser.add(chooserBox);

        JButton previewButton = new JButton("Start Preview");
        previewButton.addActionListener(new PreviewListener());

        panelStarter.add(previewButton);

        frame.getContentPane().add(panelStarter, BorderLayout.SOUTH);
        frame.getContentPane().add(panelChooser, BorderLayout.CENTER);

    }

    private Box createInputDialog(JButton button,JLabel label, JTextField directory){

        Box box= new Box(BoxLayout.X_AXIS);
        box.add(label);
        box.add(directory);
        box.add(button);

        return box;

    }

    private static String checkTrailingSlash(String inputString) {

        return inputString.endsWith("/") ? inputString : inputString + "/";
    }

    public static class InputListener extends Component implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            JFileChooser inputFileChooser = new JFileChooser();
            inputFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            int option = inputFileChooser.showOpenDialog(this);

            if (option == JFileChooser.APPROVE_OPTION) {

                inputFolder = inputFileChooser.getSelectedFile();
                inputDir.setText(String.valueOf(inputFolder));

            } else {

                System.out.println("This is not a directory");
                IJ.error("Please choose a valid directory!");

            }

        }
    }

    public static class OutputListener extends Component implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            JFileChooser outputfileChooser = new JFileChooser();
            outputfileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            int option = outputfileChooser.showOpenDialog(this);

            if (option == JFileChooser.APPROVE_OPTION) {

                outputFolder = outputfileChooser.getSelectedFile();
                outputDir.setText(String.valueOf(outputFolder));

            } else {
                IJ.error("Please choose a valid directory!");
            }

        }
    }

    public static class SettingsListener extends Component implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            FileNameExtensionFilter xmlfilter = new FileNameExtensionFilter(
                    "xml files (*.xml)", "xml");

            JFileChooser settingsFileChooser = new JFileChooser();
            settingsFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            settingsFileChooser.setFileFilter(xmlfilter);

            int option = settingsFileChooser.showOpenDialog(this);

            if (option == JFileChooser.APPROVE_OPTION) {

                settingsFile = settingsFileChooser.getSelectedFile();
                settingsFilePath.setText(String.valueOf(settingsFile));


            } else {

                settingsFile = null;
                settingsFilePath.setText("");
                IJ.error("Uses default values if no settings file is present");

            }

        }
    }

    public static class PreviewListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            IJ.log("Starting preview segmentation");

            if (inputFolder != null && outputFolder != null){

                FileList getFileList = new FileList();
                String inputFileString = inputFolder.toString();
                String outputFileString = outputFolder.toString();


                String newInputFile = checkTrailingSlash(inputFileString);
                IJ.log("Processing directory: " + newInputFile);

                String newOutputFile = checkTrailingSlash(outputFileString);
                IJ.log("Saving to directory: " + newOutputFile);

                ArrayList<String> fileList = getFileList.getFileList(newInputFile);

                if (settingsFile != null) {

                    String settingsFileString = settingsFile.toString();

                    IJ.log("Found xml settings file: " + settingsFileString);
                    XmlHandler readMyXml = new XmlHandler();

                    try {

                        readMyXml.xmlReader(settingsFileString);

                        PreviewGui previewGui = new PreviewGui(newInputFile, newOutputFile, fileList,
                                readMyXml.readProjMethod, readMyXml.readSigmaLoG, readMyXml.readProminence,
                                readMyXml.readSigmaSpots, readMyXml.readRollingSpots, readMyXml.readThresholdSpots, readMyXml.readRadiusGradient,
                                readMyXml.readMinSizeSpot, readMyXml.readMaxSizeSpot,readMyXml.readLowCirc,readMyXml.readHighCirc,
                                readMyXml.readSigmaBackground, readMyXml.readThresholdBackground,
                                readMyXml.readMinSizeBack, readMyXml.readMaxSizeBack,
                                readMyXml.readStimFrame, readMyXml.readCalibrationSetting, readMyXml.readPxSizeMicron, readMyXml.readFrameRate
                        );

                        frame.setVisible(false);
                        previewGui.setUpGui();

                    } catch (ParserConfigurationException ex) {

                        ex.printStackTrace();
                        IJ.log("ERROR: XML reader, input valid xml or leave empty");

                    } catch (IOException ex) {

                        ex.printStackTrace();
                        IJ.log("ERROR: XML reader, input valid xml or leave empty");

                    } catch (SAXException ex) {

                        ex.printStackTrace();
                        IJ.log("ERROR: XML reader, input valid xml or leave empty");

                    }

                } else {

                    IJ.log("Did no find xml settings file using default values");
                    PreviewGui previewGui = new PreviewGui(newInputFile, newOutputFile, fileList);

                    frame.setVisible(false);
                    previewGui.setUpGui();

                }
            } else {

                IJ.error("No valid folder for input or output directory selected");

            }

        }


    }

}
