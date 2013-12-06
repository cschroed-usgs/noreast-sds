package gov.usgs.noreast.noreastsds.model.SOS.components;

public class SOSObservation {
	private String rawXML;
	/**
		<om:OM_Observation gml:id="obs.NOREAST.MD-BC-BC-05.RAW.WATER">
			<om:phenomenonTime>
				<gml:TimePeriod>
					<gml:beginPosition>2010-06-01T00:00:00-05:00</gml:beginPosition>
					<gml:endPosition>2010-08-31T00:00:00-05:00</gml:endPosition>
				</gml:TimePeriod>
			</om:phenomenonTime>
			<om:resultTime>
				<gml:TimeInstant gml:id="requested_time.NOREAST.MD-BC-BC-05.RAW.WATER">
					<gml:timePosition>2013-11-01T09:57:55-05:00</gml:timePosition>
				</gml:TimeInstant>
			</om:resultTime>
			<om:procedure>
				<wml2:ObservationProcess gml:id="process.NOREAST.MD-BC-BC-05.RAW.WATER">
					<wml2:processType xlink:href="http://www.opengis.net/def/waterml/2.0/processType/Sensor" xlink:title="Sensor"/>
					<wml2:parameter xlink:title="Statistic" xlink:href="http://cida.usgs.gov/noreast/offering/RAW"/>
				</wml2:ObservationProcess>
			</om:procedure>
			<om:observedProperty xlink:title="WATER" xlink:href="http://cida.usgs.gov/noreast/observedProperty/WATER"/>
		</om:OM_Observation>
	 */

	/**
	 * Observation Members
	 */
	private String observationId;
	private String timePeriodBegin;
	private String timePeriodEnd;
	private String timePosition;
	private String processTypeTitle;
	private String parameterRef;
	private String observedPropertyRef;
}
