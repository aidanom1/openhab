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
    
	private Connection connection = null;
	
    
    public ArrayList<User> getUsers() {
    	ArrayList<User> users = new ArrayList<User>();
    	ArrayList<String> tempArrayList = new ArrayList<String>();
    	try {
			// Reset the error counter           
			logger.debug("LocationList: Attempting to connect to database {}", url);
			Class.forName(driverClass).newInstance();
			connection = DriverManager.getConnection(url, "openhab", "openhab");
			logger.debug("LocationList: Connected to database {}", url);

			Statement st = connection.createStatement();

			// Turn use of the cursor on.
			ResultSet rs = st.executeQuery("SELECT * from Item12");
			while (rs.next()) {
				String temp = rs.getString("Value");
				String[] tempArray = temp.split(",");
				String user = tempArray[tempArray.length-1];
				if(!tempArrayList.contains(user)) {
					logger.debug("LocationList: adding user "+user);
					users.add(new User(user));
					tempArrayList.add(user);
				}
			}
			rs.close();
			st.close();
	} catch (Exception e) {
		logger.error("LocationList: Failed connecting to the SQL database using: driverClass=" + driverClass + ", url="
				+ url + ", user=" + user + ", password=" + password, e);

	}
    	disconnectFromDatabase();
		return users;
    }
    
    public Location getUserLocation(User u)
    {
    	Location l = new Location();
		try {
			logger.debug("LocationList: Attempting to connect to database {}", url);
			Class.forName(driverClass).newInstance();
			connection = DriverManager.getConnection(url, "openhab", "openhab");
			logger.debug("LocationList: Connected to database {}", url);
			Statement st = connection.createStatement();
				st = connection.createStatement();
				String query = "select Time,Value from Item12 where (Value REGEXP '.*"+u.getName()+"$')";
				logger.debug("LocationList: "+query);
				ResultSet t = st.executeQuery(query);
				t.last();
				String temp = t.getString(2);
				String[] tempArray = temp.split(",");
				l.setLatitude(Double.parseDouble(tempArray[0]));
				l.setLongitude(Double.parseDouble(tempArray[1]));
				logger.debug("LocationList: adding location "+l);
				t.close();
				st.close();
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
