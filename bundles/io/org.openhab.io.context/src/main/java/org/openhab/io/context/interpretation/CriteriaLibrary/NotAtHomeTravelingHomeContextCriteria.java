package org.openhab.io.context.interpretation.CriteriaLibrary;

import java.util.LinkedList;

import org.openhab.io.context.ContextService;
import org.openhab.io.context.interpretation.Criteria;
import org.openhab.io.context.location.LocationList;
import org.openhab.io.context.primitives.Context;
import org.openhab.io.context.primitives.User;

public class NotAtHomeTravelingHomeContextCriteria extends Criteria {

	@Override
	public boolean meetsCriteria(User u) {
		LinkedList<Context> recentContexts = u.getRecentContexts();
		if(recentContexts.size() < 3) {return false;} // Who knows if we are travelling in this case, need 4 points
		double distances[] = {0.0,0.0,0.0,0.0}; // 4 distances
		distances[0] = LocationList.distance(u.getCurrentContext().getLocation().getLatitude(), 
				                             u.getCurrentContext().getLocation().getLongitude(), 
				                             ContextService.HOME_LATITUDE, 
				                             ContextService.HOME_LONGITUDE, 'K');
		distances[1] = LocationList.distance(recentContexts.get(0).getLocation().getLatitude(), 
				                             recentContexts.get(0).getLocation().getLongitude(), 
				                             ContextService.HOME_LATITUDE, 
				                             ContextService.HOME_LONGITUDE, 'K');
		distances[2] = LocationList.distance(recentContexts.get(1).getLocation().getLatitude(), 
				                             recentContexts.get(1).getLocation().getLongitude(), 
				                             ContextService.HOME_LATITUDE, 
				                             ContextService.HOME_LONGITUDE, 'K');
		distances[3] = LocationList.distance(recentContexts.get(2).getLocation().getLatitude(), 
                                             recentContexts.get(2).getLocation().getLongitude(), 
                                             ContextService.HOME_LATITUDE, 
                                             ContextService.HOME_LONGITUDE, 'K');
		
		if(distances[0] <= distances[1] && distances[1] <= distances[2] && distances[2] <= distances[3]) return true;
		return false;
	}

}
