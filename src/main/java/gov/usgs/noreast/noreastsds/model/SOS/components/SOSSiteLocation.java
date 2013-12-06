package gov.usgs.noreast.noreastsds.model.SOS.components;

import gov.usgs.noreast.noreastsds.utils.WML2Utils;

import org.w3c.dom.Node;

public class SOSSiteLocation {
	private boolean hasLocationInfo = false;
	private String monitoringPointID = "";
	private String pointId = "";
	private String featureTitle = "";
	private String latitude = "";
	private String longitude = "";
	
	/**
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
	 * 
	 * 	A Feature of Interest has a path in the tree as follows:
     * 
     * 			FOI_MP
     * 			FOI_MP / FOI_MP_ID_ATTR
     * 				FOI_SF
     * 				FOI_SF / FOI_SF_TITLE_ATTR
     *				FOI_SHAPE
     *					FOI_POINT
     *					FOI_POINT / FOI_POINT_ID_ATTR
     * 						FOI_POSITION
     * 
	 */
	public SOSSiteLocation(Node featureOfInterest) {
		/**
		 * We need to parse this node to retrieve all of the necessary data
		 */
		Node mpNode = WML2Utils.getChildNodeByName(featureOfInterest, WML2Utils.FOI_MP);
		if(mpNode != null) {
			// Monitoring Point ID looks like "Maryland Department of Natural Resources.MP.NOREAST.MD-DNR-NBP0689.DAILYMAX.WATER"
			String mpId = WML2Utils.getAttributeValue(mpNode, WML2Utils.FOI_MP_ID_ATTR);
			if (mpId != null) {
				if(mpId.contains(".MP.")) {
					String[] parts = mpId.split(".MP.");
					
					if(parts.length > 0) {
						mpId = parts[0];
					}
				}
				
				this.monitoringPointID = mpId;
			}
			
			Node sfNode = WML2Utils.getChildNodeByName(mpNode, WML2Utils.FOI_SF);
			if(sfNode != null) {
				this.featureTitle = WML2Utils.getAttributeValue(sfNode, WML2Utils.FOI_SF_TITLE_ATTR);
			}
			
			Node shapeNode = WML2Utils.getChildNodeByName(mpNode, WML2Utils.FOI_SHAPE);
			if(shapeNode != null) {
				Node pointNode = WML2Utils.getChildNodeByName(shapeNode, WML2Utils.FOI_POINT);
				if(pointNode != null) {
					// Point ID looks like "Maryland Department of Natural Resources.P.NOREAST.MD-DNR-NBP0689.DAILYMAX.WATER"
					String pId = WML2Utils.getAttributeValue(pointNode, WML2Utils.FOI_POINT_ID_ATTR);
					if (pId != null) {
						if(pId.contains(".P.")) {
							String[] parts = pId.split(".P.");
							
							if(parts.length > 0) {
								pId = parts[0];
							}
						}
						
						this.pointId = pId;
					}
					
					Node positionNode = WML2Utils.getChildNodeByName(pointNode, WML2Utils.FOI_POSITION);
					if(positionNode != null) {
						String positionContent = positionNode.getTextContent().trim();
						
						if ((positionContent != null) && (positionContent.contains(" "))) {
							String[] parts = positionContent.split(" ");
							
							if(parts.length == 2) {
								this.latitude = parts[0];
								this.longitude = parts[1];
							}
						}
					}
				}
			}
		}
		
		validateLocation();
	}
	
	public SOSSiteLocation(String id, String title, String lat, String lng) {
		this.pointId = id;
		this.featureTitle = title;
		this.latitude = lat;
		this.longitude = lng;
		
		validateLocation();
	}
	
	public boolean hasLocation() {
		return hasLocationInfo;
	}

	public String getMonitoringPointID() {
		return monitoringPointID;
	}

	public String getPointId() {
		return pointId;
	}

	public String getFeatureTitle() {
		return featureTitle;
	}

	public String getLatitude() {
		return latitude;
	}

	public String getLongitude() {
		return longitude;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("\n");
		
		sb.append("\t\tmonitoringPointID: " + this.monitoringPointID + "\n");
		sb.append("\t\tpointId: " + this.pointId + "\n");
		sb.append("\t\tfeatureTitle: " + this.featureTitle + "\n");
		sb.append("\t\tlatitude: " + this.latitude + "\n");
		sb.append("\t\tlongitude: " + this.longitude + "\n");
		
		return sb.toString();
	}
	
	private void validateLocation() {
		if(((this.pointId != null) && (!this.pointId.equals(""))) && 
		   ((this.featureTitle != null) && (!this.featureTitle.equals(""))) &&
		   ((this.latitude != null) && (!this.latitude.equals(""))) &&
		   ((this.longitude != null) && (!this.longitude.equals("")))) {
			hasLocationInfo = true;
		} else {
			this.pointId = "";
			this.featureTitle = "";
			this.latitude = "";
			this.longitude = "";
			hasLocationInfo = false;
		}
	}
}
