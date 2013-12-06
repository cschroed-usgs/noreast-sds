package gov.usgs.noreast.noreastsds.model.SOS.components;

public class SOSTimeValue {
	private String timeText;
	private long time;
	private double value;
	
	public SOSTimeValue(String timeText, long time, double value) {
		this.timeText = timeText;
		this.time = time;
		this.value = value;
	}

	public String getTimeText() {
		return timeText;
	}

	public void setTimeText(String timeText) {
		this.timeText = timeText;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		StringBuffer results = new StringBuffer();
		
		results.append("TIME: [" + this.time + "] - VALUE: [" + this.value + "]");
		
		return results.toString();
	}
	
	public String exportXML(int indent, boolean numeric) {
		StringBuffer results = new StringBuffer();
		
		String timeString = this.timeText;
		if(numeric) {
			timeString = "" + this.time;
		}
		
		results.append("<result>\n");
		for(int i = 0; i < (indent + 1); i++) {
			results.append("\t");
		}
		results.append("<time>" + timeString + "</time>\n");
		for(int i = 0; i < (indent + 1); i++) {
			results.append("\t");
		}
		results.append("<value>" + this.value + "</value>\n");
		for(int i = 0; i < indent; i++) {
			results.append("\t");
		}
		results.append("</result>\n");
		
		
		return results.toString();
	}
	
	public String exportJSON(boolean numeric) {
		StringBuffer results = new StringBuffer();
		
		String timeString = this.timeText;
		if(numeric) {
			timeString = "" + this.time;
		}
		
		results.append("{\"result\" : {\n\t\"time\":\"" + timeString + "\",\n\t\"value\":\"" + this.value + "\"\n\t}\n}");
		
		return results.toString();
	}
	
	public String exportCSV(boolean numeric) {
		StringBuffer results = new StringBuffer();
		
		String timeString = this.timeText;
		if(numeric) {
			timeString = "" + this.time;
		}
		
		results.append(timeString + "," + this.value + "\n");
		
		return results.toString();
	}
}
