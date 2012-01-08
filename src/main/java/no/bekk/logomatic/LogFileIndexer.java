package no.bekk.logomatic;

import org.apache.log4j.xml.DOMConfigurator;

public class LogFileIndexer {
	
	public LogFileIndexer(String directoryName, String url) {
		
		DOMConfigurator.configureAndWatch("log4j.xml", 5000);
		
		SolrIntegration solrIntegration = new SolrIntegration(url);
		LogFileParser fileParser = new LogFileParser(solrIntegration);
		fileParser.parseAndSend(directoryName);
	}

	public static void main(String[] args) {
		if (args.length == 0) {
			System.err.println("Usage: LogFileIndexer [log file directory] [url to Solr]");
		} else {
			new LogFileIndexer(args[0], args[1]);
		}
	}
	
}
