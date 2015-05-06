package org.openhab.io.context.interpretation.CriteriaLibrary;

import java.util.LinkedList;

import org.openhab.io.context.interpretation.Criteria;
import org.openhab.io.context.location.LocationList;
import org.openhab.io.context.primitives.Context;
import org.openhab.io.context.primitives.User;

public class NotAtHomeTravelingContextCriteria extends Criteria {

	/**
	 * Returns whether the user meets the criteria of travelling. The criteria
	 * of not at home has already been met. The definition of travelling is whether
	 * 2 out of the 3 most recent context updates are more than 10 meters away from the 
	 * previous one (to account for brief stops)
	 * @param u The user we are testing
	 * @return  true if user meets criteria, false otherwise
	 */
	@Override
	public boolean meetsCriteria(User u) {
		LinkedList<Context> recentContexts = u.getRecentContexts();
		if(recentContexts.size() < 3) {return false;} // Who knows if we are travelling in this case, need 4 points
		double distances[] = {0.0,0.0,0.0}; // 3 distances, 1 is allows to be 0
		distances[0] = LocationList.distance(u.getCurrentContext().getLocation().getLatitude(), 
				                            u.getCurrentContext().getLocation().getLongitude(), 
				                             recentContexts.get(0).getLocation().getLatitude(), 
				                             recentContexts.get(0).getLocation().getLongitude(), 'K');
		distances[1] = LocationList.distance(recentContexts.get(0).getLocation().getLatitude(), 
				                             recentContexts.get(0).getLocation().getLongitude(), 
                                             recentContexts.get(1).getLocation().getLatitude(), 
                                             recentContexts.get(1).getLocation().getLongitude(), 'K');	
		distances[2] = LocationList.distance(recentContexts.get(1).getLocation().getLatitude(), 
				                             recentContexts.get(1).getLocation().getLongitude(), 
                                             recentContexts.get(2).getLocation().getLatitude(), 
                                             recentContexts.get(2).getLocation().getLongitude(), 'K');
		if(distances[0] == 0 && distances[1] == 0) return false;
		if(distances[1] == 0 && distances[2] == 0) return false;
		if(distances[0] == 0 && distances[2] == 0) return false;
		
		// Only need two distances to be > 20 meters to indicate user is travelling
		if(distances[0] > 20 && distances[1] > 20) return true;
		if(distances[0] > 20 && distances[2] > 20) return true;
		if(distances[1] > 20 && distances[2] > 20) return true;
		
		return false;
	}

}
