package org.openhab.io.context.location;
import org.openhab.io.context.location.util.*;
import org.openhab.io.context.primitives.*;

import java.util.ArrayList;
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
import com.google.maps.GeocodingApi.ComponentFilter;
import com.google.maps.model.AddressComponentType;
import com.google.maps.model.AddressType;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import com.google.maps.model.LocationType;
import static com.google.maps.GeocodingApi.ComponentFilter.administrativeArea;
import static com.google.maps.GeocodingApi.ComponentFilter.country;



public class LocationService extends AbstractActiveService implements ManagedService {
	
	private static final Logger logger = LoggerFactory.getLogger(LocationService.class);
	public static final double HOME_LONGITUDE = -8.4929821;
	public static final double HOME_LATITUDE  = 51.8763856;
	private double longitute;
	private double latitude;
	private String locationAsString;
	private double distanceFromHome;
	private String username;
	private GeoApiContext context = null;// new GeoApiContext().setApiKey("AIzaSyBGAZA2p6mbK9k2LGNJji_U1BK1dancDnc");
	
	/** holds the current refresh interval, default to  (3 minutes) */
	public static int refreshInterval = 60000*3;
	

	/** holds the local quartz scheduler instance */
	private Scheduler scheduler;
	
	public LocationService()
	{
		context =  new GeoApiContext().setApiKey("AIzaSyBGAZA2p6mbK9k2LGNJji_U1BK1dancDnc");
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
		DistanceMatrix req = null;
		LocationList ll = new LocationList();
        ArrayList<User> users = ll.getUsers();
		logger.debug("org.openhab.core.context.location execute");
		for(int i = 0; i < users.size(); i++) {
			Location l = ll.getUserLocation(users.get(i));
		try {
             req = DistanceMatrixApi.newRequest(context)
	        .origins(new LatLng(l.getLatitude(), l.getLongitude()))
	        .destinations(new LatLng(HOME_LATITUDE, HOME_LONGITUDE))
	        .await();
		}
		catch(Exception e)
		{
			logger.debug("org.openhab.core.context.location exception"+e.toString());
		}
		

	        logger.debug("org.openhab.core.context.location user "+users.get(i).getName()+"   "
	                     +req.rows[0].elements[0].distance
	                     +" "+req.originAddresses[0]);			

		}
        
		
	}


	public void updated(Dictionary<String, ?> properties)
			throws ConfigurationException {
		logger.debug("org.openhab.core.context.location updated");
		setProperlyConfigured(true);
		
	}

}
