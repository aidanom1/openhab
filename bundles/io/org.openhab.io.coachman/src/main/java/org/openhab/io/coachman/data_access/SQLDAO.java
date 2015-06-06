package org.openhab.io.coachman.data_access;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.openhab.io.coachman.ContextService;
import org.openhab.io.coachman.primitives.Context;
import org.openhab.io.coachman.primitives.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SQLDAO {
	
	private String driverClass = "com.mysql.jdbc.Driver";
	private String url = "jdbc:mysql://127.0.0.1:3306/openhab";
	private Connection connection = null;
	private static final Logger logger = LoggerFactory.getLogger(ContextService.class);
	private static SQLDAO instance = null;
	private Statement st;
	
	
	private SQLDAO() {
		try {
			Class.forName(driverClass).newInstance();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.debug(e.toString());
		}
		try {
			connection = DriverManager.getConnection(url, "openhab", "openhab");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.debug(e.toString());
		}
	}
	
	public static SQLDAO getInstance() {
		if(instance == null) {
			instance = new SQLDAO();
		}
		return instance;
	}
	
	public boolean logUserContext(Context c) {
		SimpleDateFormat f = new SimpleDateFormat("EEE HH:mm:ss dd/MM/yyyy");
        String query = "insert into context (user,time,location,activity) values ('"+
                c.getUser().toString()+"','"+
		        f.format(c.getDate().getTime())+"','"+
                c.getLocation().toString()+"','"+
		        c.getActivity().toString()+"');";
        return executeInsertQuery(query);
	}
	
	public String getUserLocation(User u) {
		String query = "select Time,Value from Item12 where (Value REGEXP '.*"+u.getName()+"$')";
		ResultSet t = executeSelectQuery(query);
		String temp = null;
		try {
			t.last();
			temp = t.getString(2);
			t.close();
			st.close();
		} catch (SQLException e) {
			logger.debug(e.toString());
		}
		
		return temp;
	}
	
	public void logEvent(User u2, String summary, String location, double lat,
			double lng) {
        String query = "insert into MASTERLOCATIONLIST (user,category,address,lng,lat) values " +
        		"('"+u2.getName()+"','"+
        		summary+"','"+
        		location+"',"+String.valueOf(lat)+","+String.valueOf(lng)+")";
        executeInsertQuery(query);
		
	}
	

	public ArrayList<double[]> getLocations(String string, User u) {
		String query = "select lng,lat from MASTERLOCATIONLIST where user='"+u.getName()+"' and category='"+string+"'";
		ResultSet t = executeSelectQuery(query);
		ArrayList<double[]> locations = new ArrayList<double[]>();
		try {
			while(t.next()) {
				locations.add(new double[] {t.getDouble(1),t.getDouble(2)});
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.debug(e.toString());
		}
		return locations;
	}
	
	
	/**********************************************************************************************************/
	
	private ResultSet executeSelectQuery(String query) {
		try {

	    st = connection.createStatement();	
		logger.debug(query);
		ResultSet t = st.executeQuery(query);
		return t;	
	} catch (Exception e) {
		logger.debug(e.toString());
	}
		return null;
	}
	
	private boolean executeInsertQuery(String query) {
		try {
	        st = connection.createStatement();
			logger.debug(query);
			st.executeUpdate(query);
			st.close();
			return true;
		}
		catch(Exception e) {
			logger.debug("Error writing context to database "+e.toString());
		}
		return false;
	}
	




}
