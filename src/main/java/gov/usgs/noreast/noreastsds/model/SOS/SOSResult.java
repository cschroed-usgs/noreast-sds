package gov.usgs.noreast.noreastsds.model.SOS;

import gov.usgs.noreast.noreastsds.model.SOS.components.SOSPhenomenonTime;
import gov.usgs.noreast.noreastsds.model.SOS.components.SOSSiteLocation;
import gov.usgs.noreast.noreastsds.model.SOS.components.SOSTimeValue;
import gov.usgs.noreast.noreastsds.model.SOS.components.comparator.SOSTimeValueComparator;
import gov.usgs.noreast.noreastsds.utils.WML2Utils;
import gov.usgs.noreast.noreastsds.utils.WebsiteUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


public class SOSResult {
	private static Logger log = WebsiteUtils.getLogger(SOSResult.class);
	
	private int resultId;
	private String siteId;
	private String observedProperty;
	private String offering;
	private String rawResult;
	private boolean hasResult;
	
	private String queryURL;
	
	/**
	 * Content info
	 */
	private String charSet = "utf-8";
	
	/**
	 * XML Elements we are interested in
     * 
     * 			OM_OBSERVATION
     * 				OM_RESULT
     * 					RESULT_MTS
     * 						MTS_POINT
     * 							POINT_MTVP
     * 								MTVP_TIME
     * 								MTVP_VALUE
	 */
	private Node observationMember = null;			// wml2:observationMember
	private Node featureOfInterest = null;			// om:featureOfInterest
	private Node observation = null;				// om:OM_Observation
	private Node omResult = null;					// om:result
	private Node resultTimeSeries = null;			// wml2:MeasurementTimeseries
	
	/**
	 * Result Members
	 */
	private SOSSiteLocation location = null;
	private SOSPhenomenonTime phenomenonTime = null;
	private List<SOSTimeValue> resultSeries;
	
	/*
		String xml = "<resp><status>good</status><msg>hi</msg></resp>";
		
		InputSource source = new InputSource(new StringReader(xml));
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document document = db.parse(source);
		
		XPathFactory xpathFactory = XPathFactory.newInstance();
		XPath xpath = xpathFactory.newXPath();
		
		String msg = xpath.evaluate("/resp/msg", document);
		String status = xpath.evaluate("/resp/status", document);
		
		System.out.println("msg=" + msg + ";" + "status=" + status);
	 */
	
	public SOSResult(String siteid, String observedProperty, String offering, String wpsURI) {
		this.siteId = siteid;
		this.observedProperty = observedProperty;
		this.offering = offering;
		this.rawResult = "";
		this.hasResult = false;
		this.resultSeries = new ArrayList<SOSTimeValue>();
		
		/**
		 *  Query looks like:
		 *  		http://cida-wiwsc-javadevp.er.usgs.gov:8080/noreast-sos/simple?request=GetObservation&featureID=MD-DNR-NBP0689&offering=MAX&observedProperty=WATER&beginPosition=2000-08-01T00:00:00&endPosition=2013-08-02T00:00:00
		 *  
		 *  Passed in wpsURI has all but the beginPosition and endPosition parameters
		 */
		this.queryURL = wpsURI;
		
		this.location = null;
		this.phenomenonTime = null;
		
		this.resultId = ("" + this.siteId + this.observedProperty + this.offering + "").hashCode();
	}

	public int getResultId() {
		return resultId;
	}

	public String getSiteId() {
		return siteId;
	}

	public void setSiteId(String siteId) {
		this.siteId = siteId;
	}

	public String getObservedProperty() {
		return observedProperty;
	}

	public void setObservedProperty(String observedProperty) {
		this.observedProperty = observedProperty;
	}

	public String getOffering() {
		return offering;
	}

	public void setOffering(String offering) {
		this.offering = offering;
	}

	public String getRawResult() {
		return rawResult;
	}

	public void setRawResult(String rawResult, boolean resultsOnly) {
		this.rawResult = rawResult;
		parseResult(resultsOnly);
	}

	public boolean hasResults() {
		return hasResult;
	}

	public String getQueryURL() {
		return queryURL;
	}

	public String getCharSet() {
		return charSet;
	}

	public void setCharSet(String charSet) {
		this.charSet = charSet;
	}

	public Node getObservationMember() {
		return observationMember;
	}

	public Node getFeatureOfInterest() {
		return featureOfInterest;
	}

	public Node getObservation() {
		return observation;
	}

	public Node getOmResult() {
		return omResult;
	}

	public Node getResultTimeSeries() {
		return resultTimeSeries;
	}

	public SOSSiteLocation getLocation() {
		return location;
	}

	public SOSPhenomenonTime getPhenomenonTime() {
		return phenomenonTime;
	}

	public List<SOSTimeValue> getResultSeries() {
		return resultSeries;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("SOSResult [" + this.resultId + "]:\n");
		sb.append("\tSiteID:\t\t" + this.siteId + "\n");
		sb.append("\tObservedProperty:\t" + this.observedProperty + "\n");
		sb.append("\tOffering:\t\t" + this.offering + "\n");
		sb.append("\tSOS URI:\t\t" + this.queryURL + "\n");
		
		if(this.location != null) {
			sb.append("\tLOCATION: " + this.location);
		}
		
		if(this.phenomenonTime != null) {
			sb.append("\tPHENOMTIME:\t\t" + this.phenomenonTime + "\n");
		}
		
		//sb.append("\tRESULT: [" + this.rawResult + "]");
		return sb.toString();
	}
	
	public String exportData(String resultType, boolean resultsOnly, boolean numeric) {
		if(resultType != null) {
			if(resultType.equals("xml".toLowerCase())) {
				return exportXML(resultsOnly, numeric);
			} else if(resultType.equals("json".toLowerCase())) {
				return exportJSON(resultsOnly, numeric);
			} else if(resultType.equals("csv".toLowerCase())) {
				return exportCSV(resultsOnly, numeric);
			}
		}
		
		return this.toString();
	}
	
	private String exportXML(boolean resultsOnly, boolean numeric) {
		StringBuffer results = new StringBuffer();
		
		// TODO Create a full result XML export
		if(!resultsOnly) {
			
		}
		
		results.append("<results>\n");
		Iterator<SOSTimeValue> resultSeriesItr = resultSeries.iterator();
		while(resultSeriesItr.hasNext()) {
			SOSTimeValue tv = resultSeriesItr.next();
			results.append("\t" + tv.exportXML(1, numeric));			
		}
		results.append("</results>");
		
		return results.toString();
	}
	
	private String exportJSON(boolean resultsOnly, boolean numeric) {
		StringBuffer results = new StringBuffer();

		// TODO Create a full result XML export
		if(!resultsOnly) {
			
		}
		
		results.append("{\"results\" : [");
		Iterator<SOSTimeValue> resultSeriesItr = resultSeries.iterator();
		while(resultSeriesItr.hasNext()) {
			SOSTimeValue tv = resultSeriesItr.next();
			results.append(tv.exportJSON(numeric));
			
			if(resultSeriesItr.hasNext()) {
				results.append(",\n");
			}
		}
		results.append("\n\t]\n}\n");
		
		return results.toString();
	}
	
	private String exportCSV(boolean resultsOnly, boolean numeric) {
		StringBuffer results = new StringBuffer("time,value\n");

		// CSV makes no sense if not resultsOnly
		
		Iterator<SOSTimeValue> resultSeriesItr = resultSeries.iterator();
		while(resultSeriesItr.hasNext()) {
			SOSTimeValue tv = resultSeriesItr.next();
			results.append(tv.exportCSV(numeric));			
		}
		
		return results.toString();
	}
	
	/**
	 * Parse the results
	 * @param resultsOnly - if true will only parse the time/value results
	 */
	private void parseResult(boolean resultsOnly) {
		if((this.rawResult == null) || (this.rawResult.equals(""))) {
			this.hasResult = false;
			return;
		}
		
		/**
    	 * These responses are relatively small (we are only asking for LAST result)
    	 * 
    	 * Use DOM for these.
    	 * 
    	 * When we get LARGE results, use SAX
    	 */
		
		/**
		 * EMPTY RESULT:
		 * 
		 * <wml2:Collection xmlns:gml="http://www.opengis.net/gml/3.2"
		 * xmlns:om="http://www.opengis.net/om/2.0"
		 * xmlns:sa="http://www.opengis.net/sampling/2.0"
		 * xmlns:swe="http://www.opengis.net/swe/2.0"
		 * xmlns:xlink="http://www.w3.org/1999/xlink"
		 * xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
		 * xmlns:wml2="http://www.opengis.net/waterml/2.0"
		 * xmlns:gmd="http://www.isotc211.org/2005/gmd"
		 * xmlns:gco="http://www.isotc211.org/2005/gco"
		 * xmlns:sf="http://www.opengis.net/sampling/2.0"
		 * xmlns:sams="http://www.opengis.net/samplingSpatial/2.0"
		 * xsi:schemaLocation
		 * ="http://www.opengis.net/waterml/2.0 ../waterml2.xsd"/>
		 * 
		 * 
		 */
		
		/**
		 * NON-EMPTY RESULT:
		 * 
		 * <wml2:Collection xmlns:gml="http://www.opengis.net/gml/3.2"
		 * 	xmlns:om="http://www.opengis.net/om/2.0" xmlns:sa="http://www.opengis.net/sampling/2.0"
		 * 	xmlns:swe="http://www.opengis.net/swe/2.0" xmlns:xlink="http://www.w3.org/1999/xlink"
		 * 	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:wml2="http://www.opengis.net/waterml/2.0"
		 * 	xmlns:gmd="http://www.isotc211.org/2005/gmd" xmlns:gco="http://www.isotc211.org/2005/gco"
		 * 	xmlns:sf="http://www.opengis.net/sampling/2.0" xmlns:sams="http://www.opengis.net/samplingSpatial/2.0"
		 * 	xsi:schemaLocation="http://www.opengis.net/waterml/2.0 ../waterml2.xsd">
		 * 	<gml:identifier codeSpace="http://cida.usgs.gov/noreast" />
		 * 	<gml:name codeSpace="http://cida.usgs.gov/noreast">NBP0689</gml:name>
		 * 	<wml2:metadata>
		 * 		<wml2:DocumentMetadata gml:id="doc.MP.NOREAST.MD-DNR-NBP0689">
		 * 			<gml:metaDataProperty about="contact"
		 * 				xlink:href="http://cida.usgs.gov/noreast" />
		 * 			<wml2:generationDate>2013-11-21T14:52:59-05:00</wml2:generationDate>
		 * 			<wml2:version xlink:href="http://www.opengis.net/waterml/2.0"
		 * 				xlink:title="WaterML 2.0" />
		 * 		</wml2:DocumentMetadata>
		 * 	</wml2:metadata>
		 * 	<wml2:observationMember>
		 * 		<om:OM_Observation gml:id="obs.NOREAST.MD-DNR-NBP0689.DAILYMAX.WATER">
		 * 			<om:phenomenonTime>
		 * 				<gml:TimePeriod>
		 * 					<gml:beginPosition>2011-04-12T00:00:00-05:00</gml:beginPosition>
		 * 					<gml:endPosition>2012-10-03T00:00:00-05:00</gml:endPosition>
		 * 				</gml:TimePeriod>
		 * 			</om:phenomenonTime>
		 * 			<om:resultTime>
		 * 				<gml:TimeInstant
		 * 					gml:id="requested_time.NOREAST.MD-DNR-NBP0689.DAILYMAX.WATER">
		 * 					<gml:timePosition>2013-11-21T14:52:59-05:00</gml:timePosition>
		 * 				</gml:TimeInstant>
		 * 			</om:resultTime>
		 * 			<om:procedure>
		 * 				<wml2:ObservationProcess
		 * 					gml:id="process.NOREAST.MD-DNR-NBP0689.DAILYMAX.WATER">
		 * 					<wml2:processType
		 * 						xlink:href="http://www.opengis.net/def/waterml/2.0/processType/Sensor"
		 * 						xlink:title="Sensor" />
		 * 					<wml2:parameter xlink:title="Statistic"
		 * 						xlink:href="http://cida.usgs.gov/noreast/offering/DAILYMAX" />
		 * 				</wml2:ObservationProcess>
		 * 			</om:procedure>
		 * 			<om:observedProperty xlink:title="WATER"
		 * 				xlink:href="http://cida.usgs.gov/noreast/observedProperty/WATER" />
		 * 		</om:OM_Observation>
		 * 		<om:featureOfInterest xlink:title="NBP0689">
		 * 			<wml2:MonitoringPoint
		 * 				gml:id="Maryland Department of Natural Resources.MP.NOREAST.MD-DNR-NBP0689.DAILYMAX.WATER">
		 * 				<gml:descriptionReference xlink:href="http://cida.usgs.gov/noreast" />
		 * 				<sa:sampledFeature xlink:title="North Branch Potomac River" />
		 * 				<sams:shape>
		 * 					<gml:point
		 * 						gml:id="Maryland Department of Natural Resources.P.NOREAST.MD-DNR-NBP0689.DAILYMAX.WATER">
		 * 						<gml:pos srsName="urn:ogc:def:crs:EPSG:4326">39.388280000000002 -79.180019999999999</gml:pos>
		 * 					</gml:point>
		 * 				</sams:shape>
		 * 			</wml2:MonitoringPoint>
		 * 		</om:featureOfInterest>
		 * 		<om:result>
		 * 			<wml2:MeasurementTimeseries
		 * 				gml:id="TS.NOREAST.MD-DNR-NBP0689.DAILYMAX.WATER">
		 * 				<wml2:defaultPointMetadata>
		 * 					<wml2:DefaultTVPMeasurementMetadata>
		 * 						<wml2:uom xlink:title="C" />
		 * 						<wml2:interpolationType
		 * 							xlink:href="http://www.opengis.net/def/waterml/2.0/interpolationType/MaxSucc"
		 * 							xlink:title="Maximum in Succeeding Interval" />
		 * 					</wml2:DefaultTVPMeasurementMetadata>
		 * 				</wml2:defaultPointMetadata>
		 * 				<wml2:point>
		 * 					<wml2:MeasurementTVP>
		 * 						<wml2:time>2012-10-03T00:00:00-05:00</wml2:time>
		 * 						<wml2:value>16.82</wml2:value>
		 * 					</wml2:MeasurementTVP>
		 * 				</wml2:point>
		 * 			</wml2:MeasurementTimeseries>
		 * 		</om:result>
		 * 	</wml2:observationMember>
		 * </wml2:Collection>
		 * 
		 */
		
		//Get the DOM Builder Factory
	    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

	    //Get the DOM Builder
	    DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			String msg = "SOSResult.parseResult() ParserConfigurationException - {" + e.getMessage() + "}";
			log.error(msg);
			
			this.hasResult = false;
			return;
		}

	    //Load and Parse the XML document
	    //document contains the complete XML as a Tree.
	    Document document = null;
	    ByteArrayInputStream bais = null;
		try {
			bais = new ByteArrayInputStream(this.rawResult.getBytes(charSet));
			document = builder.parse(new InputSource(bais));
		} catch (SAXException es) {
			String msg = "SOSResult.parseResult() SAXException - {" + es.getMessage() + "}";
			log.error(msg);
			
			this.hasResult = false;
			return;
		} catch (IOException ei) {
			String msg = "SOSResult.parseResult() IOException - {" + ei.getMessage() + "}";
			log.error(msg);
			
			this.hasResult = false;
			return;
		} finally {
			if(bais != null) {
				try {
					bais.close();
				} catch (IOException e) {
					log.error("SOSResult.parseResult() IOException closing ByteArrayInputStream - {" + e.getMessage() + "}");
				}
			}
		}

	    /**
	     * What we are looking for is a MTVP (Measurement Time Value Pair).  If
	     * there is one then we know this is a query that has data associated
	     * with it.
	     * 
	     * A MTVP has a path in the tree as follows:
	     * 
	     * 			WML2_OM
	     * 				OM_FOI
	     * 				OM_RESULT
	     * 					RESULT_MTS
	     * 						MTS_POINT
	     * 							POINT_MTVP
	     * 								MTVP_TIME
	     * 								MTVP_VALUE
	     * 
	     * The value of the MTVP can be anything, we only care about a non-empty
	     * time.
	     */
		/**
		 * See if there is an Observation Member in this document
		 */
		Node omNode = WML2Utils.getChildNodeByName(document.getDocumentElement(), WML2Utils.WML2_OM);
		if(omNode == null) {
			this.hasResult = false;
			return;
		}
		this.observationMember = omNode;
		
		/**
		 * See if there is a result in this Observation Member
		 */
		Node resultNode = WML2Utils.getChildNodeByName(omNode, WML2Utils.OM_RESULT);
		if(resultNode == null) {
			this.observationMember = null;
			this.hasResult = false;
			return;
		}
		this.omResult = resultNode;
		
		/**
		 * See if there is a Measurement Time Series in this Result
		 */
		Node tsNode = WML2Utils.getChildNodeByName(resultNode, WML2Utils.RESULT_MTS);
		if(tsNode == null) {
			this.observationMember = null;
			this.omResult = null;
			this.hasResult = false;
			return;
		}
		this.resultTimeSeries = tsNode;
		
		/**
		 * Now that we know there is a time series, lets see if there is a valid
		 * point in the series.
		 */
		if(!populateSeriesResults(tsNode)) {
			this.observationMember = null;
			this.omResult = null;
			this.resultTimeSeries = null;
			this.hasResult = false;
			return;
		}
		
		/**
		 * Now that we know this is a valid result, lets populate the site
		 * location and data time (phenomenon time).
		 * 
		 * For Location: 
		 * 		Using the base observationMember Node, lets get the
		 * 		featureOfInterest Node and then create a location object based
		 * 		on that Node's content.
		 * 
		 * For Data Time:
		 * 		Using the base observationMember Node, lets get the Observation
		 * 		Node, then the phenomenonTime Node, then the TimePeriod Node and
		 * 		then create	a phenomenom time object based on that Node's content
		 */
		if(!resultsOnly){
			/**
			 * Get Location
			 */
			Node foiNode = WML2Utils.getChildNodeByName(omNode, WML2Utils.OM_FOI);
			if(foiNode != null) {
				this.featureOfInterest = foiNode;
				this.location = new SOSSiteLocation(foiNode);
			}
			
			/**
			 * Get Phenomenon Time
			 */
			Node oNode = WML2Utils.getChildNodeByName(omNode, WML2Utils.OM_OBSERVATION);
			if(oNode != null) {
				this.observation = oNode;
				this.phenomenonTime = new SOSPhenomenonTime(oNode);
			}
			
		} else {
			this.featureOfInterest = null;
			this.location = new SOSSiteLocation("", "", "", "");
		}
	    
		this.hasResult = true;
	}
	
	/**
	 * Given the tsNodeList, lets populate our time series result list
	 * @param tsNodeList
	 * @return true if there are results, false if no results
	 */
	private boolean populateSeriesResults(Node tsNode) {
		if(resultSeries == null) {
			resultSeries = new ArrayList<SOSTimeValue>();
		} else {
			resultSeries.clear();
		}
		
		/**
		 * Now that we know there is a time series, lets see if there is a point
		 * in the series.
		 */
		Node tsPointNode = WML2Utils.getChildNodeByName(tsNode, WML2Utils.MTS_POINT);
		if(tsPointNode == null) {
			return false;
		}
		
		/**
		 * We have the point node, lets get a list of all the POINT_MTVP elements
		 * inside this node as they contain the actual time/value pairs
		 */
		List<Node> mtvpPointNodes = WML2Utils.getChildNodesByName(tsPointNode, WML2Utils.POINT_MTVP);
		if((mtvpPointNodes == null) || (mtvpPointNodes.size() < 1)) {
			return false;
		}
	    
		/**
		 * We have a list of points, lets loop through all of them and extract the
		 * time/value pairs
		 */
		Iterator<Node> mtvpPointNodeItr = mtvpPointNodes.iterator();
		while(mtvpPointNodeItr.hasNext()) {
			Node tvPairNode = mtvpPointNodeItr.next();
			
			/**
			 * We have a Time Value Pair, lets see if there is a time node
			 */
			Node timeNode = WML2Utils.getChildNodeByName(tvPairNode, WML2Utils.MTVP_TIME);
			if(timeNode == null) {
				continue;
			}
			
			/**
			 * We have a Time Value Pair, lets see if there is a value node
			 */
			Node valueNode = WML2Utils.getChildNodeByName(tvPairNode, WML2Utils.MTVP_VALUE);
			if(valueNode == null) {
				continue;
			}
			
			/**
			 * Now that we have the time node, lets see if its valid
			 */
			String timeValue = timeNode.getTextContent();
			if(timeValue == null) {
				continue;
			}
			
			String valueValue = valueNode.getTextContent();
			if(valueValue == null) {
				valueValue = "";
			}
			
			double numericValue = 0;
			try { 
				numericValue = Double.parseDouble(valueValue);
			} catch (Exception e) {
				log.error("SOSResult.populateSeriesResults() Exception: value [" + valueValue + "] is not a double value!");
			}
			
			/**
			 * Time looks like "2012-10-03T00:00:00-05:00"
			 */
			long date = WML2Utils.isDateValid(timeValue.trim());
			if(date > 0) {
				SOSTimeValue tv = new SOSTimeValue(timeValue, date, numericValue);
				this.resultSeries.add(tv);
			}
		}
		
		if(this.resultSeries.size() > 0) {
			Collections.sort(this.resultSeries, new SOSTimeValueComparator());			
			return true;
		}
		
		
		return false;		
	}
}
