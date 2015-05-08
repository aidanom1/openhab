package org.openhab.io.context.interpretation.CriteriaLibrary;

import org.openhab.io.context.interpretation.Criteria;
import org.openhab.io.context.primitives.Context;
import org.openhab.io.context.primitives.User;

public class NotAtHomeContextCriteria extends Criteria {

	@Override
	public boolean meetsCriteria(User u) {
		String now = String.valueOf(System.currentTimeMillis());
		String then = String.valueOf(u.getCurrentContext().getDate().getTime());

		logger.info("now = "+now+", then = "+then);
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
