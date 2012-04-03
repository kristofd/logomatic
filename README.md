Logomatic - A traffic log analysis tool
=======================================

Getting it
----------

Warning: Logomatic is experimental. Some features are not robustly implemented.

    git clone git://github.com/kristofd/logomatic.git
    cd logomatic
    mvn clean package


Description
-----------

Logomatic is a traffic log analysis tool. This project contains a simple web application for viewing traffic statistics, using Solr as a backend. It also contains an utility that parses traffic logs (in Combined Log Format) and pushes the data to Solr so it can take care of searching, filtering and sorting. Flot is used for presentation.


Quick start
-----------

After downloading and building the indexing utility, put the log files you want to analyze in a subdirectory. Both plain and gzipped log files are supported.

Then start the server by typing:

	mvn jetty:run

As the server starts a stack trace will be printed - beginning with "logomatic/src/main/webapp is not an existing directory". This can safely be ignored.

Push log files to the sever by typing:

	java -Xmx512M -jar logomatic.jar [log file directory] http://localhost:8080/solr

Then open a browser and go to:

	http://localhost:8080/gui

Use the widgets on the right side to browse and filter the traffic data. Hit Enter to update the view.


TODO
-----------

* Get rid of the stack trace when starting Jetty (disabling overlays might help)
* Clean up JavaScript code, it is a mess (remember, this is an experimental project :)
* The inital start and end dates are hard coded (look at solr/webapps/gui/gui.js at around line 80), these should instead depend on the data set
* Reduce Solr memory consumption so larger data sets are supported
* Allow simultaneous filtering, sorting and presentation of additional log fields
* Simplify and improve user interaction
