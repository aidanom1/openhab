package org.openhab.io.context.location;

import org.openhab.io.context.ContextService;
import org.openhab.io.context.data_access.GeoDAO;
import org.openhab.io.context.data_access.SQLDAO;
import org.openhab.io.context.primitives.Location;
import org.openhab.io.context.primitives.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocationList {
    /**
	 * 
	 */

	public static final int CURRENT_LOCATION = 0;
    private SQLDAO sqldao;
    private GeoDAO geodao;

	private static final Logger logger = LoggerFactory.getLogger(ContextService.class);

	
    public LocationList()
    {
    	sqldao = SQLDAO.getInstance();
    	geodao = GeoDAO.getInstance();

    }

    
	public Location getUserLocation(User u) {
		Location l = new Location();
		String temp = sqldao.getUserLocation(u); // Get user location from database
		String[] tempArray = temp.split(",");
		l.setLatitude(Double.parseDouble(tempArray[0]));
		l.setLongitude(Double.parseDouble(tempArray[1]));
		
		l.setLocationAsString(geodao.getOriginAddress(l));
		
		l.setDistanceToHome(distance(l.getLatitude(), l.getLongitude(),
				ContextService.HOME_LATITUDE, ContextService.HOME_LONGITUDE,
				'K'));
		
		return l;
	}

	

	
	/* http://stackoverflow.com/questions/3694380/calculating-distance-between-two-points-using-latitude-longitude-what-am-i-doi */
	public static double distance(double lat1, double lon1, double lat2, double lon2, char unit) {
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
	    private static double deg2rad(double deg) {
	      return (deg * Math.PI / 180.0);
	    }

	    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	    /*::  This function converts radians to decimal degrees             :*/
	    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	    private static double rad2deg(double rad) {
	      return (rad * 180.0 / Math.PI);
	    }


}
