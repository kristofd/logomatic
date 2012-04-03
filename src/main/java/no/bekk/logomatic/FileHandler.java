package no.bekk.logomatic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

import org.apache.log4j.Logger;

public class FileHandler {

	private InputStream rawStream = null;
	private FileInputStream fileStream = null;
	private InputStreamReader inputReader = null;
	private BufferedReader bufferedReader = null;
	
	static final Logger log = Logger.getLogger(FileHandler.class);
	
	public FileHandler(File file) {
		try {
			fileStream = new FileInputStream(file);	
			if (file.getName().endsWith(".gz")) {
				rawStream = new GZIPInputStream(fileStream);
			} else {
				rawStream = fileStream;
			}
			inputReader = new InputStreamReader(rawStream, "UTF-8");
			bufferedReader = new BufferedReader(inputReader);
		} catch (Exception e) {
			log.error("Error getting file reader: " + e);
		}
	}
	
	public BufferedReader getBufferedReader() {
		return bufferedReader;
	}
	
	public void close() {
		try {
			bufferedReader.close();
			inputReader.close();
			rawStream.close();
			fileStream.close();
		} catch (Exception e) {
			log.error("Error closing file reader: " + e);
		}
	}
}
