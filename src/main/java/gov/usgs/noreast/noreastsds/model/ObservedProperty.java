package gov.usgs.noreast.noreastsds.model;

public class ObservedProperty {
	private String name;
	private String parameter;
	
	public ObservedProperty(String name, String parameter) {
		this.name = name;
		this.parameter = parameter;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getParameter() {
		return parameter;
	}
	
	public void setParameter(String parameter) {
		this.parameter = parameter;
	}	
}
