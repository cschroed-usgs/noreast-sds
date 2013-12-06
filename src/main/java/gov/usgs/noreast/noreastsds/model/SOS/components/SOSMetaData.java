package gov.usgs.noreast.noreastsds.model.SOS.components;

public class SOSMetaData {
	private String rawXML;
	/**
		<wml2:metadata>
			<wml2:DocumentMetadata gml:id="doc.MP.NOREAST.MD-BC-BC-05">
				<gml:metaDataProperty about="contact" xlink:href="http://cida.usgs.gov/noreast"/>
				<wml2:generationDate>2013-11-01T09:57:55-05:00</wml2:generationDate>
				<wml2:version xlink:href="http://www.opengis.net/waterml/2.0" xlink:title="WaterML 2.0"/>
			</wml2:DocumentMetadata>
		</wml2:metadata>
	 */
	
	/**
	 * MetaData members
	 */
	private String metaDataId;
	private String generationDate;
	private String version;
	
	
}
