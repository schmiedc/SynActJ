package de.leibnizfmp;

import ij.IJ;
import ij.ImageJ;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

class InputGui {

    private static JTextField inputDir;
    private static JTextField outputDir;
    private static JTextField settingsDir;
    private static File inputFolder = null;
    private static File outputFolder = null;
    private static String inputFolderString;
    private static String outputFolderString;
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

        //JLabel settingsLabel = new JLabel("Settings file: ");
        JTextField settingsDir = new JTextField("Choose File");
        //JButton settingButton = new JButton("Choose");
        //settingButton.addActionListener(new SettingsListener());

        //Box boxSettings = createInputDialog(settingButton, settingsLabel, settingsDir);
        //chooserBox.add(boxSettings);

        panelChooser.add(chooserBox);

        JButton previewButton = new JButton("Start Preview");
        previewButton.addActionListener(new PreviewListener());

        //JButton batchButton = new JButton("Batch");
        //batchButton.addActionListener(new BatchListener());

        panelStarter.add(previewButton);
        //panelStarter.add(batchButton);

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

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

            int option = fileChooser.showOpenDialog(this);
            if (option == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                settingsDir.setText(String.valueOf(file));

            } else {
                IJ.error("Please choose a valid settings file!");
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

                new ImageJ();

                String newInputFile = checkTrailingSlash(inputFileString);
                IJ.log("Processing directory: " + newInputFile);

                String newOutputFile = checkTrailingSlash(outputFileString);
                IJ.log("Saving to directory: " + newOutputFile);
                frame.setVisible(false);

                ArrayList<String> fileList = getFileList.getFileList(newInputFile);
                PreviewGui guiTest = new PreviewGui(fileList, newInputFile, newOutputFile);
                guiTest.setUpGui();

            } else {

                IJ.error("No valid folder for input or output directory selected");

            }



        }


    }

    public static class BatchListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {


            if (inputFolder != null && outputFolder != null) {

                IJ.log("Starting batch segmentation");

            } else {

                IJ.error("No valid folder for input or output directory selected");

            }

        }
    }


}
