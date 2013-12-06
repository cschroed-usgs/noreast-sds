package gov.usgs.noreast.noreastsds.model.SOS.components;

import gov.usgs.noreast.noreastsds.utils.WML2Utils;
import gov.usgs.noreast.noreastsds.utils.WebsiteUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.w3c.dom.Node;

public class SOSPhenomenonTime {
	private static Logger log = WebsiteUtils.getLogger(SOSPhenomenonTime.class);
	private static int DEFAULT_PERIOD_MS = 1000 * 60 * 60 * 24 * 7;		// 1 week
	
	private long beginTime;
	private long endTime;
	
	private String beginTimeString;
	private String endTimeString;
	
	private String beginTimeQueryString;
	private String endTimeQueryString;
	
	private String beginTimePickerString;
	private String endTimePickerString;
	
	public SOSPhenomenonTime() {
		this.beginTime = 0;
		this.beginTimeString = "";
		this.endTime = 0;
		this.endTimeString = "";
		this.beginTimeString = "";
		this.beginTimePickerString = "";
		this.endTimeString = "";
		this.endTimePickerString = "";
	}
	
	public SOSPhenomenonTime(long begin, long end) {
		this.beginTime = begin;
		this.endTime = end;
		
		Date beginDate = new Date(this.beginTime);
		this.beginTimeString = new SimpleDateFormat("MM/dd/yyyy").format(beginDate);
		
		Date endDate = new Date(this.endTime);
		this.endTimeString = new SimpleDateFormat("MM/dd/yyyy").format(endDate);
		
		setDefaultQueryTime(endDate);
	}
	
	/**
	 * 		<om:OM_Observation gml:id="obs.NOREAST.MD-DNR-NBP0689.DAILYMAX.WATER">
	 * 			<om:phenomenonTime>
	 * 				<gml:TimePeriod>
	 * 					<gml:beginPosition>2011-04-12T00:00:00-05:00</gml:beginPosition>
	 * 					<gml:endPosition>2012-10-03T00:00:00-05:00</gml:endPosition>
	 * 				</gml:TimePeriod>
	 * 			</om:phenomenonTime>
	 * 			<om:resultTime>...</om:resultTime>
	 * 			<om:procedure>...</om:procedure>
	 * 			<om:observedProperty xlink:title="WATER" xlink:href="http://cida.usgs.gov/noreast/observedProperty/WATER"/>
	 * 		</om:OM_Observation>
	 * 
	 * 	A Observation has a path in the tree as follows (that we care about):
     * 
     * 			OM_OBSERVATION
     * 				O_PHENOMENOMTIME
     *					PT_TIMEPERIOD
     * 						PT_BEGINPOSITION
     * 						PT_ENDPOSITION
     * 
	 */
	public SOSPhenomenonTime(Node observation) {
		this.beginTime = 0;
		this.endTime = 0;
		
		Node ptNode = WML2Utils.getChildNodeByName(observation, WML2Utils.O_PHENOMENOMTIME);
		if(ptNode != null) {
			Node tpNode = WML2Utils.getChildNodeByName(ptNode, WML2Utils.PT_TIMEPERIOD);
			if(tpNode != null) {
				/**
				 * We have the time period Node.  Lets get the begin and end
				 * position Nodes.
				 */
				Node beginNode = WML2Utils.getChildNodeByName(tpNode, WML2Utils.PT_BEGINPOSITION);
				if(beginNode != null) {
					String begin = beginNode.getTextContent();
					this.beginTime = WML2Utils.isDateValid(begin.trim());
					
					Date date = new Date(this.beginTime);
					this.beginTimeString = new SimpleDateFormat("MM/dd/yyyy").format(date);
				}
				
				Node endNode = WML2Utils.getChildNodeByName(tpNode, WML2Utils.PT_ENDPOSITION);
				if(endNode != null) {
					String end = endNode.getTextContent();
					this.endTime = WML2Utils.isDateValid(end);
					
					Date date = new Date(this.endTime);
					this.endTimeString = new SimpleDateFormat("MM/dd/yyyy").format(date);
					
					setDefaultQueryTime(date);
				}
			}
		}
	}

	public long getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(long beginTime) {
		this.beginTime = beginTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
	
	public String getBeginTimeString() {
		return beginTimeString;
	}

	public void setBeginTimeString(String beginTimeString) {
		this.beginTimeString = beginTimeString;
	}

	public String getEndTimeString() {
		return endTimeString;
	}

	public void setEndTimeString(String endTimeString) {
		this.endTimeString = endTimeString;
	}

	public String getBeginTimeQueryString() {
		return beginTimeQueryString;
	}

	public void setBeginTimeQueryString(String beginTimeQueryString) {
		this.beginTimeQueryString = beginTimeQueryString;
	}

	public String getEndTimeQueryString() {
		return endTimeQueryString;
	}

	public void setEndTimeQueryString(String endTimeQueryString) {
		this.endTimeQueryString = endTimeQueryString;
	}

	public String getBeginTimePickerString() {
		return beginTimePickerString;
	}

	public void setBeginTimePickerString(String beginTimePickerString) {
		this.beginTimePickerString = beginTimePickerString;
	}

	public String getEndTimePickerString() {
		return endTimePickerString;
	}

	public void setEndTimePickerString(String endTimePickerString) {
		this.endTimePickerString = endTimePickerString;
	}

	@Override
	public String toString() {
		StringBuffer results = new StringBuffer();
		
		results.append("BEGIN TIME: [" + this.beginTime + "] - END TIME: [" + this.endTime + "]");
		
		return results.toString();
	}
	
	public String exportXML(int indent) {
		StringBuffer results = new StringBuffer();
		
		results.append("<phenomenontime>\n");
		for(int i = 0; i < (indent + 1); i++) {
			results.append("\t");
		}
		results.append("<begintime>" + this.beginTime + "</begintime>\n");
		for(int i = 0; i < (indent + 1); i++) {
			results.append("\t");
		}
		results.append("<endtime>" + this.endTime + "</endtime>\n");
		for(int i = 0; i < indent; i++) {
			results.append("\t");
		}
		results.append("</phenomenontime>\n");
		
		
		return results.toString();
	}
	
	public String exportJSON() {
		StringBuffer results = new StringBuffer();
		
		results.append("{\"phenomenontime\" = {\n\t\"begintime\"=\"" + this.beginTime + "\",\n\t\"endtime\"=\"" + this.endTime + "\"\n\t}\n}\n");
		
		return results.toString();
	}
	
	public String exportCSV() {
		StringBuffer results = new StringBuffer();
		
		results.append(this.beginTime + "," + this.endTime + "\n");
		
		return results.toString();
	}
	
	private void setDefaultQueryTime(Date endDate) {
		Date startDate = new Date(endDate.getTime() - DEFAULT_PERIOD_MS);
		
		this.beginTimeQueryString = new SimpleDateFormat(WML2Utils.WML2_TIME_FORMAT).format(startDate);
		this.beginTimePickerString = new SimpleDateFormat("MMMM dd, yyyy").format(startDate);
		
		this.endTimeQueryString = new SimpleDateFormat(WML2Utils.WML2_TIME_FORMAT).format(endDate);
		this.endTimePickerString = new SimpleDateFormat("MMMM dd, yyyy").format(endDate);
	}

}
