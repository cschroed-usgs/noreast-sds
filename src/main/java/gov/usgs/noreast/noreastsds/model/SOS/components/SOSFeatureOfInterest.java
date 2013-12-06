package gov.usgs.noreast.noreastsds.model.SOS.components;

public class SOSFeatureOfInterest {
	private String rawXML;
	/**
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
	 */
	
	/**
	 * Notes on <gml:pos>
	 * 
		Any consumer of data instances compliant with this profile shall be able
		to interpret the following OGC URN.
			urn:ogc:def:crs:EPSG:6.6:4326
		This URN is to be interpreted as well known (i.e. not requiring resolution),
		however, the exact meaning can be obtained by looking up the EPSG Code 4326
		in the Version 6.6 EPSG Coordinate Reference System Database
			An informative description of this CRS is as follows:
				- First coordinate is geographic latitude in degrees
				- Second coordinate is geographic longitude in degrees
	 */
	
	/**
	 * FeatureOfInterest Members
	 */
	private String featureOfInterestTitle;
	private String monitoringPointId;
	private String shapePointId;
	private String sampledFeatureTitle;
	private String positionRaw;
	private Long latitude;
	private Long longitude;
}
