package no.bekk.logomatic;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import org.apache.log4j.Logger;

public class LogLineParser {

	SimpleDateFormat timestampInputFormat = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss Z", Locale.US);
	SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

	String[] monthName = {"jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "sep", "oct", "nov", "dec"};
	String[] dayName = {"mon", "tue", "wed", "thu", "fri", "sat", "sun"};

	HashMap<String, String> map = new HashMap<String, String>();
	Calendar calendar = Calendar.getInstance();
	int startPos, endPos;
	String temp;
	
	static final Logger log = Logger.getLogger(LogLineParser.class);

	public LogLineParser() {
		calendar.setMinimalDaysInFirstWeek(4);
		calendar.setFirstDayOfWeek(Calendar.MONDAY);
	}
	
	public LogLineBean parse(String line) {
		try {
			LogLineBean bean = new LogLineBean();

			startPos = 0;
			endPos = line.indexOf(" ");
			bean.client = line.substring(startPos, endPos);

			startPos = endPos+1;
			endPos = line.indexOf(" ", startPos);
			bean.identuser = line.substring(startPos, endPos);
		
			startPos = endPos+1;
			endPos = line.indexOf(" ", startPos);
			bean.authuser = line.substring(startPos, endPos);

			startPos = endPos+2;
			endPos = line.indexOf("]", startPos);
			Date date = timestampInputFormat.parse(line.substring(startPos, endPos));
			bean.timestamp = outputFormat.format(date);

			calendar.setTime(date);
			bean.monthofyear = monthName[calendar.get(Calendar.MONTH)];
			bean.weeknumber = "" + calendar.get(Calendar.WEEK_OF_YEAR);
			bean.dayofmonth = "" + calendar.get(Calendar.DAY_OF_MONTH);
			bean.dayofweek = dayName[calendar.get(Calendar.DAY_OF_WEEK)-1];
			bean.hourofday = "" + calendar.get(Calendar.HOUR_OF_DAY);

			startPos = endPos+3;
			endPos = line.indexOf(" ", startPos);
			bean.method = line.substring(startPos, endPos);

			startPos = endPos+1;
			endPos = line.indexOf(" ", startPos);
			temp = line.substring(startPos, endPos);
			bean.url = temp;

			// decide file type, based on URL
			bean.filetype = getFileType(temp);
			
			startPos = endPos+1;
			endPos = line.indexOf("\"", startPos);
			bean.protocol = line.substring(startPos, endPos);

			startPos = endPos+2;
			endPos = line.indexOf(" ", startPos);
			bean.responsecode = line.substring(startPos, endPos);

			startPos = endPos+1;
			endPos = line.indexOf(" ", startPos);
			temp = line.substring(startPos, endPos);
			if ("-".equals(temp)) {
				temp = "0";
			}
			bean.bytes = temp;

			startPos = endPos+2;
			endPos = line.indexOf("\"", startPos);
			bean.referrer = line.substring(startPos, endPos);
		
			startPos = endPos+3;
			endPos = line.indexOf("\"", startPos);
			bean.useragent = line.substring(startPos, endPos);
			return bean;
		} catch (Exception e) {
			log.error("Parsing error: " + e + "\n" + line);
			return null;
		}
	}
	
	public String getFileType(String input) {		
		// if any parameters, strip them
		int pos = input.indexOf("?");
		if (pos != -1) {
			input = input.substring(0, pos);
		}
		
		// if no file extension -> this is html
		pos = input.lastIndexOf(".");
		if (pos == -1) {
			return "html";
		}

		return input.substring(pos+1).toLowerCase();
	}

	public String replace(String input, String pattern, String replacement) {
		int pos = input.indexOf(pattern);
		while (pos != -1) {
			input = input.substring(0, pos) + replacement + input.substring(pos + pattern.length());
			pos = input.indexOf(pattern, pos + pattern.length() + 1);
		}
		return input;
	}	
}
