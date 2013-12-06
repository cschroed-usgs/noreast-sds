package gov.usgs.noreast.noreastsds.model.SOS.components;

public class SOSObservationMember {
	private String rawXML;
	/**
		<wml2:observationMember>
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
			<om:featureOfInterest xlink:title="BC-05">
				<wml2:MonitoringPoint gml:id="Baltimore County Department of Environmental Protection and Sustainability.MP.NOREAST.MD-BC-BC-05.RAW.WATER">
					<gml:descriptionReference xlink:href="http://cida.usgs.gov/noreast"/>
					<sa:sampledFeature xlink:title="Unnamed tributary to Prettyboy Reservoir, south of Clipper Mill Road"/>
					<sams:shape>
						<gml:point gml:id="Baltimore County Department of Environmental Protection and Sustainability.P.NOREAST.MD-BC-BC-05.RAW.WATER">
							<gml:pos srsName="urn:ogc:def:crs:EPSG:4326">39.676340000000003 -76.771569999999997</gml:pos>
						</gml:point>
					</sams:shape>
				</wml2:MonitoringPoint>
			</om:featureOfInterest>
			<om:result>...</om:result>
		</wml2:observationMember>
	 */
	
	/**
	 * ObservationMember members
	 */
	private SOSObservation observation;
	private SOSFeatureOfInterest featureOfInterest;
	

}
