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
		long now = System.currentTimeMillis();
		if((u.getCurrentContext().getDate().getTime() + (5*60*1000)) < now) { // we haven't moved in over 5 minutes, not likely to be travelling
			return false;
		}
		if(recentContexts.size() < 3) {return false;} // Who knows if we are travelling in this case, need 4 points
		double distances[] = {0.0,0.0,0.0}; // 3 distances, 1 is allows to be 0
		distances[0] = LocationList.distanceBetweenContexts(u.getCurrentContext(), recentContexts.get(0));
		distances[1] = LocationList.distanceBetweenContexts(recentContexts.get(0), recentContexts.get(1));
		distances[2] = LocationList.distanceBetweenContexts(recentContexts.get(1), recentContexts.get(2));
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
