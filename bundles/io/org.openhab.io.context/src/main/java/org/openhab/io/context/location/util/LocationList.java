package org.openhab.io.context.location.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.openhab.io.context.location.LocationService;
import org.openhab.io.context.primitives.*;
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
	private static final Logger logger = LoggerFactory.getLogger(LocationService.class);
	private Map<String, String> sqlTables = new HashMap<String, String>();
	private GeoApiContext context = null;// new GeoApiContext().setApiKey("AIzaSyBGAZA2p6mbK9k2LGNJji_U1BK1dancDnc");

	private Connection connection = null;
	
    public LocationList()
    {
		context =  new GeoApiContext().setApiKey("AIzaSyBGAZA2p6mbK9k2LGNJji_U1BK1dancDnc");
    }

    
    public Location getUserLocation(User u)
    {
    	Location l = new Location();
		DistanceMatrix req = null;
		try {
			Class.forName(driverClass).newInstance();
			connection = DriverManager.getConnection(url, "openhab", "openhab");
			Statement st = connection.createStatement();
		    st = connection.createStatement();
			String query = "select Time,Value from Item12 where (Value REGEXP '.*"+u.getName()+"$')";
			ResultSet t = st.executeQuery(query);
			t.last();
			String temp = t.getString(2);
			String[] tempArray = temp.split(",");
			l.setLatitude(Double.parseDouble(tempArray[0]));
			l.setLongitude(Double.parseDouble(tempArray[1]));
			try {
	             req = DistanceMatrixApi.newRequest(context)
		        .origins(new LatLng(l.getLatitude(), l.getLongitude()))
		        .destinations(new LatLng(LocationService.HOME_LATITUDE, LocationService.HOME_LONGITUDE))
		        .await();
			}
			catch(Exception e)
			{
				logger.debug("org.openhab.core.context.location exception"+e.toString());
			}

	
	        l.setLocationAsString(req.originAddresses[0]);
	        l.setDistanceToHome(req.rows[0].elements[0].distance.inMeters);
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

}
