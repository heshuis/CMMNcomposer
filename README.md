The tool CMMNcomposer composes a set of CMMN model fragments into a new CMNN model, using feature composition.

CMMNcomposer requires the JDOM library: jdom-1.1.3.jar

Usage: java -jar CMMNcomposer "<file>", where file is a textfile that lists line by line the CMMN files to be composed. 

CMMN files with "<name-Fx>" denote a feature that refines base CMMN file <name>.
