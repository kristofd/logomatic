package no.bekk.logomatic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;

import org.apache.log4j.Logger;

public class LogFileParser {

	private LogLineParser lineParser = new LogLineParser();
	private int linesPerBatch = 100000;
	private String logLine;
	private int linesInFile;
	private int linesTotal;
	private SolrIntegration solrIntegration = null;

	static final Logger log = Logger.getLogger(LogFileParser.class);

	public LogFileParser(SolrIntegration integration) {
		solrIntegration = integration;
	}

	public void parseAndSend(String directoryName) {
		ArrayList<LogLineBean> docs = new ArrayList<LogLineBean>();
		try {
			File directory = new File(directoryName);
			File[] files =  directory.listFiles();
			for (File file : files) {
				linesInFile = 0;
				log.info("Parsing file: " + file.getName() );
				FileInputStream fileStream = new FileInputStream(file);
				GZIPInputStream gzipStream = new GZIPInputStream(fileStream);
				InputStreamReader inputReader = new InputStreamReader(gzipStream, "UTF-8");
				BufferedReader bufferedReader = new BufferedReader(inputReader);

				long timeStart = System.currentTimeMillis();
			
				while ((logLine = bufferedReader.readLine()) != null) {
					LogLineBean bean = lineParser.parse(logLine);
					if (bean != null) {
						docs.add(lineParser.parse(logLine));
						linesInFile++;
						linesTotal++;
					}

					if (linesInFile % linesPerBatch == 0) {
						solrIntegration.send(docs);
						log.info("Has sent " + linesTotal + " lines to the server.");
						docs.clear();
					}
				}
			
				solrIntegration.send(docs);
				log.info("Done parsing " + file.getName() + ", processed " + linesInFile + " lines in " + (System.currentTimeMillis() - timeStart) + " msecs.");

				bufferedReader.close();
				inputReader.close();
				gzipStream.close();
				fileStream.close();
			}
		} catch (Exception e) {
			log.error("Error reading log files: " + e);
		}
	}

}
