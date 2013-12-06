/*******************************************************************************
 * Project:		noreastsds
 * Source:		MainController.java
 * Author:		Philip Russo
 ******************************************************************************/

package gov.usgs.noreast.noreastsds.controllers;

import gov.usgs.noreast.noreastsds.model.SOS.SOSResult;
import gov.usgs.noreast.noreastsds.model.SOS.components.SOSPhenomenonTime;
import gov.usgs.noreast.noreastsds.model.SOS.components.SOSSiteLocation;
import gov.usgs.noreast.noreastsds.services.SOSDataService;
import gov.usgs.noreast.noreastsds.services.SOSDataService.QueryStatusType;
import gov.usgs.noreast.noreastsds.utils.WML2Utils;
import gov.usgs.noreast.noreastsds.utils.WebsiteUtils;
import gov.usgs.noreast.noreastsds.utils.exception.SDSException;
import gov.usgs.noreast.noreastsds.utils.exception.SDSExceptionID;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class MainController {
	private static Logger log = WebsiteUtils.getLogger(MainController.class);
	
	/*
	 * Beans		===========================================================
	 * ========================================================================
	 */
	@Autowired
	private SOSDataService sosDataService;
	
	@Autowired
	private Environment environment;
	/* ====================================================================== */
	
	/*
	 * Local		===========================================================
	 * ========================================================================
	 */
	/* ====================================================================== */
	
	/*
	 * Actions		===========================================================
	 * ========================================================================
	 */
	@RequestMapping(value="/", method=RequestMethod.GET)
    public ModelAndView entry() {
		ModelAndView mv = new ModelAndView("/main", "title", "");
		mv.addObject("version", environment.getProperty("sds.version"));
		
		return mv;
    }
	
	@RequestMapping(value="/siteid/{siteid}")
    public ModelAndView siteQuery(@PathVariable String siteid) {
		/**
		 * Site ID null, redirect to root
		 */
		if(siteid == null) {
			return new ModelAndView("redirect:/");
		}
		
		/**
		 * Lets see if this id has results available.  If it does, redirect to
		 * the results.
		 */
		if(sosDataService.cachedResultsExist(siteid)) {
			return new ModelAndView("redirect:/results/" + siteid);
		}
		
		/**
		 * Kick off the sos query and return.
		 */
		sosDataService.asyncQueryNoreastSOS(siteid);
		
		ModelAndView mv = new ModelAndView("/siteidqueryrunning", "title", " - siteid: " + siteid + " - Query Running");
		mv.addObject("version", environment.getProperty("sds.version"));
		mv.addObject("siteid", siteid);
		
		return mv;
    }
	
	/**
	 * Ajax call to check query status
	 * @param siteid
	 * @return
	 */
	@RequestMapping(value="/query/{siteid}")
	@ResponseBody
    public String siteQueryStatus(@PathVariable String siteid) {
		/**
		 * Site ID null, redirect to root
		 */
		if(siteid == null) {
			return "/";
		}
		
		QueryStatusType status;
		try {
			status = sosDataService.sosDataQueryStatus(siteid);
		} catch (SDSException e) {
			String msg = "MainController.siteQueryStatus() ERROR - {" + e.getExceptionid() + "}";
			log.error(msg);
			
			if(e.getExceptionid() == SDSExceptionID.INVALID_JOB_ID) {
				msg = "There are no results for site id [" + siteid + "].  Please submit " +
					  "a new query.";
			}
			return "/error/" + siteid + "/message/" + msg;
		}
		
		if(status == QueryStatusType.RUNNING) {
			log.info("MainController.siteQueryStatus() Query still running for siteid - {" + siteid + "}");
			return "running";
		}
		
		/**
		 * In case we extend the enum in the service in the future
		 */
		if(status != QueryStatusType.COMPLETED) {
			String msg = "MainController.siteQueryStatus() : Unknown Status";
			log.error("MainController.siteQueryStatus() ERROR - " + msg + " [" + status.getName() + "]");
			return "/error/" + siteid + "/message/" + msg;
		}
		
		/**
		 * Query has completed
		 */
		log.info("MainController.siteQueryStatus() Query finished for siteid {" + siteid + "}");
		return "/results/" + siteid;
	}
	
	/**
	 * Error Action
	 * @param siteid
	 * @param message
	 * @return
	 */
	@RequestMapping(value="/error/{siteid}/message/{message}")
    public ModelAndView siteQueryError(@PathVariable String siteid, @PathVariable String message) {
		log.error("MainController.siteQueryError() ERROR - " + message + " {" + siteid + "}");
		ModelAndView mv = new ModelAndView("/siteidqueryerror", "title", " - Query Error");
		mv.addObject("version", environment.getProperty("sds.version"));
		mv.addObject("siteid", siteid);
		mv.addObject("error", message);
		return mv;
    }
	
	/**
	 * View results Action
	 * @param siteid
	 * @return
	 */
	@RequestMapping(value="/results/{siteid}")
    public ModelAndView siteQueryResults(@PathVariable String siteid) {
		/**
		 * Site ID null, redirect to root
		 */
		if(siteid == null) {
			return new ModelAndView("redirect:/");
		}
		
		List<SOSResult> results;
		try {
			results = sosDataService.getQueryResults(siteid);
		} catch (SDSException e) {
			String msg = "MainController.siteQueryResults() ERROR - {" + e.getExceptionid() + "}";
			log.error(msg);
			
			if(e.getExceptionid() == SDSExceptionID.INVALID_JOB_ID) {
				msg = "There are no results for site id [" + siteid + "].  Please submit " +
					  "a new query.";
			}
			
			return new ModelAndView("redirect:/error/" + siteid + "/message/" + msg);
		}
		
		if(results.size() < 1) {
			String msg = "No results found for site id [" + siteid + "]";
			return new ModelAndView("redirect:/error/" + siteid + "/message/" + msg);
		}
				
		log.info("MainController.siteQueryResults() Results called for siteid - {" + siteid + "}");
		ModelAndView mv = new ModelAndView("/siteidresult", "title", " - Query Results");
		mv.addObject("version", environment.getProperty("sds.version"));
		mv.addObject("siteid", siteid);		
		mv.addObject("results", results);
		
		/**
		 * Although the results have the location information in each instance,
		 * for what we're using it for we'll extract it and put it in its own
		 * location info object so that the view doesn't have to think about
		 * which result to choose its location info from... just use this object.
		 */
		SOSSiteLocation location = results.get(0).getLocation();
		mv.addObject("location", location);
		
		/**
		 * We also want to set the initial date period for the search.  To do this
		 * we'll find the best overlap begin/end time between all the results
		 */
		SOSPhenomenonTime phenomTime = WML2Utils.getBestEncompassingDate(results);
		mv.addObject("phenomtime", phenomTime);
		
		log.error("\n\nRESULTS\n\n" + results.toString());		
		
		return mv;
    }
	/* ====================================================================== */
	
	/**
	 * Ajax call to check query status
	 * @param siteid
	 * @return
	 */
	@RequestMapping(value="/dataproxy/{siteid}/{observedProperty}/{offering}/{beginPosition}/{endPosition}/{resultType}")
	@ResponseBody
    public String siteQueryProxy(@PathVariable String siteid, @PathVariable String observedProperty, 
    							 @PathVariable String offering, @PathVariable String beginPosition, 
    							 @PathVariable String endPosition, @PathVariable String resultType,
    							 @RequestParam(value="download", required = false) String download,
    							 @RequestParam(value="numeric", required = false) String numeric,
    							 HttpServletResponse response) {
		Boolean asDownload = false;
		if((download != null) && (download.toLowerCase().equals("true"))) {
			asDownload = true;
		}
		
		/**
		 * We default the time values to numbers
		 */
		Boolean asNumeric = true;
		if((numeric != null) && (numeric.toLowerCase().equals("false"))) {
			asNumeric = false;
		}
		
		log.error("MainController.siteQueryProxy() - " +
				  "\n\t{siteId} = " + siteid + 
				  "\n\t{observedProperty} = " + observedProperty + 
				  "\n\t{offering} = " + offering + 
				  "\n\t{beginPosition} = " + beginPosition + 
				  "\n\t{endPosition} = " + endPosition + 
				  "\n\t{resultType} = " + resultType + 
				  "\n\t{download} = " + asDownload  + 
				  "\n\t{numeric} = " + asNumeric );
		
		if(!WebsiteUtils.validateParameters(Arrays.asList(siteid, observedProperty, offering, beginPosition, endPosition, resultType), true)) {
			String msg = "All paremeters must have a non-empty value.  Please resubmit " +
					  "the query.";
			
			if(resultType != null) {
				if(resultType.equals("xml".toLowerCase())) {
					msg = "<error>" + msg + "</error>";
				} else if(resultType.equals("json".toLowerCase())) {
					msg = "{\"error\":\"" + msg + "\"}";
				} else if(resultType.equals("csv".toLowerCase())) {
					msg = "error," + msg;
				}
			}
			return "/error/"  + siteid + "/message/" + msg;
		}
		
		SOSResult result = sosDataService.directQueryNoreastSOS(siteid, observedProperty, offering, beginPosition, endPosition);

		if(asDownload) {
			String extension = ".csv";
			if(resultType != null) {
				if(resultType.equals("xml".toLowerCase())) {
					extension = ".xml";
				} else if(resultType.equals("json".toLowerCase())) {
					extension = ".json";
				} else if(resultType.equals("csv".toLowerCase())) {
					extension = ".csv";
				}
			}
			
			String fileName = siteid + "_" + observedProperty + "-" + offering + "_" + beginPosition + "-" + endPosition + extension;
		    response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
		}
		
		return result.exportData(resultType, true, asNumeric);
	}
}












