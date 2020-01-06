The tool CMMNcomposer composes a base CMMN model and refining CMMN model fragments into a new CMNN model, using feature composition. See https://research.tue.nl/en/publications/feature-oriented-composition-of-declarative-artifact-centric-proc for a paper defining the composition approach.

CMMNcomposer requires the JDOM library: jdom-1.1.3.jar

Usage: java -jar CMMNcomposer \<file\>, where file is a textfile that lists line by line the CMMN files to be composed. The first files is a base CMMN model, which is refined by the subsequent CMMN models.

Folder examples contains base CMMN models and CMMN model fragments for three different business processes. CMMN files with \<name-Fx\> denote a feature that refines base CMMN file \<name\>.
