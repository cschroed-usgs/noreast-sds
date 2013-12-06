/*******************************************************************************
 * Project:		noreastsds
 * Source:		WebsiteUtils.java
 * Author:		Philip Russo
 ******************************************************************************/

package gov.usgs.noreast.noreastsds.utils;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class WebsiteUtils {	
	private static Logger log = WebsiteUtils.getLogger(WebsiteUtils.class);
	
	public static Logger getLogger(Class<?> T) {
		URL logFile = T.getResource("/conf/logging/log4j.properties");
		if(logFile == null) {
			logFile = T.getResource("conf/log4j.properties");
		}
		
		Logger log = Logger.getLogger(T.getName());	
		PropertyConfigurator.configure(logFile);
		
		return log;
	}
	
	public static <T> List<T> castList(Class<? extends T> clazz, Collection<?> c) {
	    List<T> r = new ArrayList<T>(c.size());
	    for(Object o: c)
	      r.add(clazz.cast(o));
	    return r;
	}
	
	public static String parseWorkflow(String path) {
		String result = "";
		
		/**
		 * Workflow path pattern:
		 * 		/ang/<WORKFLOW>/#/<WORKFLOW>/value1/value2/...
		 * 
		 * Since the path starts with a /, the first indexed value will be
		 * empty:
		 * 		[, ang, <WORKFLOW>]
		 */
		List<String> parts = Arrays.asList(path.split("/"));
		if(parts.size() >= 2) {
			result = parts.get(2);
		}
		
		return result;
	}
	
	public static boolean validateParameters(List<String> params, boolean validateEmpty) {
		Iterator<String> paramsItr = params.iterator();
		while(paramsItr.hasNext()) {
			String param = paramsItr.next();
			
			if(param == null) {
				return false;
			}
			
			if(validateEmpty && (param.equals(""))) {
				return false;
			}
		}
		
		return true;
	}
}
