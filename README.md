Logomatic - A traffic log analysis tool
===================================

Getting it
-----------

Warning: Logomatic is experimental. Some features are not robustly implemented.

    git clone git://github.com/kristofd/logomatic.git
    cd logomatic
    mvn clean install assembly:single


Description
-----------

Logomatic is a Solr-based traffic log analysis tool. This project contains an utility that parses traffic logs (in Combined Log Format) and pushes the data to a local Solr server for searching, filtering, sorting and presentation. The project also contains a simple web application that lets you browse and view statistics about the traffic. 


Quick start
-----------

After downloading and building the indexing utility, put the log files you want to analyze in `logfiles` subdirectory.

Then start the Solr server by typing:

	cd solr
	./startserver.sh

Push log files to Solr by opening a new terminal window and typing:

	./pushdata.sh

Then open a browser and go to:

	http://localhost:8080/gui

Use the widgets on the right side to browse and filter the traffic data. Hit Enter to update the view.


TODO
-----------

* The inital start and end dates are hard coded (look at solr/webapps/gui/gui.js at around line 80), these should instead depend on current data set.
* Clean up JavaScript code, it is a mess (remember, this is an experimental project)
* Reduce Solr memory consumption to allow for larger input data sets
* Allow for filtering, sorting and presentation of additional fields
* Simplify and improve the interaction