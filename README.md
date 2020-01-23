pHlourinJ - ImageJ 1.x plugin.
========================

Synapto-pHluorin allows to measure synaptic vesicle release and recycling in cultured neurons. 
This ImageJ plugin segments synaptic boutons in noisy and background rich images.
It is using a seeded watershed and extracts fluorescent intensity traces over time.
The key advantage of this plugins is the easy and interactive adjustment of the 
segmentation parameters for the end user. 

Based on the ImageJ 1.x plugin example: https://github.com/imagej/example-legacy-plugin

Uses the following plugins:

Marker controlled watershed: http://fiji.sc/Marker-controlled_Watershed

David Legland, Ignacio Arganda-Carreras, Philippe Andrey; MorphoLibJ: integrated library and plugins for mathematical morphology with ImageJ. Bioinformatics 2016; 32 (22): 3532-3534. doi: 10.1093/bioinformatics/btw413 

LoG3D plugin: http://bigwww.epfl.ch/sage/soft/LoG3D/ 

D. Sage, F.R. Neumann, F. Hediger, S.M. Gasser, M. Unser, "Automatic Tracking of Individual Fluorescence Particles: Application to the Study of Chromosome Dynamics," IEEE Transactions on Image Processing, vol. 14, no. 9, pp. 1372-1383, September 2005. 

Installation
========================

Download Fiji: https://fiji.sc/

Download .jar file: https://github.com/schmiedc/pHluorinJ/releases

Copy into Fiji plugin folder.

Datasets
========================

Expected are 2D single channel .tif files containing multiple frames.
At a specific frame the cultured neurons were stimulated and active boutons show an increase in intensity. 
The image calibration can be changed in the workflow.
A settings file can be provided but can also be created later.

A small example file is provided here: https://github.com/schmiedc/pHluorinJ/blob/master/testInput/testMovie.tif

The default segmentation parameters should work for this example file.

Quick start guide
========================
Execute the plugin via: ***Plugins > pHluorinJ***.

A log file and a Setup dialog will appear.
Specify the input and output directory.
A settings file can be provided or left empty.

Start the Preview by pressing ***OK***.

The Preview GUI dialog with the title pHluorin Processing will appear:

*Left section:* contains tabs and allows to test different settings for the segmentation of the Boutons and the Background.  
*Middle section:* the available files can be selected.  
*Right section:* experimental settings such as image calibration, stimulation frame can be specified and the batch processing can be executed.  
*Bottom section:* save, load and reset the settings as well as reset the directories.  

To test the segmentation select an image title in the middle pane of the window. 
Then select the segmentation type by selecting the tab for either ***Boutons*** or ***Background***.
The settings are structured according to the image processing that is performed. 
Change the parameters you want to test and the press ***Preview***.
A new image window will appear showing the movie overlayed with the segmentation based on the specified segmentation parameters. 

**IMPORTANT:** please make sure that the calibration is correct in the right window and override the existing metadata if necessary. Otherwise the segmentation might not work.

Once you are happy with the segmentation you can execute the batch processing.
This will perform the segmentation of the boutons as well as the background. 
Using this segmentation the fluorescent intensity traces over time are extracted from the movies.
Press ***Batch Processing*** to execute the workflow with the specified segmentation parameters.
The log window will document the processing steps.

When the workflow is finished ***Finished batch Processing*** will appear in the log window.

Results
========================

The workflow will save a <Date><Time>-settings.xml as well as a <Date><Time>-Log.txt file into the output directory.
Please attach the log and settings file in bug reports.
  
The workflow will save the input image as well as the segmentation as ImageJ ROIs for later review in the output folder
`<filename>_spot.tif
<filename>_Spot.zip
<filename>_background.tif
<filename>_background.zip`
To review the segmentation drag & drop the input image and the ROI into ImageJ.
 
The measurements for the background as well as the bouton intensity over time are saved as .csv files for each ROI.
`<filename>_ROI-<number>_background.csv
<filename>_ROI-<number>_Spot.csv`

The processing and analysis of the result files can be achieved using the following R scripts:
https://github.com/schmiedc/pHluorin-Rscripts
