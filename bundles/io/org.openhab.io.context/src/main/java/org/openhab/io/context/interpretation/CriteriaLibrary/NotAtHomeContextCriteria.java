package org.openhab.io.context.interpretation.CriteriaLibrary;

import org.openhab.io.context.interpretation.Criteria;
import org.openhab.io.context.primitives.Context;
import org.openhab.io.context.primitives.User;

public class NotAtHomeContextCriteria implements Criteria {

	@Override
	public boolean meetsCriteria(User u) {
		if(u.getRecentContexts().isEmpty()) { // No recent contexts
			return u.getCurrentContext().getLocation().getDistanceToHome() > User.radius;
		}
	    Context prevContext = u.getRecentContexts().peekFirst();
	    if(prevContext.getLocation().getDistanceToHome() < User.radius &&
	    		u.getCurrentContext().getLocation().getDistanceToHome() >= User.radius) {
	    	return true;
	    }
		return false;
	}

}
