package gov.usgs.noreast.noreastsds.services;

import gov.usgs.noreast.noreastsds.utils.WebsiteUtils;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

public class ChartingService {
	private static Logger log = WebsiteUtils.getLogger(ChartingService.class);
	
	public enum ChartingType {
        /**
         * ChartType:
         * 
         * 		HIGHCHARTS	-	Highcharts library
         * 		HIGHSTOCK	-	Highcharts library with Highstock extension
         *		D3			-	D3 library
         */
        HIGHCHARTS("HIGHCHARTS"), HIGHSTOCK("HIGHSTOCK"), D3("D3"), UNKNOWN("UNKNOWN");
        
        private String code;
        
        private static Map<String, ChartingType> codeToEnum = new HashMap<String, ChartingType>();
        
        static {
                for(ChartingType t : ChartingType.values()) {
                        codeToEnum.put(t.getName(), t);
                }
        }
        
        private ChartingType(String dbCode) {
                this.code = dbCode;
        }
        
        public String getName() {
                return code;
        }
        
        public static ChartingType convertFromCode(String code) {
                return codeToEnum.get(code);
        }
	}
	
	
}
