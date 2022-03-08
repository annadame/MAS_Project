# MAS_Project

# Initialize project steps:
- Download Genius from: [TU Delft](http://ii.tudelft.nl/genius/?q=article/releases)
- Rename 'genius\<version\>.jar' to 'genius.jar'
- Place this .jar file in the /lib folder. The project should recognize it and the run configuration should be functioning.

# Adding/modifying new components:
In the /src folder new components can be created, only the necessary .java files for our project have been included. Feel free to create helper classes.

# Building components
To build the .java files making them ready for use, find the green hammer icon in the top right of IntelliJ. Pressing this will create a /out directory with the .class files (compiled code). After running Genius, these .class files can be imported into the boarepository by right clicking and clicking on 'Add new component' in the BOA components tab.

# Running Genius
On the top right in IntelliJ, the 'Genius Run' run configuration should be available, run this and Genius will execute.

# .xml files in the main path
Genius automatically creates these files, maybe we can change the directory somehow. It uses these to find the components we add.

# Viewing examples/taking inspiration
In IntelliJ you can view the decompiled code from other components by navigating into genius.jar in the /lib folder. in the /agents package, agents from anac competitions and others can be found. IntelliJ will decompile these, meaning that you can see the .java code that generated the .class file. Compiling java code does mean that comments and variable names disappear.