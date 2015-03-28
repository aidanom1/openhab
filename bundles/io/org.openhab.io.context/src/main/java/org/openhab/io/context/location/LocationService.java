package org.openhab.io.context.location;

import java.util.Dictionary;

import org.openhab.core.service.AbstractActiveService;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.maps.*;
import com.google.maps.model.*;

public class LocationService extends AbstractActiveService implements ManagedService {
	
	private static final Logger logger = LoggerFactory.getLogger(LocationService.class);
	public static final double HOME_LONGITUDE = -8.493444;
	public static final double HOME_LATITUDE  = 51.876501;
	private double longitute;
	private double latitude;
	private String locationAsString;
	private double distanceFromHome;
	private String username;
	private GeoApiContext context = null;//new GeoApiContext().setApiKey("AIzaSyBGAZA2p6mbK9k2LGNJji_U1BK1dancDnc");
	
	/** holds the current refresh interval, default to 900000ms (15 minutes) */
	public static int refreshInterval = 900000;
	

	/** holds the local quartz scheduler instance */
	private Scheduler scheduler;
	
	public LocationService()
	{
		logger.debug("org.openhab.core.context.location execute");
	}


	@Override
	protected long getRefreshInterval() {
		logger.debug("org.openhab.core.context.location getRefreshInterval");
		return refreshInterval;
	}

	@Override
	protected String getName() {
		logger.debug("org.openhab.core.context.location getName");
		return "Context Aware Location Service";
	}
	
	@Override
	public void activate() {
		logger.debug("org.openhab.core.context.location activate");
        try {
            scheduler = StdSchedulerFactory.getDefaultScheduler();
            super.activate();
        }
        catch (SchedulerException se) {
            logger.error("initializing scheduler throws exception", se);
        }
	}


	@Override
	protected void execute() {
		username = "aidan";
		logger.debug("org.openhab.core.context.location execute");
		DistanceMatrix req = DistanceMatrixApi.newRequest(context)
	        .origins(new LatLng(51.8843400, -8.5340610))
	        .destinations(new LatLng(HOME_LATITUDE, HOME_LONGITUDE))
	        .awaitIgnoreError();
        try {
        	req.wait();
        }
        catch(Exception e) {
        	logger.debug("org.openhab.core.context.location constructer timeout");
        }
        logger.debug("org.openhab.core.context.location constructer"+req.toString());
		
	}


	public void updated(Dictionary<String, ?> properties)
			throws ConfigurationException {
		logger.debug("org.openhab.core.context.location updated");
		setProperlyConfigured(true);
		
	}

}
