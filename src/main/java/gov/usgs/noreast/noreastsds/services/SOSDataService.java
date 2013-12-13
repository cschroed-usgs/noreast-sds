package gov.usgs.noreast.noreastsds.services;

import gov.usgs.noreast.noreastsds.model.ObservedProperty;
import gov.usgs.noreast.noreastsds.model.Offering;
import gov.usgs.noreast.noreastsds.model.SOS.SOSResult;
import gov.usgs.noreast.noreastsds.services.async.SOSDataQuery;
import gov.usgs.noreast.noreastsds.utils.WebsiteUtils;
import gov.usgs.noreast.noreastsds.utils.exception.SDSException;
import gov.usgs.noreast.noreastsds.utils.exception.SDSExceptionID;
import gov.usgs.noreast.springinit.config.FactoryConfig;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class SOSDataService {
	private static Logger log = WebsiteUtils.getLogger(SOSDataService.class);
	
	public enum QueryStatusType {
        /**
         * ChartType:
         * 
         * 		COMPLETED	-	Query run completed
         *		RUNNING		-	Query still running	
         */
        COMPLETED("COMPLETED"), RUNNING("RUNNING"), ERROR("ERROR");
        
        private String code;

        private static Map<String, QueryStatusType> codeToEnum = new HashMap<String, QueryStatusType>();
        
        static {
                for(QueryStatusType t : QueryStatusType.values()) {
                        codeToEnum.put(t.getName(), t);
                }
        }
        
        private QueryStatusType(String dbCode) {
                this.code = dbCode;
        }
        
        public String getName() {
                return code;
        }
        
        public static QueryStatusType convertFromCode(String code) {
                return codeToEnum.get(code);
        }
	}
	
	/*
	 * Beans		===========================================================
	 * ========================================================================
	 */
	@Autowired
	private FactoryConfig factories;
	
	@Autowired
    private SOSDataQuery sosDataQuery;
	
	@Autowired
	private Environment environment;
	/* ====================================================================== */
	
	/*
	 * Local		===========================================================
	 * ========================================================================
	 */
	/* ====================================================================== */
	private static long cacheTimeout = 604800000;		// 1000 * 60 * 60 * 24 * 7 (1 week)
	
	private static Map<String, List<Future<SOSResult>>> queryFutures;
	private static Map<String, List<SOSResult>> cachedResults;
	private static Map<String, Long> resultCacheTiming;
	
	private static final SOSDataService INSTANCE = new SOSDataService();
    
	/**
	 * TODO
	 * 		Must put in a maintenance algorithm to clean the results periodically
	 */
    private SOSDataService() {
    	queryFutures = new ConcurrentHashMap<String, List<Future<SOSResult>>>();
    	cachedResults = new ConcurrentHashMap<String, List<SOSResult>>();
    	resultCacheTiming = new ConcurrentHashMap<String, Long>();
    }
    
    public static SOSDataService getInstance() {
        return INSTANCE;
    }
    
    /**
     * SOS Site Query: 
     * 
     * 		LAST VALUE
     * 		http://cida-wiwsc-javadevp.er.usgs.gov:8080/noreast-sos/simple?request=GetObservation&featureID=MD-DNR-NBP0689&offering=MAX&observedProperty=WATER
     * 
     * 		TIME FRAME
     * 		http://cida-wiwsc-javadevp.er.usgs.gov:8080/noreast-sos/simple?request=GetObservation&featureID=MD-BC-BC-05&offering=RAW&observedProperty=WATER&beginPosition=2010-08-01T00:00:00&endPosition=2010-08-02T00:00:00
     * 
     * 
	 * The Site Data Summary logic follows the following pattern:
	 * 
	 * 		1) Receive site id
	 * 		2) Perform exploratory queries for site against:
	 * 		   http://cida-wiwsc-javadevp.er.usgs.gov:8080/noreast-sos/simple?request=GetObservation&featureID=MD-DNR-NBP0689&offering=MAX&observedProperty=WATER
	 * 				- Each site will be queried for all known "observedProperty"
	 * 				  values.  Currently (11/2013) there are 2:
	 * 						* WATER
	 * 						* AIR
	 * 				- Each observedProperty will be queried for all known
	 * 				  "offering" values.  Currently (11/2013) there are 3:
	 * 						* RAW
	 * 						* MIN
	 * 						* MAX
	 * 				- If there are results for a specific combination, then
	 * 				  that combination will be shown as a possible filter
	 * 				  for the site in charting its data.
	 * 		3) Given results, show filters for chart with a base chart pre-
	 * 		   built for the user.
	 * 
	 */
    public void asyncQueryNoreastSOS(String siteid) {
		/**
		 * Lets see if this id has a query running already
		 */
		if(queryRunning(siteid)) {
			return;
		}
		
		String wpsURL = environment.getProperty("sos.wps.url");
		
    	List<ObservedProperty> observedProperties = factories.observedPropertyFactory().getObservedProperties();
		List<Offering> offerings = factories.offeringFactory().getOfferings();
		
		List<Future<SOSResult>> asyncResults = new ArrayList<Future<SOSResult>>();
		for(ObservedProperty prop : observedProperties) {			
			for(Offering offering : offerings) {
				asyncResults.add(sosDataQuery.asyncQuery(wpsURL, siteid, prop.getParameter(), offering.getParameter()));
			}
		}
    	
		SOSDataService.queryFutures.put(siteid, asyncResults);
    }
    
    /**
     * SOS Site Query: 
     * 
     * 		LAST VALUE
     * 		http://cida-wiwsc-javadevp.er.usgs.gov:8080/noreast-sos/simple?request=GetObservation&featureID=MD-DNR-NBP0689&offering=MAX&observedProperty=WATER
     * 
     * 		TIME FRAME
     * 		http://cida-wiwsc-javadevp.er.usgs.gov:8080/noreast-sos/simple?request=GetObservation&featureID=MD-BC-BC-05&offering=RAW&observedProperty=WATER&beginPosition=2010-08-01T00:00:00&endPosition=2010-08-02T00:00:00
     * 
     * 
	 * The Site Data Summary logic follows the following pattern:
	 * 
	 * 		1) Receive site id
	 * 		2) Perform query for site
	 * 		3) Return results.
	 * 
	 */
    public SOSResult directQueryNoreastSOS(String siteid, String observedProperty, 
			 							   String offering, String beginPosition, 
			 							   String endPosition) {
		String wpsURL = environment.getProperty("sos.wps.url");
		

		SOSResult result = SOSDataQuery.directQuery(wpsURL, siteid, observedProperty, offering, beginPosition, 
													endPosition, true);

		
		return result;
    }
    
    /**
     * Method to see if a previously requested query is finished
     */
    public QueryStatusType sosDataQueryStatus(String siteid) throws SDSException {
    	if(cachedResultsExist(siteid)) {
    		return QueryStatusType.COMPLETED;
    	}
    	
    	List<Future<SOSResult>> futures = SOSDataService.queryFutures.get(siteid);
    	
    	if(futures == null) {
    		String msg = "Unknown JOB ID [" + siteid + "].";
    		throw new SDSException(SDSExceptionID.INVALID_JOB_ID, 
    							   "gov.usgs.noreast.noreastsds.services.SOSDataService",
    							   "sosDataQueryCompleted()",
    							   msg);
    	}
    	
    	for(Future<SOSResult> future : futures) {
    		if(!future.isDone()) {
    			return QueryStatusType.RUNNING;
    		}
    	}
    	
    	return QueryStatusType.COMPLETED;
    }
    
    /**
     * Return true if cached results exist.  False if not.
     * @param siteid
     * @return
     */
    public boolean cachedResultsExist(String siteid) {
    	if(SOSDataService.cachedResults.containsKey(siteid)) {
    		/**
    		 * Lets do a hack cache timing check here.  If the date of this
    		 * cached result is greater than cacheTimeout, we'll kill it
    		 * and have a new one created.
    		 */
    		Long cacheTime = SOSDataService.resultCacheTiming.get(siteid);
    		if(cacheTime != null) {
    			long now = new Date().getTime();
    			if((now - cacheTime.longValue()) >= SOSDataService.cacheTimeout) {
    				SOSDataService.cachedResults.remove(siteid);
    				SOSDataService.resultCacheTiming.remove(siteid);
    				
    				log.info("SOSDataService.cachedResultsExist() INFO - Cached results for site id [" + siteid + 
    						 "] have expired.  Removing cache and rerunning query.");
        			return false;
    			} else {
    				return true;
    			}
    		} else {
    			SOSDataService.cachedResults.remove(siteid);
    			return false;
    		}
    	} else {
    		return false;
    	}
    }
    
    /**
     * Return true if a query is running for this site id
     * @param siteid
     * @return
     */
    public boolean queryRunning(String siteid) {
    	if(SOSDataService.queryFutures.containsKey(siteid)) {
    		return true;
    	}
    	
    	return false;
    }
    
    /**
     * Method to retrieve list of results for given query siteid.  
     * 
     * @throws SDSException 
     */
    public List<SOSResult> getQueryResults(String siteid) throws SDSException {
    	if(cachedResultsExist(siteid)) {
    		return SOSDataService.cachedResults.get(siteid);
    	}
    	
    	List<Future<SOSResult>> futures = SOSDataService.queryFutures.get(siteid);
    	
    	if(futures == null) {
    		String msg = "Unknown JOB ID [" + siteid + "].";
    		throw new SDSException(SDSExceptionID.INVALID_JOB_ID, 
    							   "gov.usgs.noreast.noreastsds.services.SOSDataService",
    							   "getQueryResults()",
    							   msg);
    	}

    	List<SOSResult> results = new ArrayList<SOSResult>();    	
    	for(Future<SOSResult> future : futures) {
    		try {
    			SOSResult result = future.get();
    			
    			if(result.hasResults()) {
    				results.add(result);
    			}
			} catch (InterruptedException e) {
				String msg = "JOB ID [" + siteid + "] has been interrupted.";
	    		throw new SDSException(SDSExceptionID.JOB_ID_INTERRUPTED, 
	    							   "gov.usgs.noreast.noreastsds.services.SOSDataService",
	    							   "getQueryResults()",
	    							   msg, e);
			} catch (ExecutionException e) {
				String msg = "Unknown JOB ID [" + siteid + "].";
	    		throw new SDSException(SDSExceptionID.JOB_ID_ERROR, 
	    							   "gov.usgs.noreast.noreastsds.services.SOSDataService",
	    							   "getQueryResults()",
	    							   msg, e);
			}
    	}
    	
    	/**
    	 * Now remove the futures from the list as we dont need it anymore and
    	 * store the results in the cache
    	 */
    	SOSDataService.queryFutures.remove(siteid);
    	SOSDataService.cachedResults.put(siteid, results);
    	
    	/**
    	 * Grab the cachetimeout in the properties (everytime in case they are changed)
    	 */
    	try {
    		SOSDataService.cacheTimeout = Long.parseLong(environment.getProperty("service.sos.cache.timeout"));
    	} catch (Exception e) {
    		SOSDataService.cacheTimeout = 604800000;
    		log.error("Environment property [service.sos.cache.timeout] not found.  Using 1 week as default cache.");
    	}
    	SOSDataService.resultCacheTiming.put(siteid, new Long(new Date().getTime()));
    	
    	return results;
    }
}




