package no.bekk.logomatic;

import org.apache.solr.client.solrj.beans.Field;

public class LogLineBean {

	public static int idCounter = 0;
	
	
	@Field
	public String id;
	
	@Field
	public String client;
	
	@Field
	public String identuser;
	
	@Field
	public String authuser;
	
	@Field
	public String timestamp;
	
	@Field
	public String monthofyear;
	
	@Field
	public String weeknumber;
	
	@Field
	public String dayofmonth;
	
	@Field
	public String dayofweek;
	
	@Field
	public String hourofday;
	
	@Field
	public String method;
	
	@Field
	public String url;
	
	@Field
	public String filetype;
	
	@Field
	public String protocol;
	
	@Field
	public String responsecode;
	
	@Field
	public String bytes;
	
	@Field
	public String referrer;
	
	@Field
	public String useragent;
	
	public LogLineBean() {
		idCounter++;
		id = "" + idCounter;
	}
	
}
