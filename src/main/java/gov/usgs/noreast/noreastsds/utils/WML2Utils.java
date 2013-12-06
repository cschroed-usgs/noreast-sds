package gov.usgs.noreast.noreastsds.utils;

import gov.usgs.noreast.noreastsds.model.SOS.SOSResult;
import gov.usgs.noreast.noreastsds.model.SOS.components.SOSPhenomenonTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class WML2Utils {
	private static Logger log = WebsiteUtils.getLogger(WML2Utils.class);
	
	/**
	 * wml2:time Date Format
	 * 		2012-10-03T00:00:00-05:00
	 */
	public static final String WML2_TIME_FORMAT		= "yyyy-MM-dd'T'HH:mm:ss";
	
	/**
	 * GML Tags
	 */
	public static final String GML_IDENTIFIER		= "gml:identifier";    
    public static final String GML_NAME 			= "gml:name";
    
    /**
     * WML2 Tags
     */
    public static final String WML2_METADATA		= "wml2:metadata";
    public static final String WML2_OM				= "wml2:observationMember";
    
    /**
     * Observation Member Tags (data content)
     */
    public static final String OM_OBSERVATION		= "om:OM_Observation";
    public static final String OM_FOI		 		= "om:featureOfInterest";
    public static final String OM_RESULT 			= "om:result";
    
    /**
     * Observation Tags
     */
    public static final String O_PHENOMENOMTIME		= "om:phenomenonTime";
    
    /**
     * Phenomenon Time Tags
     */
    public static final String PT_TIMEPERIOD		= "gml:TimePeriod";
    public static final String PT_BEGINPOSITION		= "gml:beginPosition";
    public static final String PT_ENDPOSITION		= "gml:endPosition";    
    
    /**
     * Feature of Interest (Location data)
     */
    public static final String FOI_MP				= "wml2:MonitoringPoint";
    public static final String FOI_MP_ID_ATTR		= "gml:id";
    public static final String FOI_SF				= "sa:sampledFeature";
    public static final String FOI_SF_TITLE_ATTR	= "xlink:title";
    public static final String FOI_SHAPE			= "sams:shape";
    public static final String FOI_POINT			= "gml:point";
    public static final String FOI_POINT_ID_ATTR	= "gml:id";
    public static final String FOI_POSITION			= "gml:pos";
    
    /**
     * Result
     */
    public static final String RESULT_MTS 			= "wml2:MeasurementTimeseries";
    
    /**
     * Measurement Time Series
     */
    public static final String MTS_DPMETADATA		= "wml2:defaultPointMetadata";
    public static final String MTS_POINT 			= "wml2:point";
    
    /**
     * Point
     */
    public static final String POINT_MTVP 			= "wml2:MeasurementTVP";
    
    /**
     * Measurement Time Value Pair
     */
    public static final String MTVP_TIME 			= "wml2:time";
    public static final String MTVP_VALUE 			= "wml2:value";
    
    /**
     * getChildNodeByName() returns a direct child node with the name "name"
     * 
     * @param document
     * @param name
     * @return Node
     */
    public static Node getChildNodeByName(Node node, String name) {
    	NodeList childNodes = node.getChildNodes();
    	
	    for(int i = 0; i < childNodes.getLength(); i++) {
	    	Node child = childNodes.item(i);
			if (child instanceof Element) {
				if(((Element)child).getTagName().equals(name)) {
					return child;					
				}
			}
	    }
	    
	    return null;
    }
    
    /**
     * getChildNodesByName() returns a list of child nodes with the name "name"
     * 
     * @param document
     * @param name
     * @return List<Node>
     */
    public static List<Node> getChildNodesByName(Node node, String name) {
    	List<Node> nodes = new ArrayList<Node>();
    	
    	NodeList childNodes = node.getChildNodes();
    	
	    for(int i = 0; i < childNodes.getLength(); i++) {
	    	Node child = childNodes.item(i);
			if (child instanceof Element) {
				if(((Element)child).getTagName().equals(name)) {
					nodes.add(child);					
				}
			}
	    }
	    
	    return nodes;
    }
    
    public static String getAttributeValue(Node node, String attributeName) {
    	NamedNodeMap attribs = node.getAttributes();
		for(int i = 0; i < attribs.getLength(); i++) {
			Node attrib = attribs.item(i);
			
			if((attrib != null) && (attrib.getNodeType() == Node.ATTRIBUTE_NODE)) {
				if(attrib.getNodeName().equals(attributeName)) {
					String value = attrib.getNodeValue();
					
					if(value == null) {
						return "";
					}
					
					return value;
				}
			}
		}
		
		return "";
    }
    
    
    /**
     * isDateValid() returns true if the date value it valid
     * @param date
     * @return true/false
     * 
     * The <wml2:time> element looks like 2012-10-03T00:00:00-05:00
     */
    public static long isDateValid(String dateString) {
    	SimpleDateFormat sdf = new SimpleDateFormat(WML2Utils.WML2_TIME_FORMAT);
		sdf.setLenient(false);
		
		long milliseconds = 0;
 
		try { 
			//if not valid, it will throw ParseException//
			Date date = sdf.parse(dateString);
			milliseconds = date.getTime();
		} catch (ParseException e) {
			WML2Utils.log.error("WML2Utils.isDateValid() Exception: Date [" + dateString + "] is an invalid date");
			return -1;
		}
		
		return milliseconds;
    }
    
    public static SOSPhenomenonTime getBestEncompassingDate(List<SOSResult> results) {
    	long earliestBeginDate = WML2Utils.getEarliestBeginDate(results);
    	long latestEndDate = WML2Utils.getLatestEndDate(results);
    	
    	// Something went wrong with our latest end date... lets make it 1 year
    	// past the earliest begin date
    	if(latestEndDate < earliestBeginDate) {
    		//                                    ms    s    m    h     d  = 1 year
    		latestEndDate = earliestBeginDate + (1000 * 60 * 60 * 24 * 365);
    		
    		long now = new Date().getTime();
    		if(now < latestEndDate) {
    			latestEndDate = now;
    		}
    	}
    	
    	return new SOSPhenomenonTime(earliestBeginDate, latestEndDate);
    }
    
    /**
     * Given a result set, will return the latest endTime for all results
     * @param results
     * @return
     */
    public static long getLatestEndDate(List<SOSResult> results) {
    	long currentLatestDate = 0;
    	
    	Iterator<SOSResult> resultsItr = results.iterator();
    	while(resultsItr.hasNext()) {
    		SOSResult result = resultsItr.next();
    		
    		long resultTime = result.getPhenomenonTime().getEndTime();
    		if(resultTime > currentLatestDate) {
    			currentLatestDate = resultTime;
    		}
    	}
    	
    	// We really shouldnt do ALL time
    	if(currentLatestDate == 0) {
    		//											 ms    s    m    h     d  = 1 year
    		currentLatestDate = new Date().getTime() - (1000 * 60 * 60 * 24 * 365);
    	}
    	
    	return currentLatestDate;
    }
    
    /**
     * Given a result set, will return the earliest beginTime for all results
     * @param results
     * @return
     */
    public static long getEarliestBeginDate(List<SOSResult> results) {
    	long currentEarliestDate = new Date().getTime();
    	
    	Iterator<SOSResult> resultsItr = results.iterator();
    	while(resultsItr.hasNext()) {
    		SOSResult result = resultsItr.next();
    		
    		long resultTime = result.getPhenomenonTime().getBeginTime();
    		if(resultTime < currentEarliestDate) {
    			currentEarliestDate = resultTime;
    		}
    	}
    	
    	return currentEarliestDate;
    }
}
