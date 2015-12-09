# CPSC433-AI-assignment
Authors:

Brandon Brien      10079883

Henry Khuu 		     10041340

Nikita Ryzhenkov   10055746

Richard Huynh      10099642

Shayne Baumgartner 10098339


to build:  
from root (where 'src' and 'jfxrt.jar' are located):  
	1) mkdir build   
	2) javac -cp jfxrt.jar -d build src/*/*.java  
	

to run:  
from root (where '/src', '/build' and 'jfxrt.jar' are located):  
	1) java -cp .:../jfxrt.jar:build/*.class:build main.Main   
	2) follow prompts
	3) be aware of your current location in respect to your .txt files used for testing
