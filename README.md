# Synaptic Activity in ImageJ (SynActJ)

SynActJ is an image and data analysis workflow that allows to analyze synaptic activity. It is based on a Fiji plugin and a R Shiny App that implement the automated image analysis of active synapses in time-lapse movies. We tested the workflow with movies of pHluorin or calcium sensors.

<img src="https://schmiedc.github.io/SynActJ/images/main/teaser.png" alt="Intro" class="inline"/>

## Documentation

Have a look at the github pages site for more information:<br>
[https://schmiedc.github.io/SynActJ/](https://schmiedc.github.io/SynActJ/)
<br/>
<br/>

## Core features
- Java Swing based graphical user interface
- Interactive adjustment over entire dataset
- Batch processing executed from main interface
- Saving and loading of processing settings
- Shiny App for data processing

## Accepted Datasets
Expected are 2D single channel .tif files containing multiple frames. At a specific frame the cultured neurons were stimulated and active boutons show an increase in intensity. The image calibration can be changed in the workflow. A settings file can be provided but can also be created later.

A small example file is provided here: [Link to example data](https://github.com/schmiedc/SynActJ/blob/master/testInput/testMovie.tif)

The default segmentation parameters should work for this example file.

## Installation

For the image analysis you need to download and install Fiji: [https://fiji.sc/](https://fiji.sc/)
The plugin is available via an update site. Add the Cellular-Imaging site:

1. Select **_Help  › Update...</strong>_** from the menu bar.
2. Click on Manage update sites. Which opens the **_Manage update sites_** dialog.
3. Press **_Add update size_** a new line in the Manage update sites dialog appears
4. Add **_https://sites.imagej.net/Cellular-Imaging/_** as url
5. Add an optional name such as Cellular-Imaging
6. Press **_Close_** and then **_Apply changes_**

For the data anaylsis you need to install R: [https://www.r-project.org/](https://www.r-project.org/)<br>
As an R editor I recommend to use RStudio: [https://rstudio.com/products/rstudio/download/](https://rstudio.com/products/rstudio/download/)
