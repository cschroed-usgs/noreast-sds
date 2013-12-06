package gov.usgs.noreast.noreastsds.services.async;

import gov.usgs.noreast.noreastsds.model.SOS.SOSResult;
import gov.usgs.noreast.noreastsds.utils.WebsiteUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

@Component
public class SOSDataQuery {
	private static Logger log = WebsiteUtils.getLogger(SOSDataQuery.class);
	
	/**
     * Call to the sos web service for data
     * 
     * SOS Site Query: 
     * 
     * 		LAST VALUE
     * 		http://cida-wiwsc-javadevp.er.usgs.gov:8080/noreast-sos/simple?request=GetObservation&featureID=MD-DNR-NBP0689&offering=MAX&observedProperty=WATER
     * 
     * 		TIME FRAME
     * 		http://cida-wiwsc-javadevp.er.usgs.gov:8080/noreast-sos/simple?request=GetObservation&featureID=MD-BC-BC-05&offering=RAW&observedProperty=WATER&beginPosition=2010-08-01T00:00:00&endPosition=2010-08-02T00:00:00
     * 
     * 
	 * The Site Data Summary logic follows the following pattern:
	 * 
	 * 		1) Receive site id
	 * 		2) Perform exploratory queries for site against:
	 * 		   http://cida-wiwsc-javadevp.er.usgs.gov:8080/noreast-sos/simple?request=GetObservation&featureID=MD-DNR-NBP0689&offering=MAX&observedProperty=WATER
	 * 				- Each site will be queried for all known "observedProperty"
	 * 				  values.  Currently (11/2013) there are 2:
	 * 						* WATER
	 * 						* AIR
	 * 				- Each observedProperty will be queried for all known
	 * 				  "offering" values.  Currently (11/2013) there are 3:
	 * 						* RAW
	 * 						* MIN
	 * 						* MAX
	 * 				- If there are results for a specific combination, then
	 * 				  that combination will be shown as a possible filter
	 * 				  for the site in charting its data.
	 * 		3) Given results, show filters for chart with a base chart pre-
	 * 		   built for the user.
	 * 
	 */
    @Async
    public Future<SOSResult> asyncQuery(String wpsURL, String siteId, String observedProperty, String offering) {    	
    	String uri = wpsURL + "&featureID=" + siteId + "&offering=" + offering + "&observedProperty=" + observedProperty;
    	
    	SOSResult result = new SOSResult(siteId, observedProperty, offering, uri);
    	
    	log.info("SOS Query: [" + uri + "]");
    	
    	String queryResult = SOSDataQuery.doQuery(result);
    	
        result.setRawResult(queryResult, false);
    	
    	return new AsyncResult<SOSResult>(result);
    }
    
    /**
     * Do direct SOS query (no async)
     * @param wpsURL
     * @param siteId
     * @param observedProperty
     * @param offering
     * @param beginPosition
     * @param endPosition
     * @param resultsOnly
     * @return
     */
    public static SOSResult directQuery(String wpsURL, String siteId, String observedProperty, 
										String offering, String beginPosition, String endPosition,
										boolean resultsOnly) {
    	/**
    	 * Query looks like:
    	 * 
    	 * 		http://cida-wiwsc-javadevp.er.usgs.gov:8080/noreast-sos/simple?request=GetObservation&featureID=MD-BC-BC-05&offering=RAW&observedProperty=WATER&beginPosition=2010-08-01T00:00:00&endPosition=2010-08-02T00:00:00
    	 */
    	String uri = wpsURL + "&featureID=" + siteId + "&offering=" + offering +
    				 "&observedProperty=" + observedProperty + "&beginPosition=" +
    				 beginPosition + "&endPosition=" + endPosition;
    	
    	SOSResult result = new SOSResult(siteId, observedProperty, offering, uri);
    	
    	log.info("SOS Query: [" + uri + "]");
    	
    	String queryResult = SOSDataQuery.doQuery(result);
    	
        result.setRawResult(queryResult, resultsOnly);
    	
    	return result;
    }
    
    private static String doQuery(SOSResult result) {
    	BufferedReader reader = null;
    	OutputStreamWriter wr = null;
    	StringBuffer sb = new StringBuffer();
    	try {	    	
    		URLConnection connection = new URL(result.getQueryURL()).openConnection();
    		InputStream response = connection.getInputStream();
	
	        // Get the response code
    		int status = ((HttpURLConnection)connection).getResponseCode();
    		log.info("SOS Query Response: " + status);
    		
    		if(status == 200) {
	    		// Get the response headers
	    		for (Entry<String, List<String>> header : connection.getHeaderFields().entrySet()) {
	    		    System.out.println(header.getKey() + "=" + header.getValue());
	    		}
	    		
	    		// Get the response content
	    		String contentType = connection.getHeaderField("Content-Type");
	    		String charset = null;
	    		for (String param : contentType.replace(" ", "").split(";")) {
	    		    if (param.startsWith("charset=")) {
	    		        charset = param.split("=", 2)[1];
	    		        break;
	    		    }
	    		}
	
	    		if (charset != null) {
	    			result.setCharSet(charset);
	    		    reader = new BufferedReader(new InputStreamReader(response, charset));
	
			        for (String line; (line = reader.readLine()) != null;) {
			        	sb.append(line);
			        }
	    		} else {
	    			log.warn("SOSDataQuery.doQuery() WARNING : Response content most likely binary.");
	    		}
    		} else {
    			log.error("SOSDataQuery.doQuery() HTTP Result Code != 200 : [" + status + "]");
    			sb.append("UNSUCCESSFUL RESPONSE [" + status + "]");
    		}
    	} catch (MalformedURLException e) {
    		log.error("SOSDataQuery.doQuery() MalformedURLException : " + e.getMessage());
    	} catch (IOException e) {
    		log.error("SOSDataQuery.doQuery() IOException : " + e.getMessage());
		} finally {
			try {
				if(reader != null) {
					reader.close();
				}
				
				if(wr != null) {
					wr.close();
				}
			} catch (IOException e) {
				log.error("SOSDataQuery.doQuery() Closing buffers exception : " + e.getMessage());
			}
		}
    	
    	return sb.toString();
    }
}
