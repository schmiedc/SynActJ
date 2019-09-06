package de.leibnizfmp;

import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.Overlay;
import ij.measure.Calibration;
import ij.plugin.Commands;

import ij.plugin.filter.ParticleAnalyzer;
import ij.plugin.frame.RoiManager;
import ij.process.ByteProcessor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;


public class PreviewGui {

    private String[] thresholdString = { "Default", "Huang", "IJ_IsoData", "Intermodes",
            "IsoData ", "Li", "MaxEntropy", "Mean", "MinError", "Minimum",
            "Moments", "Otsu", "Percentile", "RenyiEntropy", "Shanbhag",
            "Triangle","Yen",
    };

    public ArrayList<String> aListOfFiles;

    // creates the panel that contains the buttons boxlayout vertical aligned
    Box buttonBox = new Box(BoxLayout.Y_AXIS);

    // tabbed pane
    JTabbedPane tabbedPane = new JTabbedPane();

    // settings for spot segmentation
    JList list;
    SpinnerModel doubleSpinnerLoGSpot;
    SpinnerModel doubleSpinnerProminenceSpot;
    SpinnerModel doubleSpinnerGaussSpot;
    SpinnerModel intSpinnerRollingBallSpot;
    JComboBox thresholdListSpot;
    SpinnerModel intSpinnerGradient;
    SpinnerModel doubleSpinnerMinSize;
    SpinnerModel doubleSpinnerMaxSize;
    SpinnerModel doubleSpinnerLowCirc;
    SpinnerModel doubleSpinnerHighCirc;

    // settings for background segmentation
    SpinnerModel doubleSpinBack1;
    JComboBox thresholdListBack;
    SpinnerModel doubleSpinBack2;
    SpinnerModel doubleSpinBack3;

    // experimental settings
    SpinnerModel doubleSpinnerPixelSize;
    SpinnerModel doubleSpinnerFrameRate;
    SpinnerModel integerSpinnerStimulationFrame;


    private void setUpSpotTab() {

        // Setup Interactions for Segment Boutons
        Box boxSpotSeg = new Box(BoxLayout.Y_AXIS);

        // Spinner for some number input
        doubleSpinnerLoGSpot = new SpinnerNumberModel(0.5, 0.0,5.0, 0.1);
        String spinLabelSpot1 = "LoG sigma: ";
        String spinUnitSpot1 = "µm";
        Box spinSpot1 = addLabeledSpinnerUnit(spinLabelSpot1, doubleSpinnerLoGSpot, spinUnitSpot1);
        boxSpotSeg.add(spinSpot1);

        doubleSpinnerProminenceSpot = new SpinnerNumberModel(0.005, 0.0,1.000, 0.001);
        String spinLabelSpot2 = "Prominence - grayValue: ";
        String spinUnitSpot2 = "";
        Box spinSpot2 = addLabeledSpinnerUnit(spinLabelSpot2, doubleSpinnerProminenceSpot, spinUnitSpot2);
        boxSpotSeg.add(spinSpot2);

        doubleSpinnerGaussSpot = new SpinnerNumberModel(1.0, 0.0,10.0, 0.1);
        String spinLabelSpot3 = "Gauss sigma: ";
        String spinUnitSpot3 = "px";
        Box spinSpot3 = addLabeledSpinnerUnit(spinLabelSpot3, doubleSpinnerGaussSpot, spinUnitSpot3);
        boxSpotSeg.add(spinSpot3);

        intSpinnerRollingBallSpot = new SpinnerNumberModel(30, 0,100, 1);
        String spinLabelSpot4 = "RollingBall Radius: ";
        String spinUnitSpot4 = "px";
        Box spinSpot4 = addLabeledSpinnerUnit(spinLabelSpot4, intSpinnerRollingBallSpot, spinUnitSpot4);
        boxSpotSeg.add(spinSpot4);

        thresholdListSpot = new JComboBox(thresholdString);
        JLabel thresholdListSpotLabel  = new JLabel("Select threshold: ");
        Box thresholdListSpotBox= new Box(BoxLayout.X_AXIS);
        thresholdListSpot.setMaximumSize(new Dimension(Integer.MAX_VALUE, thresholdListSpot.getMinimumSize().height));
        thresholdListSpot.setSelectedIndex(15);
        thresholdListSpotBox.add(thresholdListSpotLabel);
        thresholdListSpotBox.add(thresholdListSpot);
        boxSpotSeg.add(thresholdListSpotBox);

        intSpinnerGradient = new SpinnerNumberModel(3,0,10,1);
        String spinLabelSpot5 = "Gradient radius: ";
        String spinUnitSpot5 = "px";
        Box spinSpot5 = addLabeledSpinnerUnit(spinLabelSpot5, intSpinnerGradient, spinUnitSpot5);
        boxSpotSeg.add(spinSpot5);

        doubleSpinnerMinSize = new SpinnerNumberModel(0.0, 0.0,10000, 0.1);
        String labelMinSize = "Minimum spot size: ";
        String unitMinSize = "µm²";
        Box minSizeBox = addLabeledSpinnerUnit(labelMinSize , doubleSpinnerMinSize, unitMinSize);
        boxSpotSeg.add(minSizeBox);

        doubleSpinnerMaxSize = new SpinnerNumberModel(1000, 0.0,10000, 0.1);
        String labelMaxSize = "Maximum spot size: ";
        String unitMaxSize = "µm²";
        Box maxSizeBox = addLabeledSpinnerUnit(labelMaxSize , doubleSpinnerMaxSize, unitMaxSize);
        boxSpotSeg.add(maxSizeBox);

        doubleSpinnerLowCirc = new SpinnerNumberModel(0.0, 0.0,1.0, 0.01);
        String labelLowCirc = "Minimum spot circ.: ";
        String unitLowCirc = "";
        Box lowCirc = addLabeledSpinnerUnit(labelLowCirc , doubleSpinnerLowCirc, unitLowCirc);
        boxSpotSeg.add(lowCirc);

        doubleSpinnerHighCirc = new SpinnerNumberModel(1.0, 0.0,1.0, 0.01);
        String labelHighCirc = "Minimum spot size: ";
        String unitHighCirc = "";
        Box highCirc = addLabeledSpinnerUnit(labelHighCirc , doubleSpinnerHighCirc , unitHighCirc );
        boxSpotSeg.add(highCirc);

        // Preview Button for Spot segmentation
        JButton previewSpot = new JButton("Preview");
        previewSpot.addActionListener(new MyPreviewSpotListener());
        boxSpotSeg.add(previewSpot);

        // create tabbed panes
        tabbedPane.addTab("Boutons", boxSpotSeg);
    }

    private void setUpBackTab() {

        // Setup Interactions for Segment Background
        Box boxBackground = new Box(BoxLayout.Y_AXIS);

        doubleSpinBack1 = new SpinnerNumberModel(4.0, 0.0,20.0, 1.0);
        String spinBackLabel1 = "Gauss sigma: ";
        String spinBackUnit1 = "px";
        Box spinnerBack1 = addLabeledSpinnerUnit(spinBackLabel1, doubleSpinBack1, spinBackUnit1);
        boxBackground.add(spinnerBack1);

        thresholdListBack = new JComboBox(thresholdString);
        JLabel thresholdListBackLabel  = new JLabel("Select threshold: ");
        Box thresholdListBackBox= new Box(BoxLayout.X_AXIS);
        thresholdListBack.setMaximumSize(new Dimension(Integer.MAX_VALUE, thresholdListBack.getMinimumSize().height));
        thresholdListBack.setSelectedIndex(8);
        thresholdListBackBox.add(thresholdListBackLabel);
        thresholdListBackBox.add(thresholdListBack);
        boxBackground.add(thresholdListBackBox);

        doubleSpinBack2 = new SpinnerNumberModel(0.0,0.0,1000000,10.0);
        String minSizeLabel = "Select minimum size: ";
        String minUnitLabel = "µm²";
        Box spinnerBack2 = addLabeledSpinnerUnit(minSizeLabel, doubleSpinBack2, minUnitLabel );
        boxBackground.add(spinnerBack2);

        doubleSpinBack3 = new SpinnerNumberModel(10000,0.0,1000000,10.0);
        String maxSizeLabel = "Select maximum size: ";
        String maxUnitLabel = "µm²";
        Box spinnerBack3 = addLabeledSpinnerUnit(maxSizeLabel, doubleSpinBack3, maxUnitLabel);
        boxBackground.add(spinnerBack3);

        // setup Buttons
        JButton previewButton = new JButton("Preview");
        previewButton.addActionListener(new MyPreviewBackListener());
        boxBackground.add(previewButton);

        tabbedPane.addTab("Background", boxBackground);

    }

    private void setUpSettingsTab(){

        // Setup Interactions for experimental settings
        Box boxSettings = new Box(BoxLayout.Y_AXIS);

        doubleSpinnerPixelSize = new SpinnerNumberModel(0.1620, 0.0000,1.0000, 0.0001);
        String pixelSizeLabel = "Pixel size: ";
        String pixelSizeUnit = "µm";
        Box boxPixelSize = addLabeledSpinnerUnit(pixelSizeLabel,doubleSpinnerPixelSize, pixelSizeUnit);
        boxSettings.add(boxPixelSize);

        doubleSpinnerFrameRate = new SpinnerNumberModel(2.0, 0.0,10.0, 1.0);
        String frameRateLabel = "Frame rate: ";
        String frameRateUnit = "s";
        Box boxFrameRate = addLabeledSpinnerUnit(frameRateLabel, doubleSpinnerFrameRate, frameRateUnit);
        boxSettings.add(boxFrameRate);

        integerSpinnerStimulationFrame = new SpinnerNumberModel(5, 0,10, 1);
        String stimulationFrameLabel = "Stimulation Frame: ";
        String stimulationFrameUnit = "";
        Box boxStimulationFrame = addLabeledSpinnerUnit(stimulationFrameLabel, integerSpinnerStimulationFrame, stimulationFrameUnit);
        boxSettings.add(boxStimulationFrame);

        tabbedPane.addTab("Settings", boxSettings);

    }

    private JScrollPane setUpFileList(ArrayList<String> aListOfFiles) {

        // setup List
        list = new JList(aListOfFiles.toArray());

        // create a new scroll pane
        JScrollPane scroller = new JScrollPane(list);

        // set scroller to use only vertical scrollbar
        scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

        // only 4 items visible
        list.setVisibleRowCount(10);
        // only 1 selection possible
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //list.addListSelectionListener(this);

        return scroller;
    }

    private void setUpButtons() {

        JButton batchButton = new JButton("Batch Process");
        batchButton.addActionListener(new MyBatchListener());
        buttonBox.add(batchButton);
    }

    public void setUpGui() {

        JFrame theFrame;

        // sets up the frame
        theFrame = new JFrame("pHluorin Processing");
        theFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        BorderLayout layout = new BorderLayout();
        JPanel background = new JPanel(layout);

        // creates margin between edges of the panel and where the components
        // are placed
        background.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        setUpSpotTab();
        setUpBackTab();
        setUpSettingsTab();
        JScrollPane scroller = setUpFileList(aListOfFiles);
        setUpButtons();

        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPane.setPreferredSize(new Dimension(320, 80));

        // setup Buttons
        JButton  saveButton = new JButton("Save settings");
        saveButton.addActionListener(new MySaveListener());

        // add boxes to panel and frame
        background.add(BorderLayout.WEST, tabbedPane);
        background.add(BorderLayout.EAST, buttonBox);
        background.add(BorderLayout.CENTER, scroller);
        background.add(BorderLayout.SOUTH, saveButton);
        theFrame.getContentPane().add(background);

        theFrame.setSize(900,400);
        theFrame.setVisible(true);

    }

    static protected Box addLabeledTextField(String label, JTextField field, String unit) {

        Box textBox = new Box(BoxLayout.X_AXIS);

        JLabel labelLabel  = new JLabel(label);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, field.getMinimumSize().height));
        JLabel unitLabel  = new JLabel(unit);
        unitLabel.setPreferredSize(new Dimension(30, unitLabel.getMinimumSize().height));

        textBox.add(labelLabel);
        textBox.add(field);
        textBox.add(unitLabel);

        return textBox;
    }

    private static Box addLabeledSpinnerUnit(String label,
                                             SpinnerModel model,
                                             String unit) {

        Box spinnerLabelBox = new Box(BoxLayout.X_AXIS);
        JLabel l1 = new JLabel(label);
        spinnerLabelBox.add(l1);

        JSpinner spinner = new JSpinner(model);
        l1.setLabelFor(spinner);
        spinnerLabelBox.add(spinner);
        spinner.setMaximumSize(new Dimension(Integer.MAX_VALUE, spinner.getMinimumSize().height));

        JLabel l2 = new JLabel(unit);
        l2.setPreferredSize(new Dimension(30, l2.getMinimumSize().height));
        spinnerLabelBox.add(l2);

        return spinnerLabelBox;
    }

    // Upon pressing the start button call buildTrackAndStart() method
    public class MyPreviewSpotListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            // test settings
            String testDir = "/home/schmiedc/Desktop/Projects/pHluorinPlugin_TS/Input/";
            String projMethod = "median";

            System.out.println("Starting preview for spot segmentation");

            // checks if there is a file selected
            int selectionChecker = list.getSelectedIndex();

            // if a file for preview is selected then proceed with preview
            if (selectionChecker != -1){

                String selectedFile = (String) list.getSelectedValue();
                System.out.println("Selected File: " + selectedFile);

                String[] openImage = WindowManager.getImageTitles();

                for (String image : openImage) {



                    System.out.println("Open image: " + image);

                }

                Double sigmaLoG = (Double) doubleSpinnerLoGSpot.getValue();
                System.out.println("LoG sigma: " + sigmaLoG);

                Double prominence = (Double) doubleSpinnerProminenceSpot.getValue();
                System.out.println("Prominence: " + prominence);

                Double sigmaSpots = (Double) doubleSpinnerGaussSpot.getValue();
                System.out.println("Gauss sigma: " + sigmaSpots);

                Integer rollingSpots = (Integer) intSpinnerRollingBallSpot.getValue();
                System.out.println("Rolling Ball radius: " + rollingSpots);

                String thresholdSpots = (String) thresholdListSpot.getSelectedItem();
                System.out.println("Threshold: " + thresholdSpots);

                Integer radiusGradient = (Integer) intSpinnerGradient.getValue();
                System.out.println("Gradient Radius: " + radiusGradient);

                Double minSizeMicron = (Double) doubleSpinnerMinSize.getValue();
                Double maxSizeMicron = (Double) doubleSpinnerMaxSize.getValue();
                System.out.println("Spots size from: " + minSizeMicron + " to " + maxSizeMicron + " µm²" );

                // calculate size in pixel
                Double pxSizeMicron = (Double) doubleSpinnerPixelSize.getValue();
                Double pxArea = pxSizeMicron * pxSizeMicron;
                Integer minSizePx = (int)Math.round(minSizeMicron / pxArea);
                Integer maxSizePx = (int)Math.round(maxSizeMicron  / pxArea);

                Double lowCirc = (Double) doubleSpinnerLowCirc.getValue();
                Double highCirc = (Double) doubleSpinnerHighCirc.getValue();
                System.out.println("Spots circ. from: " + lowCirc + " to " + highCirc);

                Double frameRate = (Double) doubleSpinnerFrameRate.getValue();

                Integer stimFrame = (Integer) integerSpinnerStimulationFrame.getValue();
                System.out.println("Stimulation frame: " + stimFrame);

                // start preview for spot segmentation
                Image previewImage = new Image( testDir, pxSizeMicron, frameRate );
                ImagePlus originalImage = previewImage.openImage(selectedFile);
                Calibration calibration = previewImage.calibrate();

                DifferenceImage processImage = new DifferenceImage(projMethod);
                ImagePlus diffImage = processImage.createDiffImage(originalImage, stimFrame);

                SpotSegmenter spot = new SpotSegmenter();
                ByteProcessor detectSpots = spot.detectSpots(diffImage, sigmaLoG, prominence);
                ByteProcessor segmentSpots = spot.segmentSpots(diffImage, sigmaSpots, rollingSpots, thresholdSpots);

                ImagePlus watershed = spot.watershed(diffImage, detectSpots, segmentSpots, radiusGradient);

                RoiManager manager = new RoiManager();
                ParticleAnalyzer analyzer = new ParticleAnalyzer(2048,0,null, minSizePx, maxSizePx, lowCirc, highCirc );
                analyzer.analyze(watershed);

                manager.moveRoisToOverlay(watershed);
                Overlay overlay = watershed.getOverlay();
                overlay.drawLabels(false);
                originalImage.setOverlay(overlay);
                originalImage.setCalibration(calibration);
                originalImage.setDisplayRange(100,200);
                originalImage.show();

                manager.reset();
                manager.close();

            } else {

                System.out.println("Please choose a file in the file list!");

            }

        }

    } // close inner class

    // Upon pressing the start button call buildTrackAndStart() method
    public class MyPreviewBackListener implements ActionListener {

        public void actionPerformed(ActionEvent a) {

            Commands.closeAll();

            // test settings
            String testDir = "/home/schmiedc/Desktop/Projects/pHluorinPlugin_TS/Input/";

            System.out.println("Starting preview for background segmentation");

            int selectionChecker = list.getSelectedIndex();

            if (selectionChecker != -1){

                // get values from fields
                String selectedFile = (String) list.getSelectedValue();
                System.out.println("Selected File: " + selectedFile);

                Double sigmaBackground = (Double) doubleSpinBack1.getValue();

                String thresholdBackground = (String) thresholdListBack.getSelectedItem();

                Double minSizeBack = (Double) doubleSpinBack2.getValue();
                Double maxSizeBack  = (Double) doubleSpinBack3.getValue();
                System.out.println("Background size from: " + minSizeBack + " to " + maxSizeBack + " µm²" );

                // calculate size in pixel
                Double pxSizeMicron = (Double) doubleSpinnerPixelSize.getValue();
                Double pxArea = pxSizeMicron * pxSizeMicron;
                Integer minSizePx = (int)Math.round(minSizeBack / pxArea);
                Integer maxSizePx = (int)Math.round(maxSizeBack  / pxArea);

                Double frameRate = (Double) doubleSpinnerFrameRate.getValue();

                // segment background and show for validation
                Image previewImage = new Image( testDir, pxSizeMicron, frameRate );
                ImagePlus originalImage = previewImage.openImage(selectedFile);
                Calibration calibration = previewImage.calibrate();
                String titleOriginal = originalImage.getTitle();

                ImagePlus forBackSegmentation = previewImage.projectImage(originalImage, "max");

                BackgroundSegmenter back = new BackgroundSegmenter();
                ByteProcessor background = back.segmentBackground(forBackSegmentation, sigmaBackground, thresholdBackground);

                RoiManager manager = new RoiManager();
                ParticleAnalyzer backAnalyzer = new ParticleAnalyzer(2048,0,null, minSizePx, maxSizePx);

                ImagePlus testBack = new ImagePlus("test", background);
                backAnalyzer.analyze(testBack);

                manager.moveRoisToOverlay(testBack);
                Overlay overlay = testBack.getOverlay();
                overlay.drawLabels(false);

                ImagePlus showBack = previewImage.projectImage(originalImage, "max");
                showBack.setOverlay(overlay);
                showBack.setTitle(titleOriginal);
                showBack.setCalibration(calibration);
                showBack.setDisplayRange(100,200);
                showBack.show();

                manager.reset();
                manager.close();

            } else {

            System.out.println("Please choose a file in the file list!");

            }

        }

    } // close inner class

    public static class MySaveListener implements ActionListener {
        public void actionPerformed(ActionEvent a) { System.out.println("Saving settings");}
    }

    // upon pressing the stop button call sequencer.stop() method
    public static class MyBatchListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            System.out.println("Starting batch");
        }
    } // close inner class

    public PreviewGui (ArrayList<String> filesSelected){

        aListOfFiles = filesSelected;

    }



}