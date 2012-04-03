
package no.bekk.logomatic;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

public class LogFileParser {

	private LogLineParser lineParser = new LogLineParser();
	private int linesPerBatch = 100000;
	private SolrIntegration solrIntegration = null;

	static final Logger log = Logger.getLogger(LogFileParser.class);

	public LogFileParser(SolrIntegration integration) {
		solrIntegration = integration;
	}

	public int sendFiles(String directoryName) {
		int fileCounter = 0;
		ArrayList<LogLineBean> docs = new ArrayList<LogLineBean>();
		File[] files = getFileList(directoryName);
		for (File file : files) {
			FileHandler handler = new FileHandler(file);
			while ((docs = readBatch(handler, linesPerBatch)).size() > 0) {
				solrIntegration.send(docs);
			}
			handler.close();
			fileCounter++;
		}
		return fileCounter;
	}

	public File[] getFileList(String directoryName) {
		try {
			File directory = new File(directoryName);
			return directory.listFiles();
		} catch (Exception e) {
			log.error("Error getting list of log files to parse: " + e);
		}
		return null;
	}

	public ArrayList<LogLineBean> readBatch(FileHandler handler, int batchSize) {
		int lineCounter = 0;
		String logLine;
		ArrayList<LogLineBean> docs = new ArrayList<LogLineBean>();
		try {
			while (lineCounter < batchSize && (logLine = handler.getBufferedReader().readLine()) != null) {
				LogLineBean bean = lineParser.parse(logLine);
				if (bean != null) {
					docs.add(bean);
					lineCounter++;
				}
			}
		} catch (IOException ioe) {
			log.error("Error reading log files: " + ioe);
		}
		return docs;
	}
}
