package org.openhab.io.context.location;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.openhab.io.context.ContextService;
import org.openhab.io.context.primitives.Location;
import org.openhab.io.context.primitives.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.maps.DistanceMatrixApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.LatLng;

public class LocationList {
    /**
	 * 
	 */

	public static final int CURRENT_LOCATION = 0;
    
    private int command;
	private String driverClass = "com.mysql.jdbc.Driver";
	private String url = "jdbc:mysql://127.0.0.1:3306/openhab";
	private String user = "openhab";
	private String password = "openhab";
	private static final Logger logger = LoggerFactory.getLogger(ContextService.class);
	private Map<String, String> sqlTables = new HashMap<String, String>();
	private GeoApiContext context = null;// new GeoApiContext().setApiKey("AIzaSyBGAZA2p6mbK9k2LGNJji_U1BK1dancDnc");

	private Connection connection = null;
	
    public LocationList()
    {
		context =  new GeoApiContext().setApiKey("AIzaSyBGAZA2p6mbK9k2LGNJji_U1BK1dancDnc");
    }

    
    public Location getUserLocation(User u)
    {
    	logger.debug("getUserLocation("+u.getName()+")");
    	Location l = new Location();
		DistanceMatrix req = null;
		try {
			Class.forName(driverClass).newInstance();
			connection = DriverManager.getConnection(url, "openhab", "openhab");
			Statement st = connection.createStatement();
		    st = connection.createStatement();
			String query = "select Time,Value from Item12 where (Value REGEXP '.*"+u.getName()+"$')";
			logger.debug(query);
			ResultSet t = st.executeQuery(query);
			t.last();
			String temp = t.getString(2);
			logger.debug(temp);
			String[] tempArray = temp.split(",");
			l.setLatitude(Double.parseDouble(tempArray[0]));
			l.setLongitude(Double.parseDouble(tempArray[1]));
			try {
	             req = DistanceMatrixApi.newRequest(context)
		        .origins(new LatLng(l.getLatitude(), l.getLongitude()))
		        .destinations(new LatLng(ContextService.HOME_LATITUDE, ContextService.HOME_LONGITUDE))
		        .await();
			}
			catch(Exception e)
			{
				logger.debug("org.openhab.core.context.location exception"+e.toString());
			}


	        l.setLocationAsString(req.originAddresses[0]);
	        l.setDistanceToHome(distance(l.getLatitude(),l.getLongitude(),ContextService.HOME_LATITUDE,ContextService.HOME_LONGITUDE,'K'));;
			t.close();
			st.close();
		} catch (Exception e) {
			logger.error("LocationList: Failed connecting to the SQL database using: driverClass=" + driverClass + ", url="
					+ url + ", user=" + user + ", password=" + password, e);
		}
		disconnectFromDatabase();
		return l;
    }

	
	private void disconnectFromDatabase() {
		if (connection != null) {
			try {
				connection.close();
				logger.debug("mySQL: Disconnected from database {}", url);
			} catch (Exception e) {
				logger.error("mySQL: Failed disconnecting from the SQL database {}", e);
			}
			connection = null;
		}
	}
	
	/* http://stackoverflow.com/questions/3694380/calculating-distance-between-two-points-using-latitude-longitude-what-am-i-doi */
	private double distance(double lat1, double lon1, double lat2, double lon2, char unit) {
	      double theta = lon1 - lon2;
	      double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
	      dist = Math.acos(dist);
	      dist = rad2deg(dist);
	      dist = dist * 60 * 1.1515;
	      if (unit == 'K') {
	        dist = dist * 1.609344 * 1000;
	      } else if (unit == 'N') {
	        dist = dist * 0.8684;
	        }
	      return (Math.round(dist));
	    }

	    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	    /*::  This function converts decimal degrees to radians             :*/
	    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	    private double deg2rad(double deg) {
	      return (deg * Math.PI / 180.0);
	    }

	    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	    /*::  This function converts radians to decimal degrees             :*/
	    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	    private double rad2deg(double rad) {
	      return (rad * 180.0 / Math.PI);
	    }


}
