/*
 * Copyright (c) 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.openhab.io.context.activity;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;

import org.openhab.io.context.ContextService;
import org.openhab.io.context.activity.OAuth2Util.UserToken;
import org.openhab.io.context.primitives.Activity;
import org.openhab.io.context.primitives.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Lists;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;

/**
 * @author Yaniv Inbar
 */
public class ActivityGenerator {
	private static final Logger logger = LoggerFactory.getLogger(ContextService.class);
  /**
   * Be sure to specify the name of your application. If the application name is {@code null} or
   * blank, the application will log a warning. Suggested format is "MyCompany-ProductName/1.0".
   */
  private static final String APPLICATION_NAME = "openHAB Context Aware Services";

  /** Directory to store user credentials. */
  private static final java.io.File DATA_STORE_DIR =
      new java.io.File(System.getProperty("user.home"), ".store/calendar_sample");

  /**
   * Global instance of the {@link DataStoreFactory}. The best practice is to make it a single
   * globally shared instance across your application.
   */
  private static FileDataStoreFactory dataStoreFactory;
 
  /** Global instance of the HTTP transport. */
  private static HttpTransport httpTransport;

  /** Global instance of the JSON factory. */
  private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

  private static com.google.api.services.calendar.Calendar client;

  static final java.util.List<Calendar> addedCalendarsUsingBatch = Lists.newArrayList();
  
  private User user;
  private UserToken u;
  private net.fortuna.ical4j.model.Calendar calendar = null;
  
  private boolean isinit = false;
  private CalendarConfigurationImpl t;
  /** Authorizes the installed application to access user's protected data. */
  public ActivityGenerator()
  {
	  
	  try {
		  t = new CalendarConfigurationImpl();
		  t.initialize();
		  if(t.isConfiguredCorrectly()) {
			  isinit = true;
		  }
	} catch (Exception e) {
		// TODO Auto-generated catch block
		logger.debug("org.openhab.core.context.activity "+e.toString());
	}
	  
			 
      
  }
  
  private BufferedReader getURL(String URL)
  {
	  URL oracle = null;
	  String returnVal = "";
	try {
		oracle = new URL(URL);
	} catch (MalformedURLException e) {
		// TODO Auto-generated catch block
		logger.debug("org.openhab.core.context.activity "+e.toString());
	}
      BufferedReader in = null;
	try {
		in = new BufferedReader(
		  new InputStreamReader(oracle.openStream()));
	} catch (IOException e) {
		// TODO Auto-generated catch block
		logger.debug("org.openhab.core.context.activity "+e.toString());
	}
    return in;
    /*
      String inputLine = null;
      try {
    	  char temp = (char) -3;
		while ((inputLine = in.readLine()) != null) {
			returnVal+='\r'+inputLine;
		}
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
      try {
		in.close();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
      return returnVal;
      */
  }

  public Activity getUserActivity(User u) {
	  // TODO Auto-generated method stub
	  com.google.api.services.calendar.model.Events e = t.getUserEvents(u);
	  Activity a = new Activity();
	  if(e == null) return a;
	  List<Event> items = e.getItems();
		for(Event event: items) {
			EventDateTime start = event.getStart();
			EventDateTime end = event.getEnd();
			if(start.getDateTime().getValue() <= System.currentTimeMillis() && end.getDateTime().getValue() > System.currentTimeMillis()) {
				if(event.getDescription() != null)
				    a.setDescription(event.getDescription());
				if(event.getSummary() != null)
				    a.setSummary(event.getSummary());
				logEvent(event, u);
			}
			logger.debug(event.getSummary());
		}
	  return a;
  }
	private String driverClass = "com.mysql.jdbc.Driver";
	private String url = "jdbc:mysql://127.0.0.1:3306/openhab";

	private Connection connection = null;
	
private void logEvent(Event event, User u2) {
	
	if(event.getSummary() == null) return;

	if(!event.getSummary().equals("WORK") &&
			!event.getSummary().equals("SOCIAL") &&
			!event.getSummary().equals("SCHOOL") &&
			!event.getSummary().equals("SHOPPING")) return;
	String location = event.getLocation();
	if(location == null) return;
	String summary = event.getSummary();
	GeoApiContext context = new GeoApiContext().setApiKey("AIzaSyBGAZA2p6mbK9k2LGNJji_U1BK1dancDnc");
	GeocodingResult[] result = null;
	try {
		result = GeocodingApi.geocode(context,location).await();
	} catch (Exception e1) {
		// TODO Auto-generated catch block
		logger.debug(e1.getMessage());
	}
	double lat = result[0].geometry.location.lat;
	double lng = result[0].geometry.location.lng;

	try {
		Class.forName(driverClass).newInstance();
	    connection = DriverManager.getConnection(url, "openhab", "openhab");
	    Statement st = connection.createStatement();
        st = connection.createStatement();
       
        String query = "insert into MASTERLOCATIONLIST (user,category,address,lng,lat) values " +
        		"('"+u2.getName()+"','"+
        		summary+"','"+
        		location+"',"+String.valueOf(lat)+","+String.valueOf(lng)+")";
		logger.debug(query);
		int t = st.executeUpdate(query);
		st.close();
	}
	catch(Exception e) {
		logger.debug("Error writing context to database "+e.toString());
	}
}

public boolean initialised() {
	return isinit;
}
  
  


}
