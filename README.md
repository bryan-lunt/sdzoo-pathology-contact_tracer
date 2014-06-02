This program is for contact tracing in epidemilogical studies. Specifically, it was commissioned for the San Diego Zoo's veterinary hospital, thus all input and output formats and basic assumptions about patients are chosen to conform to their needs.

This program takes as input a description of the movements of animals as they are relocated between zoo enclosures.
It also takes (in one of a few forms) an input describing which animals are known to be sick.

It then determines which animals were exposed to the sick individual either by begin together with that individual, or being in a potentially contaminated exposure _shortly_ (adjustable) after that individual left.

Along the way, it is possible also to just declare an enclosure to be contaminated, without knowning by whom, and see which animals passed through it during that time-period.

For a description of input and output formats, see the file "./doc/FORMATS.md" .


Building the program
--------------------

(If you were distributed an executable jar file, this step is unnecessary.)

To build this program, you must have a new (version 1.6 or greater?) JDK installed, and maven.
Typing 

	mvn package

at the command-line will accomplish this, it downloads all the project dependencies.

Running the program
-------------------

The program can be used two ways, either as a GUI (Graphical User Interface) program, or entirely from the command-line.

To start the program from the command-line (assuming you are still in the build directory), type :

	java -jar ./target/contact-tracer-0.0.1-SNAPSHOT-jar-with-dependencies.jar

Also, you can double-click on the file contact-tracer-0.0.1-SNAPSHOT-jar-with-dependencies.jar

This will start the GUI version.

To get a description of the command-line arguments needed to run the program from the command-line, type:

	java -jar ./target/contact-tracer-0.0.1-SNAPSHOT-jar-with-dependencies.jar --help

This will explain all of the command-line options needed to specify inputs solely from the command-line. (As you might inside a script or one a server or other headless machine.)

Please see the "./doc/FORMATS.md" file for an explanation of input and output formats.
