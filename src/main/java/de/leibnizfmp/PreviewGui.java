package de.leibnizfmp;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.measure.Calibration;
import ij.plugin.Commands;
import org.scijava.util.ArrayUtils;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

public class PreviewGui extends JPanel{

    private String[] thresholdString = { "Default", "Huang", "IJ_IsoData", "Intermodes",
            "IsoData ", "Li", "MaxEntropy", "Mean", "MinError", "Minimum",
            "Moments", "Otsu", "Percentile", "RenyiEntropy", "Shanbhag",
            "Triangle","Yen",
    };

    private String inputDir;
    private String outputDir;
    private ArrayList<String> fileList;

    private String projMethod;

    private double sigmaLoG;
    private double prominence;
    private double sigmaSpots;
    private double rollingSpots;
    private String thresholdSpots;
    private int radiusGradient;
    private double minSizeSpot;
    private double maxSizeSpot;
    private double lowCirc;
    private double highCirc;

    private double sigmaBackground;
    private String thresholdBackground;
    private double minSizeBack;
    private double maxSizeBack;

    private boolean calibrationSetting;
    private int stimFrame;
    private double pxSizeMicron;
    private double frameRate;

    private boolean setDisplayRange = false;

    // creates the panel that contains the buttons boxlayout vertical aligned
    private JPanel buttonBox = new JPanel(new FlowLayout(FlowLayout.LEFT));

    // tabbed pane
    private JTabbedPane tabbedPane = new JTabbedPane();

    // settings for spot segmentation
    private JList list;
    private SpinnerModel doubleSpinnerLoGSpot;
    private SpinnerModel doubleSpinnerProminenceSpot;
    private SpinnerModel doubleSpinnerGaussSpot;
    private SpinnerModel doubleSpinnerRollingBallSpot;
    private JComboBox<String> thresholdListSpot;
    private SpinnerModel intSpinnerGradient;
    private SpinnerModel doubleSpinnerMinSize;
    private SpinnerModel doubleSpinnerMaxSize;
    private SpinnerModel doubleSpinnerLowCirc;
    private SpinnerModel doubleSpinnerHighCirc;

    // settings for background segmentation
    private SpinnerModel doubleSpinBack1;
    private JComboBox<String> thresholdListBack;
    private SpinnerModel doubleSpinBack2;
    private SpinnerModel doubleSpinBack3;

    // experimental settings
    private SpinnerModel doubleSpinnerPixelSize;
    private JCheckBox checkCalibration;
    private SpinnerModel doubleSpinnerFrameRate;
    private SpinnerModel integerSpinnerStimulationFrame;

    private Border blackline;

    private void setUpSpotTab() {

        // Setup Interactions for Segment Boutons
        Box boxSpotSeg = new Box(BoxLayout.Y_AXIS);

        //box with titled borders
        Box detectionBox = new Box(BoxLayout.Y_AXIS);
        TitledBorder titleDetection;
        blackline = BorderFactory.createLineBorder(Color.black);
        titleDetection = BorderFactory.createTitledBorder(blackline, "Detection: number of spots");
        detectionBox.setBorder(titleDetection);

        // Spinner for some number input
        doubleSpinnerLoGSpot = new SpinnerNumberModel(sigmaLoG, 0.0,5.0, 0.1);
        String spinLabelSpot1 = "LoG sigma: ";
        String spinUnitSpot1 = "µm";
        Box spinSpot1 = addLabeledSpinnerUnit(spinLabelSpot1, doubleSpinnerLoGSpot, spinUnitSpot1);
        detectionBox.add(spinSpot1);

        doubleSpinnerProminenceSpot = new SpinnerNumberModel(prominence, 0.0,1.0, 0.0001);
        String spinLabelSpot2 = "Prominence: ";
        String spinUnitSpot2 = "";
        Box spinSpot2 = addLabeledSpinner5Digit(spinLabelSpot2, doubleSpinnerProminenceSpot, spinUnitSpot2);
        detectionBox.add(spinSpot2);

        boxSpotSeg.add(detectionBox);

        // box with titled borders
        Box segmentationBox = new Box(BoxLayout.Y_AXIS);
        TitledBorder titleSegmentation;
        blackline = BorderFactory.createLineBorder(Color.black);
        titleSegmentation = BorderFactory.createTitledBorder(blackline, "Segmentation: size of spots");
        segmentationBox.setBorder(titleSegmentation);

        doubleSpinnerGaussSpot = new SpinnerNumberModel(sigmaSpots, 0.0,10.0, 0.1);
        String spinLabelSpot3 = "Gauss sigma: ";
        String spinUnitSpot3 = "px";
        Box spinSpot3 = addLabeledSpinnerUnit(spinLabelSpot3, doubleSpinnerGaussSpot, spinUnitSpot3);
        segmentationBox.add(spinSpot3);

        doubleSpinnerRollingBallSpot = new SpinnerNumberModel(rollingSpots, 0,100, 1);
        String spinLabelSpot4 = "RollingBall Radius: ";
        String spinUnitSpot4 = "px";
        Box spinSpot4 = addLabeledSpinnerUnit(spinLabelSpot4, doubleSpinnerRollingBallSpot, spinUnitSpot4);
        segmentationBox.add(spinSpot4);

        thresholdListSpot = new JComboBox<>(thresholdString);
        JLabel thresholdListSpotLabel  = new JLabel("Select threshold: ");
        Box thresholdListSpotBox= new Box(BoxLayout.X_AXIS);
        thresholdListSpot.setMaximumSize(new Dimension(Integer.MAX_VALUE, thresholdListSpot.getMinimumSize().height));

        // get index of selected threshold using arrayUtils
        int indexOfThreshold = ArrayUtils.indexOf(thresholdString, thresholdSpots);
        // set this index as selected in the threshold list
        thresholdListSpot.setSelectedIndex(indexOfThreshold);

        thresholdListSpotBox.add(thresholdListSpotLabel);
        thresholdListSpotBox.add(thresholdListSpot);
        segmentationBox.add(thresholdListSpotBox);

        boxSpotSeg.add(segmentationBox);

        // box with titled borders
        Box splittingBox = new Box(BoxLayout.Y_AXIS);
        TitledBorder titleSplitting;
        blackline = BorderFactory.createLineBorder(Color.black);
        titleSplitting = BorderFactory.createTitledBorder(blackline, "Spot splitter: separation of spots");
        splittingBox.setBorder(titleSplitting);

        intSpinnerGradient = new SpinnerNumberModel(radiusGradient,0,10,1);
        String spinLabelSpot5 = "Gradient radius: ";
        String spinUnitSpot5 = "px";
        Box spinSpot5 = addLabeledSpinnerUnit(spinLabelSpot5, intSpinnerGradient, spinUnitSpot5);
        splittingBox.add(spinSpot5);

        boxSpotSeg.add(splittingBox);

        // box with titled borders
        Box filterBox = new Box(BoxLayout.Y_AXIS);
        TitledBorder titleFilter;
        blackline = BorderFactory.createLineBorder(Color.black);
        titleFilter = BorderFactory.createTitledBorder(blackline, "Spot filter: size and circ.");
        filterBox.setBorder(titleFilter);

        doubleSpinnerMinSize = new SpinnerNumberModel(minSizeSpot, 0.0,10000, 0.1);
        String labelMinSize = "Minimum spot size: ";
        String unitMinSize = "µm²";
        Box minSizeBox = addLabeledSpinnerUnit(labelMinSize , doubleSpinnerMinSize, unitMinSize);
        filterBox.add(minSizeBox);

        doubleSpinnerMaxSize = new SpinnerNumberModel(maxSizeSpot, 0.0,10000, 0.1);
        String labelMaxSize = "Maximum spot size: ";
        String unitMaxSize = "µm²";
        Box maxSizeBox = addLabeledSpinnerUnit(labelMaxSize , doubleSpinnerMaxSize, unitMaxSize);
        filterBox.add(maxSizeBox);

        doubleSpinnerLowCirc = new SpinnerNumberModel(lowCirc, 0.0,1.0, 0.01);
        String labelLowCirc = "Minimum spot circ.: ";
        String unitLowCirc = "";
        Box lowCirc = addLabeledSpinnerUnit(labelLowCirc , doubleSpinnerLowCirc, unitLowCirc);
        filterBox.add(lowCirc);

        doubleSpinnerHighCirc = new SpinnerNumberModel(highCirc, 0.0,1.0, 0.01);
        String labelHighCirc = "Minimum spot size: ";
        String unitHighCirc = "";
        Box highCirc = addLabeledSpinnerUnit(labelHighCirc , doubleSpinnerHighCirc , unitHighCirc );
        filterBox.add(highCirc);

        boxSpotSeg.add(filterBox);

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

        // box with titled borders
        Box segmentationBox = new Box(BoxLayout.Y_AXIS);
        TitledBorder titleSegmentation;
        blackline = BorderFactory.createLineBorder(Color.black);
        titleSegmentation = BorderFactory.createTitledBorder(blackline, "Segmentation: size of background");
        segmentationBox.setBorder(titleSegmentation);

        doubleSpinBack1 = new SpinnerNumberModel(sigmaBackground, 0.0,20.0, 0.1);
        String spinBackLabel1 = "Gauss sigma: ";
        String spinBackUnit1 = "px";
        Box spinnerBack1 = addLabeledSpinnerUnit(spinBackLabel1, doubleSpinBack1, spinBackUnit1);
        segmentationBox.add(spinnerBack1);

        thresholdListBack = new JComboBox<>(thresholdString);
        JLabel thresholdListBackLabel  = new JLabel("Select threshold: ");
        Box thresholdListBackBox= new Box(BoxLayout.X_AXIS);
        thresholdListBack.setMaximumSize(new Dimension(Integer.MAX_VALUE, thresholdListBack.getMinimumSize().height));

        // get index of selected threshold using arrayUtils
        int indexOfThreshold = ArrayUtils.indexOf(thresholdString, thresholdBackground);
        // set this index as selected in the threshold list
        thresholdListBack.setSelectedIndex(indexOfThreshold);

        thresholdListBackBox.add(thresholdListBackLabel);
        thresholdListBackBox.add(thresholdListBack);
        segmentationBox.add(thresholdListBackBox);

        boxBackground.add(segmentationBox);

        // box with titled borders
        Box filterBox = new Box(BoxLayout.Y_AXIS);
        TitledBorder titleFilter;
        blackline = BorderFactory.createLineBorder(Color.black);
        titleFilter = BorderFactory.createTitledBorder(blackline, "Filter: size");
        filterBox .setBorder(titleFilter);

        doubleSpinBack2 = new SpinnerNumberModel(minSizeBack,0.0,1000000,10.0);
        String minSizeLabel = "Select min. size: ";
        String minUnitLabel = "µm²";
        Box spinnerBack2 = addLabeledSpinnerUnit(minSizeLabel, doubleSpinBack2, minUnitLabel );
        filterBox.add(spinnerBack2);

        doubleSpinBack3 = new SpinnerNumberModel(maxSizeBack,0.0,1000000,10.0);
        String maxSizeLabel = "Select max. size: ";
        String maxUnitLabel = "µm²";
        Box spinnerBack3 = addLabeledSpinnerUnit(maxSizeLabel, doubleSpinBack3, maxUnitLabel);
        filterBox.add(spinnerBack3);

        boxBackground.add(filterBox);

        // setup Buttons
        JButton previewButton = new JButton("Preview");
        previewButton.addActionListener(new MyPreviewBackListener());
        boxBackground.add(previewButton);

        tabbedPane.addTab("Background", boxBackground);

    }

    private void setUpSettingsTab(){

        // Setup Interactions for experimental settings
        Box boxSettings = new Box(BoxLayout.Y_AXIS);

        JLabel settingsLabel = new JLabel("Specify experimental Settings: ");
        buttonBox.add(settingsLabel);

        doubleSpinnerPixelSize = new SpinnerNumberModel(pxSizeMicron, 0.000,1.000, 0.001);
        String pixelSizeLabel = "Pixel size: ";
        String pixelSizeUnit = "µm";
        Box boxPixelSize = addLabeledSpinnerUnit(pixelSizeLabel,doubleSpinnerPixelSize, pixelSizeUnit);
        boxSettings.add(boxPixelSize);

        doubleSpinnerFrameRate = new SpinnerNumberModel(frameRate, 0.0,10.0, 1.0);
        String frameRateLabel = "Frame rate: ";
        String frameRateUnit = "s";
        Box boxFrameRate = addLabeledSpinnerUnit(frameRateLabel, doubleSpinnerFrameRate, frameRateUnit);
        boxSettings.add(boxFrameRate);

        checkCalibration = new JCheckBox("Override metadata?");
        checkCalibration.setSelected(calibrationSetting);
        boxSettings.add(checkCalibration);

        integerSpinnerStimulationFrame = new SpinnerNumberModel(stimFrame, 0,10, 1);
        String stimulationFrameLabel = "Stimulation Frame: ";
        String stimulationFrameUnit = "";
        Box boxStimulationFrame = addLabeledSpinnerUnit(stimulationFrameLabel, integerSpinnerStimulationFrame, stimulationFrameUnit);
        boxSettings.add(boxStimulationFrame);

        buttonBox.add(boxSettings);

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
        buttonBox.setPreferredSize(new Dimension(270, 100));

    }

    void setUpGui() {

        JFrame theFrame;

        // sets up the frame
        theFrame = new JFrame("pHluorin Processing");
        // needs to set to dispose otherwise it also closes Fiji
        theFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        BorderLayout layout = new BorderLayout();
        JPanel background = new JPanel(layout);

        // creates margin between edges of the panel and where the components
        // are placed
        background.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        setUpSpotTab();
        setUpBackTab();
        setUpSettingsTab();
        JScrollPane scroller = setUpFileList(fileList);
        setUpButtons();

        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPane.setPreferredSize(new Dimension(290, 100));

        // setup Buttons
        JButton  saveButton = new JButton("Save settings");
        saveButton.addActionListener(new MySaveListener());

        // add boxes to panel and frame
        background.add(BorderLayout.WEST, tabbedPane);
        background.add(BorderLayout.EAST, buttonBox);
        background.add(BorderLayout.CENTER, scroller);
        background.add(BorderLayout.SOUTH, saveButton);
        theFrame.getContentPane().add(background);

        theFrame.setSize(900,500);
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
        l1.setPreferredSize(new Dimension(150, l1.getMinimumSize().height));
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

    private static Box addLabeledSpinner5Digit(String label,
                                             SpinnerModel model,
                                             String unit) {

        Box spinnerLabelBox = new Box(BoxLayout.X_AXIS);
        JLabel l1 = new JLabel(label);
        l1.setPreferredSize(new Dimension(150, l1.getMinimumSize().height));
        spinnerLabelBox.add(l1);

        JSpinner spinner = new JSpinner(model);
        JSpinner.NumberEditor editor = (JSpinner.NumberEditor)spinner.getEditor();
        DecimalFormat format = editor.getFormat();
        format.setMinimumFractionDigits(4);
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

            ImagePlus originalImage;
            Calibration calibration;

            IJ.log("Starting preview for spot segmentation");

            // get all the values from the GUI
            double sigmaLoG = (Double) doubleSpinnerLoGSpot.getValue();
            IJ.log("LoG sigma: " + sigmaLoG);

            double prominence = (Double) doubleSpinnerProminenceSpot.getValue();
            IJ.log("Prominence: " + prominence);

            double sigmaSpots = (Double) doubleSpinnerGaussSpot.getValue();
            IJ.log("Gauss sigma: " + sigmaSpots);

            double rollingSpots = (Double) doubleSpinnerRollingBallSpot.getValue();
            IJ.log("Rolling Ball radius: " + rollingSpots);

            String thresholdSpots = (String) thresholdListSpot.getSelectedItem();
            IJ.log("Threshold: " + thresholdSpots);

            int radiusGradient = (Integer) intSpinnerGradient.getValue();
            IJ.log("Gradient Radius: " + radiusGradient);

            // calculate size in pixel
            double pxSizeMicron = (Double) doubleSpinnerPixelSize.getValue();
            boolean calibrationSetting = checkCalibration.isSelected();

            double minSizeMicron = (Double) doubleSpinnerMinSize.getValue();
            double maxSizeMicron = (Double) doubleSpinnerMaxSize.getValue();
            IJ.log("Spots size from: " + minSizeMicron + " to " + maxSizeMicron + " µm²" );

            double lowCirc = (Double) doubleSpinnerLowCirc.getValue();
            double highCirc = (Double) doubleSpinnerHighCirc.getValue();
            IJ.log("Spots circ. from: " + lowCirc + " to " + highCirc);

            double frameRate = (Double) doubleSpinnerFrameRate.getValue();

            int stimFrame = (Integer) integerSpinnerStimulationFrame.getValue();
            IJ.log("Stimulation frame: " + stimFrame);

            Image previewImage = new Image(inputDir, pxSizeMicron, frameRate);

            // checks if there is a file selected
            int selectionChecker = list.getSelectedIndex();

            // if a file for preview is selected then proceed with preview
            if (selectionChecker != -1){

                String selectedFile = (String) list.getSelectedValue();
                IJ.log("Selected File: " + selectedFile);

                // check if there are windows open already
                int openImages = WindowManager.getImageCount();

                // if there are image windows open check if they are of the list and of the selected image
                if  ( openImages != 0 ) {

                    IJ.log("There are images open!");

                    String[] openImage = WindowManager.getImageTitles();
                    ArrayList<String> openImageList = new ArrayList<>(Arrays.asList(openImage));

                    FileList fileUtility = new FileList();
                    ArrayList<String> openInputImages = fileUtility.intersection(openImageList, fileList);

                    boolean selectedFileChecker = false;

                    for (String image : openInputImages) {

                        if (image.equals(selectedFile)) {

                            IJ.log(selectedFile + " is already open");
                            selectedFileChecker = true;

                        } else {

                            IJ.selectWindow(image);
                            IJ.run("Close");

                        }

                    }

                    if (selectedFileChecker) {

                        IJ.log("The selected image is already open");

                        IJ.selectWindow(selectedFile);
                        setDisplayRange = false;
                        ImagePlus selectedImage = WindowManager.getCurrentWindow().getImagePlus();
                        calibration = selectedImage.getCalibration();

                        Double pxSizeFromImage = calibration.pixelWidth;
                        int minSizePx = Image.calculateMinSizePx(pxSizeFromImage, minSizeMicron);
                        int maxSizePx = Image.calculateMaxSizePx(pxSizeFromImage, maxSizeMicron);

                        SegmentationVisualization visualizer = new SegmentationVisualization();

                        visualizer.spotVisualization( selectedImage, projMethod, stimFrame, sigmaLoG, prominence,
                                sigmaSpots, rollingSpots, thresholdSpots,
                                radiusGradient, minSizePx, maxSizePx, lowCirc, highCirc,
                                calibration, setDisplayRange);


                    } else {

                        IJ.log("The selected image is not open");
                        IJ.log("Opening new image");

                        setDisplayRange = true;
                        // start preview for spot segmentation
                        originalImage = previewImage.openImage(selectedFile);

                        int minSizePx;
                        int maxSizePx;

                        if (calibrationSetting) {

                            calibration = previewImage.calibrate();
                            minSizePx = Image.calculateMinSizePx(pxSizeMicron, minSizeMicron);
                            maxSizePx = Image.calculateMaxSizePx(pxSizeMicron, maxSizeMicron);

                            IJ.log("Metadata will be overwritten.");
                            IJ.log("Pixel size set to: " + pxSizeMicron);
                            IJ.log("Frame rate set to: " + frameRate);


                        } else {

                            calibration = originalImage.getCalibration();
                            Double pxSizeFromImage = calibration.pixelWidth;
                            minSizePx = Image.calculateMinSizePx(pxSizeFromImage, minSizeMicron);
                            maxSizePx = Image.calculateMaxSizePx(pxSizeFromImage, maxSizeMicron);

                            IJ.log("Metadata will no be overwritten");

                        }

                        SegmentationVisualization visualizer = new SegmentationVisualization();

                        visualizer.spotVisualization(originalImage, projMethod, stimFrame, sigmaLoG, prominence,
                                sigmaSpots, rollingSpots, thresholdSpots,
                                radiusGradient, minSizePx, maxSizePx, lowCirc, highCirc,
                                calibration, setDisplayRange);

                    }

                } else {

                    IJ.log("There are no images open!");

                    // start preview for spot segmentation
                    setDisplayRange = true;
                    previewImage = new Image(inputDir, pxSizeMicron, frameRate );
                    originalImage = previewImage.openImage(selectedFile);

                    int minSizePx;
                    int maxSizePx;

                    if (calibrationSetting) {

                        calibration = previewImage.calibrate();
                        minSizePx = Image.calculateMinSizePx(pxSizeMicron, minSizeMicron);
                        maxSizePx = Image.calculateMaxSizePx(pxSizeMicron, maxSizeMicron);
                        IJ.log("Metadata will be overwritten.");
                        IJ.log("Pixel size set to: " + pxSizeMicron);
                        IJ.log("Frame rate set to: " + frameRate);


                    } else {

                        calibration = originalImage.getCalibration();
                        Double pxSizeFromImage = calibration.pixelWidth;
                        minSizePx = Image.calculateMinSizePx(pxSizeFromImage, minSizeMicron);
                        maxSizePx = Image.calculateMaxSizePx(pxSizeFromImage, maxSizeMicron);

                        IJ.log("Metadata will no be overwritten");

                    }

                    SegmentationVisualization visualizer = new SegmentationVisualization();

                    visualizer.spotVisualization(originalImage, projMethod, stimFrame, sigmaLoG, prominence,
                            sigmaSpots, rollingSpots, thresholdSpots,
                            radiusGradient, minSizePx, maxSizePx, lowCirc, highCirc,
                            calibration, setDisplayRange);
                }

            } else {

                IJ.error("Please choose a file in the file list!");

            }

        }

    } // close inner class

    // Upon pressing the start button call buildTrackAndStart() method
    public class MyPreviewBackListener implements ActionListener {

        public void actionPerformed(ActionEvent a) {

            Calibration calibration;

            IJ.log("Starting preview for background segmentation");

            Double sigmaBackground = (Double) doubleSpinBack1.getValue();
            IJ.log("Sigma background set to: " + sigmaBackground);

            String thresholdBackground = (String) thresholdListBack.getSelectedItem();
            IJ.log("Threshold for background set to: " + thresholdBackground);

            Double minSizeBack = (Double) doubleSpinBack2.getValue();
            Double maxSizeBack  = (Double) doubleSpinBack3.getValue();
            IJ.log("Background size from: " + minSizeBack + " to " + maxSizeBack + " µm²" );

            // calculate size in pixel
            boolean calibrationSetting = checkCalibration.isSelected();

            Double pxSizeMicron = (Double) doubleSpinnerPixelSize.getValue();
            Double frameRate = (Double) doubleSpinnerFrameRate.getValue();

            Image previewImage = new Image(inputDir, pxSizeMicron, frameRate);

            int selectionChecker = list.getSelectedIndex();

            if (selectionChecker != -1){

                String selectedFile = (String) list.getSelectedValue();
                IJ.log("Selected File: " + selectedFile);

                // check if there are windows open already
                int openImages = WindowManager.getImageCount();

                // if there are image windows open check if they are of the list and of the selected image
                if  ( openImages != 0 ) {

                    IJ.log("There are images open!");

                    String[] openImage = WindowManager.getImageTitles();
                    ArrayList<String> openImageList = new ArrayList<>(Arrays.asList(openImage));

                    FileList fileUtility = new FileList();
                    ArrayList<String> openInputImages = fileUtility.intersection(openImageList, fileList);

                    boolean selectedFileChecker = false;

                    for (String image : openInputImages) {

                        if (image.equals(selectedFile)) {

                            IJ.log(selectedFile + " is already open");
                            selectedFileChecker = true;

                        } else {

                            IJ.selectWindow(image);
                            IJ.run("Close");

                        }

                    }

                    if (selectedFileChecker) {

                        IJ.log("Selected file is already open");

                        IJ.selectWindow(selectedFile);
                        ImagePlus selectedImage = WindowManager.getCurrentWindow().getImagePlus();

                        setDisplayRange = false;
                        calibration = selectedImage.getCalibration();
                        String titleOriginal = selectedImage.getTitle();

                        ImagePlus forBackSegmentation = previewImage.projectImage(selectedImage, "max");

                        Double pxSizeFromImage = calibration.pixelWidth;
                        int minSizePx = Image.calculateMinSizePx(pxSizeFromImage, minSizeBack);
                        int maxSizePx = Image.calculateMaxSizePx(pxSizeFromImage, maxSizeBack);

                        SegmentationVisualization visualizer = new SegmentationVisualization();

                        visualizer.backgroundVisualization(forBackSegmentation, sigmaBackground, thresholdBackground,
                                        minSizePx, maxSizePx, selectedImage, titleOriginal,
                                        calibration, setDisplayRange);

                    } else {

                        IJ.log("The selected image is not open");

                        // segment background and show for validation
                        setDisplayRange = true;
                        ImagePlus originalImage = previewImage.openImage(selectedFile);

                        String titleOriginal = originalImage.getTitle();

                        ImagePlus forBackSegmentation = previewImage.projectImage(originalImage, "max");

                        int minSizePx;
                        int maxSizePx;

                        if (calibrationSetting) {

                            calibration = previewImage.calibrate();
                            minSizePx = Image.calculateMinSizePx(pxSizeMicron, minSizeBack);
                            maxSizePx = Image.calculateMaxSizePx(pxSizeMicron, maxSizeBack);

                            IJ.log("Metadata will be overwritten.");
                            IJ.log("Pixel size set to: " + pxSizeMicron);
                            IJ.log("Frame rate set to: " + frameRate);


                        } else {

                            calibration = originalImage.getCalibration();
                            Double pxSizeFromImage = calibration.pixelWidth;
                            minSizePx = Image.calculateMinSizePx(pxSizeFromImage, minSizeBack);
                            maxSizePx = Image.calculateMaxSizePx(pxSizeFromImage, maxSizeBack);

                            IJ.log("Metadata will no be overwritten");

                        }

                        SegmentationVisualization visualizer = new SegmentationVisualization();

                        visualizer.backgroundVisualization(forBackSegmentation, sigmaBackground, thresholdBackground,
                                minSizePx, maxSizePx, originalImage, titleOriginal,
                                calibration, setDisplayRange);

                    }

                } else {

                    IJ.log("There are no images open!");

                    // segment background and show for validation
                    ImagePlus originalImage = previewImage.openImage(selectedFile);

                    setDisplayRange = true;
                    String titleOriginal = originalImage.getTitle();

                    ImagePlus forBackSegmentation = previewImage.projectImage(originalImage, "max");

                    int minSizePx;
                    int maxSizePx;

                    if (calibrationSetting) {

                        calibration = previewImage.calibrate();
                        minSizePx = Image.calculateMinSizePx(pxSizeMicron, minSizeBack);
                        maxSizePx = Image.calculateMaxSizePx(pxSizeMicron, maxSizeBack);

                        IJ.log("Metadata will be overwritten.");
                        IJ.log("Pixel size set to: " + pxSizeMicron);
                        IJ.log("Frame rate set to: " + frameRate);

                    } else {

                        calibration = originalImage.getCalibration();
                        Double pxSizeFromImage = calibration.pixelWidth;
                        minSizePx = Image.calculateMinSizePx(pxSizeFromImage, minSizeBack);
                        maxSizePx = Image.calculateMaxSizePx(pxSizeFromImage, maxSizeBack);

                        IJ.log("Metadata will no be overwritten");

                    }


                    SegmentationVisualization visualizer = new SegmentationVisualization();

                    visualizer.backgroundVisualization(forBackSegmentation, sigmaBackground, thresholdBackground,
                            minSizePx, maxSizePx, originalImage, titleOriginal,
                            calibration, setDisplayRange);
                }

            } else {

                IJ.error("Please choose a file in the file list!");

            }

        }

    } // close inner class

    public class MySaveListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {

            IJ.log("Saving settings");

            // get current settings for spot segmentation
            // get all the values from the GUI
            double sigmaLoG = (Double) doubleSpinnerLoGSpot.getValue();
            IJ.log("Spot LoG sigma: " + sigmaLoG);

            double prominence = (Double) doubleSpinnerProminenceSpot.getValue();
            IJ.log("Spot prominence: " + prominence);

            double sigmaSpots = (Double) doubleSpinnerGaussSpot.getValue();
            IJ.log("Spot gauss sigma: " + sigmaSpots);

            double rollingSpots = (Double) doubleSpinnerRollingBallSpot.getValue();
            IJ.log("Spot rolling ball radius: " + rollingSpots);

            String thresholdSpots = (String) thresholdListSpot.getSelectedItem();
            IJ.log("Spot threshold: " + thresholdSpots);

            int radiusGradient = (Integer) intSpinnerGradient.getValue();
            IJ.log("Spot gradient Radius: " + radiusGradient);

            double minSizeSpot = (Double) doubleSpinnerMinSize.getValue();
            double maxSizeSpot = (Double) doubleSpinnerMaxSize.getValue();
            IJ.log("Spots size from: " + minSizeSpot + " to " + maxSizeSpot + " µm²");

            double lowCirc = (Double) doubleSpinnerLowCirc.getValue();
            double highCirc = (Double) doubleSpinnerHighCirc.getValue();
            IJ.log("Spots circ. from: " + lowCirc + " to " + highCirc);

            // get current settings for background segmentation
            double sigmaBackground = (Double) doubleSpinBack1.getValue();
            IJ.log("Background sigma: " + sigmaBackground);

            String thresholdBackground = (String) thresholdListBack.getSelectedItem();
            IJ.log("Background threshold: " + thresholdBackground);

            double minSizeBack = (Double) doubleSpinBack2.getValue();
            double maxSizeBack = (Double) doubleSpinBack3.getValue();
            IJ.log("Background size from: " + minSizeBack + " to " + maxSizeBack + " µm²");

            // check if calibration needs to be overwritten
            boolean calibrationSetting;

            if (checkCalibration.isSelected()) calibrationSetting = true;
            else calibrationSetting = false;
            IJ.log("Calibration override: " + calibrationSetting);

            double pxSizeMicron = (Double) doubleSpinnerPixelSize.getValue();
            IJ.log("Calibration: " + pxSizeMicron + " µm");

            double frameRate = (Double) doubleSpinnerFrameRate.getValue();
            IJ.log("Calibration: " + frameRate + " sec");

            int stimFrame = (Integer) integerSpinnerStimulationFrame.getValue();
            IJ.log("Stimulation frame: " + stimFrame);

            XmlHandler writeToXml = new XmlHandler();

            writeToXml.xmlWriter(outputDir, projMethod,
                    sigmaLoG, prominence,
                    sigmaSpots, rollingSpots, thresholdSpots, radiusGradient,
                    minSizeSpot, maxSizeSpot, lowCirc, highCirc,
                    sigmaBackground, thresholdBackground,
                    minSizeBack, maxSizeBack,
                    stimFrame, calibrationSetting, pxSizeMicron, frameRate);

        }
    }

    // upon pressing the stop button call sequencer.stop() method
    public class MyBatchListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {

            IJ.log("Starting batch processing");
            Commands.closeAll();

            // get current settings for spot segmentation
            // get all the values from the GUI
            double sigmaLoG = (Double) doubleSpinnerLoGSpot.getValue();
            IJ.log("LoG sigma: " + sigmaLoG);

            double prominence = (Double) doubleSpinnerProminenceSpot.getValue();
            IJ.log("Prominence: " + prominence);

            double sigmaSpots = (Double) doubleSpinnerGaussSpot.getValue();
            IJ.log("Gauss sigma: " + sigmaSpots);

            double rollingSpots = (Double) doubleSpinnerRollingBallSpot.getValue();
            IJ.log("Rolling Ball radius: " + rollingSpots);

            String thresholdSpots = (String) thresholdListSpot.getSelectedItem();
            IJ.log("Threshold: " + thresholdSpots);

            int radiusGradient = (Integer) intSpinnerGradient.getValue();
            IJ.log("Gradient Radius: " + radiusGradient);

            double minSizeSpot = (Double) doubleSpinnerMinSize.getValue();
            double maxSizeSpot = (Double) doubleSpinnerMaxSize.getValue();
            IJ.log("Spots size from: " + minSizeSpot + " to " + maxSizeSpot + " µm²" );

            double lowCirc = (Double) doubleSpinnerLowCirc.getValue();
            double highCirc = (Double) doubleSpinnerHighCirc.getValue();
            IJ.log("Spots circ. from: " + lowCirc + " to " + highCirc);

            // get current settings for background segmentation
            double sigmaBackground = (Double) doubleSpinBack1.getValue();
            IJ.log("Sigma background set to: " + sigmaBackground);

            String thresholdBackground = (String) thresholdListBack.getSelectedItem();
            IJ.log("Threshold for background set to: " + thresholdBackground);

            double minSizeBack = (Double) doubleSpinBack2.getValue();
            double maxSizeBack  = (Double) doubleSpinBack3.getValue();
            IJ.log("Background size from: " + minSizeBack + " to " + maxSizeBack + " µm²" );

            // check if calibration needs to be overwritten
            boolean calibrationSetting;

            if (checkCalibration.isSelected()) calibrationSetting = true;
            else calibrationSetting = false;

            double pxSizeMicron = (Double) doubleSpinnerPixelSize.getValue();
            double frameRate = (Double) doubleSpinnerFrameRate.getValue();

            int stimFrame = (Integer) integerSpinnerStimulationFrame.getValue();
            IJ.log("Stimulation frame: " + stimFrame);

            XmlHandler writeToXml = new XmlHandler();

            writeToXml.xmlWriter(outputDir, projMethod,
                    sigmaLoG, prominence,
                    sigmaSpots, rollingSpots, thresholdSpots, radiusGradient,
                    minSizeSpot, maxSizeSpot, lowCirc, highCirc,
                    sigmaBackground, thresholdBackground,
                    minSizeBack, maxSizeBack,
                    stimFrame, calibrationSetting, pxSizeMicron, frameRate);

            BatchProcessor batch = new BatchProcessor(inputDir, outputDir, fileList,
                    projMethod, sigmaLoG, prominence, sigmaSpots, rollingSpots, thresholdSpots, radiusGradient,
                    minSizeSpot, maxSizeSpot, lowCirc, highCirc,
                    sigmaBackground, thresholdBackground,
                    minSizeBack, maxSizeBack,
                    stimFrame, calibrationSetting, pxSizeMicron, frameRate
                    );

            batch.loopOverImages();

        }

    } // close inner class

    // PreviewGui constructor
    PreviewGui (String inputDirectory, String outputDirectory, ArrayList<String> filesToProcess){

        inputDir = inputDirectory;
        outputDir = outputDirectory;
        fileList= filesToProcess;

        // Projection Method
        projMethod = "median";

        sigmaLoG = 0.5;
        prominence = 0.005;
        sigmaSpots = 1.0;
        rollingSpots = 30.0;
        thresholdSpots = "Triangle";
        radiusGradient = 3;
        minSizeSpot = 0.0;
        maxSizeSpot = 1000.0;
        lowCirc = 0.0;
        highCirc = 1.0;

        sigmaBackground = 4.0;
        thresholdBackground = "MinError";
        minSizeBack = 0.0;
        maxSizeBack =  10000.0;

        calibrationSetting = false;
        stimFrame = 5;
        pxSizeMicron = 0.162;
        frameRate = 2.0;

    }

    PreviewGui (String inputDirectory, String outputDirectory, ArrayList<String> filesToProcess,
                       String projectionMethod, double getSigmaLoG, double getProminence,
                       double getSigmaSpots, double getRollingSpots, String getThresholdSpots, int getRadiusGradient,
                       double getMinSizePxSpot, double getMaxSizePxSpot, double getLowCirc, double getHighCirc,
                       double getSigmaBackground, String getThresholdBackground,
                       double getMinSizePxBack, double getMaxSizePxBack,
                       int getStimFrame, boolean getCalibrationSetting, double getSizeMicron, double getFrameRate ){

        inputDir = inputDirectory;
        outputDir = outputDirectory;
        fileList = filesToProcess;

        projMethod = projectionMethod;

        sigmaLoG = getSigmaLoG;
        prominence = getProminence;
        sigmaSpots = getSigmaSpots;
        rollingSpots = getRollingSpots;
        thresholdSpots = getThresholdSpots;
        radiusGradient = getRadiusGradient;
        minSizeSpot = getMinSizePxSpot;
        maxSizeSpot = getMaxSizePxSpot;
        lowCirc = getLowCirc;
        highCirc = getHighCirc;

        sigmaBackground = getSigmaBackground;
        thresholdBackground = getThresholdBackground;
        minSizeBack = getMinSizePxBack;
        maxSizeBack =  getMaxSizePxBack;

        calibrationSetting = getCalibrationSetting;
        stimFrame = getStimFrame;
        pxSizeMicron = getSizeMicron;
        frameRate = getFrameRate;

    }

}