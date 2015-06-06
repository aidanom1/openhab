package org.openhab.io.coachman.activity.oauth;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.openhab.io.coachman.activity.oauth.OAuth2Util.AccessToken;
import org.openhab.io.coachman.activity.oauth.OAuth2Util.UserToken;
import org.openhab.io.coachman.primitives.User;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.google.gdata.client.GoogleService;

/**
 * Configuration class which implements {@link ManagedService} to act as a central
 * handler for configuration issues. It holds the current configuration values
 * and gives access through static member fields.
 *
 * @author Tom Deckers
 * @since 1.2.0
 */
public class CalendarConfigurationImpl implements  JobListener {
       
        private static final Logger logger =
                        LoggerFactory.getLogger(CalendarConfigurationImpl.class);
       
        private final static String PREFIX_PROPNAME = "prefix";
        private final static String PATTERN_PROPNAME = "pattern";
        private final static String TEMPLATE_PROPNAME = "template";
        private final static String INTERVAL_PROPNAME = "interval";
        private final static String DEFAULT_PREFIX = "openhab";
        private final static String DEFAULT_PATTERN = "wwYYYY";
        private final static int DEFAULT_INTERVAL = 5;
       
        // Properties file to hold all Oauth2 related attributes (tokens, codes, ...)
        private Properties properties = new Properties();
        // OAuth 2.0 vars are stored here.  
        // Avoids having to re-register the install after each reboot.
        private static final String CRED_FOLDER_NAME = "etc" + File.separator + "Calendar";
        private static final String CRED_FILE_NAME = "Calendar.priv";
        private File authStateFile = new File(CRED_FOLDER_NAME + File.separator + CRED_FILE_NAME);
       
        private CalendarList feed;
        
        /** Google Calendar filename prefix. */
        private String prefix;
       
        /** Google Calendar filename suffix (pattern).  Must be a valid value for SimpleDateFormat. */
        private String pattern;
       
        /** Use a copy of this document instead of creating a blank one. */
        private String template;
       
        /** Upload interval.  Changes are cached, and pushed to Google Calendar every 'internal' minutes */
        private int interval = DEFAULT_INTERVAL;        
       
        private boolean configured = false;
        private boolean initialized = false;

        public String getTemplate() {
                return template;
        }

        public int getInterval() {
                return interval;
        }

        public boolean isConfigured() {
                return configured;
        }
       
        public boolean isInitizalized() {
                return initialized;
        }
       
        public String getSheetname() {
            SimpleDateFormat formatter = new SimpleDateFormat(this.pattern);
            String sheetName = this.prefix + "_" + formatter.format(new Date());
            return sheetName;
        }

        public void updated(Dictionary<String, ?> config)
                        throws ConfigurationException {
                if (config != null) {
                        // prefix
                        String prefixString = (String) config.get(PREFIX_PROPNAME);
                        if (StringUtils.isNotBlank(prefixString)) {
                                prefix = prefixString;
                        } else {
                                logger.debug("Using default file prefix: {}", DEFAULT_PREFIX);
                                prefix = DEFAULT_PREFIX;
                        }
                        // prefix
                        String patternString = (String) config.get(PATTERN_PROPNAME);
                        if (StringUtils.isNotBlank(patternString)) {
                                pattern = patternString;
                        } else {
                                logger.debug("Using default pattern: {}", DEFAULT_PATTERN);
                                pattern = DEFAULT_PATTERN;
                        }
                       
                        // interval
                        String intervalString = (String) config.get(INTERVAL_PROPNAME);
                        if (StringUtils.isNotBlank(intervalString)) {
                                try {
                                        interval = Integer.parseInt(intervalString);
                                } catch (NumberFormatException nfe) {
                                        logger.warn("{} must be an integer.  Using default value {} mins.", INTERVAL_PROPNAME, DEFAULT_INTERVAL);
                                }
                        }
                        if (interval < 5) { // fix in case value is too small.
                                logger.warn("{} must be at least 5 mins.  Using default value {} mins", INTERVAL_PROPNAME, DEFAULT_INTERVAL);
                                interval = DEFAULT_INTERVAL;
                        }
                       
                } else {
                        logger.debug("Config is null, ignoring update.");
                }
               
        }
       

        /** Global instance of the HTTP transport. */
        private static HttpTransport httpTransport;

        /** Global instance of the JSON factory. */
        private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

        private static com.google.api.services.calendar.Calendar client;
        
        public CalendarList getCalendarList() {
        	return feed;
        }
        
        public Events getUserEvents(User u)
        {
        	String calendarName = "openHAB_"+u.getName();
        	String calendarId = null;
        	String pageToken = null;
        	Events events = null;
        	List<CalendarListEntry> listItems = feed.getItems();
        	for(CalendarListEntry entry : listItems) {
        		if(entry.getSummary().equals(calendarName)) {
        			calendarId = entry.getId();
        		}
        	}
        	if(calendarId == null) {
        		return null;
        	}
           try {
			events = client.events().list(calendarId).setPageToken(pageToken).execute();
			List<Event> items = events.getItems();
			for(Event event: items) {
				logger.debug(event.getSummary());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.debug(e.toString());
		}
           return events;
           
        		
        	
        }

        /**
         * initialize the configuration by verifying and if needed requesting tokens.  This
         * method is used each time configuration changes.  It's a way to refresh and validate all
         * settings.
         *
         * @throws IOException
         * @throws FileNotFoundException
         */
        public void initialize() throws FileNotFoundException, IOException {
        	try {
				httpTransport = GoogleNetHttpTransport.newTrustedTransport();
			} catch (GeneralSecurityException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

                // if already initialized, skip.
                if (initialized) {
                        return;
                }
               
                File folder = new File(CRED_FOLDER_NAME);
                if(!folder.exists()) {
                        folder.mkdir();
                }
               
                // Load properties if we have them, if not: create file.
                if(authStateFile.exists()) {
                        properties.load(new FileInputStream(authStateFile));
                        logger.debug("Properties loaded from " + authStateFile.getAbsolutePath());
                        if (logger.isDebugEnabled()) {
                                Enumeration<?> e =  properties.propertyNames();
                                while(e.hasMoreElements()) {
                                        String name = (String) e.nextElement();
                                        logger.debug("{} = {}", name, properties.get(name));
                                }
                        }
                } else {
                        authStateFile.createNewFile();
                }
               

               
               
                // If we have access tokens, let's see if they work.
                if(properties.containsKey("access_token") && properties.containsKey("refresh_token")) {
                        logger.debug("Found access token, will try them");
                        GoogleCredential credential = OAuth2Util.createCredential(properties.getProperty("access_token"),
                                        Long.parseLong(properties.getProperty("expires_in")),
                                        properties.getProperty("refresh_token"));
                        
                        client = new com.google.api.services.calendar.Calendar.Builder(
                                httpTransport, JSON_FACTORY, credential).setApplicationName("openHAB").build();

                        try {                  
                               
                                // If we can get this feed, we're got to go.                            
                        	feed = client.calendarList().list().execute();


                                logger.debug("Authentication succeeded.  We're good to go.");
                                initialized = true;
                                                               
                                return;
                               
                        } catch (Exception e) {
                                // Check which exception we're dealing with:
                                // https://developers.google.com/gdata/javadoc/com/google/gdata/client/GoogleService
                                if (e instanceof GoogleService.InvalidCredentialsException) {
                                        // continue to get user code and authToken.
                                        logger.debug("OAuth credentials invalid, getting new ones");
                                } else if (e instanceof GoogleService.SessionExpiredException) {
                                        logger.debug("OAuth session expired, getting new one");
                                } else {
                                        throw new RuntimeException("Exception accessing Google Calendar", e);
                                }
                        }
                }
               
                // If we end up here, get user token, and then wait for the access token.
                // It's possible user_code already exists if code was obtained, but user didn't
                // authorize us yet.
               
                if(!properties.containsKey("user_code")) {
                        logger.debug("Didn't find user_code.. getting one now.");
                        UserToken token = OAuth2Util.getUserToken(properties);
                        if (token != null) {
                                properties.put("expires_in", new Integer(token.expires).toString());
                                properties.put("interval", new Integer(token.interval).toString());
                                properties.put("device_code", token.deviceCode);
                                properties.put("verification_url", token.verificationUrl);
                                properties.put("user_code", token.userCode);
                               
                                save();
                        }
                }
               
                // after getting setting the user code, need to poll until authorized.
                // we register ourselves to receive updates on job completion.
                OAuth2Util.getAccessToken(properties, this);            
        }
       
       
        private void save() {
                try {
                        properties.store(new FileOutputStream(authStateFile), "Google Calendar persistence");
                        logger.debug("OAuth state saved.");
                } catch (FileNotFoundException fnfe) {
                        logger.warn("Failed to save properties", fnfe);
                } catch (IOException ioe) {
                        logger.warn("Failed to save properties", ioe);
                } catch (Exception e) {
                	    logger.warn("Failed to store");
                }
                
        }
        

        // Job listener methods.
        @Override
		public String getName() {
                return "AccessTokenJobListener";
        }

        @Override
		public void jobExecutionVetoed(JobExecutionContext arg0) {
                // we're not interested
        }

        @Override
		public void jobToBeExecuted(JobExecutionContext arg0) {
                // we're not interested        
        }

        @Override
		public void jobWasExecuted(JobExecutionContext context,
                        JobExecutionException e) {
                AccessToken token = (AccessToken) context.getResult();
               
                if (token != null) {                    
                        properties.put("expires_in", new Integer(token.expires).toString());
                        properties.put("access_token",token.accessToken);
                        properties.put("token_type",token.tokenType);
                        properties.put("refresh_token",token.refreshToken);
                       
                        logger.debug("Storing OAuth2 access and refresh token");
                        save();
                       
                        this.initialized = true;
                }
        }

		public boolean isConfiguredCorrectly() {
			// TODO Auto-generated method stub
			return initialized;
		}
}
