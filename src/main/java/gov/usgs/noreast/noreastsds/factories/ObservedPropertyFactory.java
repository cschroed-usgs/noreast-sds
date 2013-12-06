package gov.usgs.noreast.noreastsds.factories;

import gov.usgs.noreast.noreastsds.model.ObservedProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObservedPropertyFactory {
	private static final ObservedPropertyFactory INSTANCE = new ObservedPropertyFactory();        
    private static Map<String, ObservedProperty> propertyMap;
    
    private ObservedPropertyFactory() {
    	/**
    	 * TODO
    	 * 
    	 * Make the instantiation and/or retrieval of these dynamic
    	 */
        ObservedPropertyFactory.propertyMap = new HashMap<String, ObservedProperty>();
        propertyMap.put("AIR", new ObservedProperty("AIR", "AIR"));
        propertyMap.put("WATER", new ObservedProperty("WATER", "WATER"));
    }
    
    public static ObservedPropertyFactory getInstance() {
        return INSTANCE;
    }
    
    public static void addObservedProperty(String name, String param) {                
        ObservedProperty ObservedProperty = new ObservedProperty(name, param);
        
        if(ObservedProperty != null) {
                propertyMap.put(param, ObservedProperty);
        }
    }
    
    public ObservedProperty getObservedProperty(String ObservedPropertyName) {
        return propertyMap.get(ObservedPropertyName);
    }
    
    /**
     * 	Ordered list of Observed Properties
     */
    public List<ObservedProperty> getObservedProperties() {
        List<String> keys = new ArrayList<String>(propertyMap.keySet());
        Collections.sort(keys);
        
        List<ObservedProperty> ObservedPropertys = new ArrayList<ObservedProperty>();
        for(String key : keys) {
                ObservedPropertys.add(propertyMap.get(key));
        }
        
        return ObservedPropertys;            
    }
    
    public Map<String, ObservedProperty> getObservedPropertiesMap() {
        return propertyMap;            
    }
}
