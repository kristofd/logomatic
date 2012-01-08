package no.bekk.logomatic;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.impl.BinaryRequestWriter;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;

public class SolrIntegration {
	
	CommonsHttpSolrServer server = null;

	static final Logger log = Logger.getLogger(SolrIntegration.class);
	
	public SolrIntegration(String url) {
		try {
			server = new CommonsHttpSolrServer(url);
			server.setRequestWriter(new BinaryRequestWriter());
		} catch (Exception e) {
			log.error("Error creating Solr integration: " + e);
		}
	}

	
	public void send(List<LogLineBean> docs) {
		try {
			server.addBeans(docs);
			server.commit();
		} catch (Exception e) {
			log.error("Error sending log lines: " + e);
		}
	}

}
