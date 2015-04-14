package org.openhab.io.context;
import org.openhab.io.context.location.*;
import org.openhab.io.context.primitives.*;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Properties;
import java.util.StringTokenizer;

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
import org.openhab.io.context.activity.*;
import static com.google.maps.GeocodingApi.ComponentFilter.administrativeArea;
import static com.google.maps.GeocodingApi.ComponentFilter.country;



public class ContextService extends AbstractActiveService implements ManagedService {
	
	private static final Logger logger = LoggerFactory.getLogger(ContextService.class);
	public static final double HOME_LONGITUDE = -8.4929821;
	public static final double HOME_LATITUDE  = 51.8763856;
	
	/** holds the current refresh interval, default to  (3 minutes) */
	public static int refreshInterval = 60000*1;
	private static ArrayList<User> users = null;
	

	/** holds the local quartz scheduler instance */
	private Scheduler scheduler;
	
	public ContextService()
	{
		if(users == null) {
		    users = new ArrayList<User>();
		}
	}


	@Override
	protected long getRefreshInterval() {
		return refreshInterval;
	}

	@Override
	protected String getName() {
		return "Context Aware Location Service";
	}
	
	@Override
	public void activate() {
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
		logger.debug("org.openhab.core.context.location execute");
		for(int i = 0; i < users.size(); i++) {
			users.get(i).updateContext();
		}
	}


	public void updated(Dictionary<String, ?> config) throws ConfigurationException
	{
		logger.debug("org.openhab.core.context.location updated");
		if(users != null)// && config != null)
		{
	        //String[] st = ((String) config.get("users")).split(",");
	        //String[] emails = ((String) config.get("emails")).split(",");
			String[] st = {"aidan","aileen"};
			String[] emails = {"aidan.omahony@gmail.com","aileenmorelly@gmail.com"};
	        for(int i = 0; i < st.length; i++)
            {
                users.add(new User(st[i],emails[i]));
        		//TODO should check if adding duplicates
            }
		}
		setProperlyConfigured(true);	
		//logger.debug("org.openhab.core.context.location before getUserToken");
		//OAuth2Util.UserToken u = OAuth2Util.getUserToken(new Properties());
		//logger.debug("org.openhab.core.context.location after getUserToken");
	}

}
