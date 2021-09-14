---
layout: default
title: Start
---
# Automated analysis of synaptic activity

Synaptic Activity in ImageJ (SynActJ) is an image and data analysis workflow that allows to analyze synaptic activity. It is based on a Fiji plugin and a R Shiny App that implement the automated image analysis of active synapses in time-lapse movies. We tested the workflow with movies of pHluorin or calcium sensors.

## Core features

- Java Swing based graphical user interface
- Interactive adjustment over entire dataset
- Batch processing executed from main interface
- Saving and loading of processing settings
- Shiny App for data processing

[Link to SynActJ repository](https://github.com/schmiedc/SynActJ)<br>
[Link to SynActJ Shiny repository](https://github.com/schmiedc/SynActJ_Shiny)

## Methods overview
 
**1. Detection & Segmentation**
- Average projections are performed before and after the specified stimulation frame
- The projection after the stimulation is divided by the projection before the stimulation
- A Laplacian of Gaussian is performed to enhance Blob-like structures and a maximum detection is performed
- An intensity threshold is used to segment the blob area
- The detection and the segmentation is fed into a seeded watershed to compute regions of interest (ROI) per detection and separate touching objects

**2. Background segmentation**
- Maximum intensity projection over movie
- Intensity based threshold
- Invert mask

**3. Image measurements**
- Mean signal per ROI is extracted for each frame
- Other parameters such as area are measured per ROI

**4. Data processing**
- Average signal and background is computed
- Background is subtracted from average signal
- Signal is surface normalized
- Signal is peak normalized

### Fiji methods used

[Marker controlled watershed:](http://fiji.sc/Marker-controlled_Watershed)<br>
David Legland, Ignacio Arganda-Carreras, Philippe Andrey; MorphoLibJ: integrated library and plugins for mathematical morphology with ImageJ. Bioinformatics 2016; 32 (22): 3532-3534. doi: 10.1093/bioinformatics/btw413
<br>
<br>
[LoG3D plugin:](http://bigwww.epfl.ch/sage/soft/LoG3D/)<br>
D. Sage, F.R. Neumann, F. Hediger, S.M. Gasser, M. Unser, "Automatic Tracking of Individual Fluorescence Particles: Application to the Study of Chromosome Dynamics," IEEE Transactions on Image Processing, vol. 14, no. 9, pp. 1372-1383, September 2005.

## Accepted Datasets

Expected are 2D single channel .tif files containing multiple frames. At a specific frame the cultured neurons were stimulated and active boutons show an increase in intensity. The image calibration can be changed in the workflow. A settings file can be provided but can also be created later.
<br>
<br>
A small example file is provided here: [Link to example data](https://github.com/schmiedc/pHluorinJ/blob/master/testInput/testMovie.tif)
<br>
The default segmentation parameters should work for this example file.

## Installation

For the image analysis you need to download and install Fiji: [Link to Fiji](https://fiji.sc/).<br/>
The plugin is available via an update site. Add the Cellular-Imaging site:

1. Select **Help › Update…** from the menu bar. This will install potential updates and open a new window.
2. Click on **Manage update sites**. Which opens the Manage update sites dialog.
3. Press **Add update site** a new line in the Manage update sites dialog appears.
4. Add https://sites.imagej.net/Cellular-Imaging/ as url.
5. Add an optional name such as Cellular-Imaging.
6. Press **Close** and then **Apply** changes.


R Version 4.1.0<br/>
[Link to R](https://cran.r-project.org/bin/windows/base/)<br/>
Select version 4.1.0


RStudio 1.4.17117<br/>
[Link to RStudio](https://www.rstudio.com/products/rstudio/download/)


# User tutorials
