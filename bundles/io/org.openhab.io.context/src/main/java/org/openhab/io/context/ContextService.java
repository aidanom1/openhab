package org.openhab.io.context;
import java.util.ArrayList;
import java.util.Dictionary;

import org.openhab.core.events.EventPublisher;
import org.openhab.core.service.AbstractActiveService;
import org.openhab.io.context.primitives.User;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ContextService extends AbstractActiveService implements ManagedService {
	
	private static final Logger logger = LoggerFactory.getLogger(ContextService.class);
	public static double HOME_LONGITUDE = -8.493406;
	public static double HOME_LATITUDE  = 51.876496;
	public static boolean DEMO_MODE = false;
	
	/** holds the current refresh interval, default to  (3 minutes) */
	public static int refreshInterval = 60000*1;
	private static ArrayList<User> users = null;
	
	private EventPublisher eventPublisher = null;
	
	
	public void setEventPublisher(EventPublisher eventPublisher) {
		this.eventPublisher = eventPublisher;
	}

	public void unsetEventPublisher(EventPublisher eventPublisher) {
		this.eventPublisher = null;
	}
	
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
            StdSchedulerFactory.getDefaultScheduler();
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


	@Override
	public void updated(Dictionary<String, ?> config) throws ConfigurationException
	{
		logger.debug("org.openhab.core.context.location updated");
		if(Boolean.parseBoolean(((String) config.get("demo_mode")))) {
			ContextService.DEMO_MODE = true;
			ContextService.refreshInterval = 10000;
		}
		String[] ht = ((String) config.get("home")).split(",");
		User.radius = Integer.parseInt((String)config.get("radius"));
		try {
			
		    ContextService.HOME_LATITUDE = Double.parseDouble(ht[0]); 
		    ContextService.HOME_LONGITUDE = Double.parseDouble(ht[1]); 
		}
		catch(Exception e) {
			logger.info("Forgot to set location, no worries, we can pretend we're on Tramore Road! "+e);
		}
		if(users != null && config != null)
		{
	        String[] st = ((String) config.get("users")).split(",");
	        String[] emails = ((String) config.get("emails")).split(",");
	        for(int i = 0; i < st.length; i++)
            {
	        	User temp = new User(st[i],emails[i], eventPublisher);
                users.add(temp);
            }
		}
		setProperlyConfigured(true);	
	}

	/*
	@Override
	public void updated(Dictionary<String, ?> config) throws ConfigurationException
	{
		logger.debug("org.openhab.core.context.location updated");

		String[] ht ={ "51.922594","-8.486041"};
		User.radius = 50;
		try {
			
		    ContextService.HOME_LATITUDE = Double.parseDouble(ht[0]); 
		    ContextService.HOME_LONGITUDE = Double.parseDouble(ht[1]); 
		    logger.info(Double.toString(ContextService.HOME_LATITUDE)+","+Double.toString(ContextService.HOME_LONGITUDE));
		}
		catch(Exception e) {
			logger.info("Forgot to set location, no worries, we can pretend we're at home! "+e);
		}
		if(users != null)// && config != null)
		{

			String[] st = {"aidan","aileen"};
			String[] emails = {"aidan.omahony@gmail.com","aileenmorelly@gmail.com"};
	        for(int i = 0; i < st.length; i++)
            {
	        	User temp = new User(st[i],emails[i], eventPublisher);
                users.add(temp);
        		//TODO should check if adding duplicates
            }
		}
		setProperlyConfigured(true);	
	}
	*/
}
