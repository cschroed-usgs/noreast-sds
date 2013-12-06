package gov.usgs.noreast.noreastsds.factories;

import gov.usgs.noreast.noreastsds.model.Offering;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OfferingFactory {
	private static final OfferingFactory INSTANCE = new OfferingFactory();        
    private static Map<String, Offering> offeringMap;
    
    private OfferingFactory() {
    	/**
    	 * TODO
    	 * 
    	 * Make the instantiation and/or retrieval of these dynamic
    	 */
        OfferingFactory.offeringMap = new HashMap<String, Offering>();
        offeringMap.put("RAW", new Offering("RAW", "RAW"));
        offeringMap.put("MIN", new Offering("MIN", "MIN"));
        offeringMap.put("MAX", new Offering("MAX", "MAX"));
    }
    
    public static OfferingFactory getInstance() {
        return INSTANCE;
    }
    
    public static void addOffering(String name, String param) {                
        Offering Offering = new Offering(name, param);
        
        if(Offering != null) {
                offeringMap.put(param, Offering);
        }
    }
    
    public Offering getOffering(String OfferingName) {
        return offeringMap.get(OfferingName);
    }
    
    public List<Offering> getOfferings() {
        List<String> keys = new ArrayList<String>(offeringMap.keySet());
        Collections.sort(keys);
        
        List<Offering> Offerings = new ArrayList<Offering>();
        for(String key : keys) {
                Offerings.add(offeringMap.get(key));
        }
        
        return Offerings;            
    }
    
    public Map<String, Offering> getOfferingsMap() {
        return offeringMap;            
    }
}
