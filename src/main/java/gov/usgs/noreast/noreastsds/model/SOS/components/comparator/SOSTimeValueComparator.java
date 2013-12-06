package gov.usgs.noreast.noreastsds.model.SOS.components.comparator;

import gov.usgs.noreast.noreastsds.model.SOS.components.SOSTimeValue;

import java.util.Comparator;

public class SOSTimeValueComparator implements Comparator<SOSTimeValue> {
	@Override
    public int compare(SOSTimeValue o1, SOSTimeValue o2) {
        return (o1.getTime() > o2.getTime() ? -1 : (o1.getTime() == o2.getTime() ? 0 : 1));
    }
}
