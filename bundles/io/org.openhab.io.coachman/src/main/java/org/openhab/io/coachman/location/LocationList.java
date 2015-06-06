package org.openhab.io.coachman.location;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import org.openhab.io.coachman.ContextService;
import org.openhab.io.coachman.data_access.GeoDAO;
import org.openhab.io.coachman.data_access.SQLDAO;
import org.openhab.io.coachman.primitives.Context;
import org.openhab.io.coachman.primitives.Location;
import org.openhab.io.coachman.primitives.User;
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

	
    public static double distanceBetweenContexts(Context c1, Context c2) {
    	Location l1 = c1.getLocation();
    	Location l2 = c2.getLocation();
        return LocationList.distance(l1.getLatitude(), l1.getLongitude(), l2.getLatitude(), l2.getLongitude(), 'K'); 
    }
    
    // Checks if the user is enroute is going towards a specific Location 
    public static boolean enrouteTo(User u, Location L) {
		LinkedList<Context> recentContexts = u.getRecentContexts();
		if(recentContexts.size() < 3) {return false;} // Who knows if we are travelling in this case, need 4 points
		double distances[] = {0.0,0.0,0.0,0.0}; // 4 distances
		distances[0] = LocationList.distance(u.getCurrentContext().getLocation().getLatitude(), 
				                             u.getCurrentContext().getLocation().getLongitude(), 
				                             L.getLatitude(), 
				                             L.getLongitude(), 'K');
		distances[1] = LocationList.distance(recentContexts.get(0).getLocation().getLatitude(), 
				                             recentContexts.get(0).getLocation().getLongitude(), 
				                             L.getLatitude(), 
				                             L.getLongitude(), 'K');
		distances[2] = LocationList.distance(recentContexts.get(1).getLocation().getLatitude(), 
				                             recentContexts.get(1).getLocation().getLongitude(), 
				                             L.getLatitude(), 
				                             L.getLongitude(), 'K');
		distances[3] = LocationList.distance(recentContexts.get(2).getLocation().getLatitude(), 
                                             recentContexts.get(2).getLocation().getLongitude(), 
				                             L.getLatitude(), 
				                             L.getLongitude(), 'K');
		
		if(distances[0] <= distances[1] && distances[1] <= distances[2] && distances[2] <= distances[3]) return true; 
		return false;
    }
    
    public static boolean enrouteToAny(User u, ArrayList<Location> l) {
    	Iterator<Location> it = l.iterator();
    	while(it.hasNext()) {
    		if(enrouteTo(u,it.next())) {
    			return true;
    		}
    	}
    	return false;
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
