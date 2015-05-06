package org.openhab.io.context.interpretation.CriteriaLibrary;

import org.openhab.io.context.interpretation.Criteria;
import org.openhab.io.context.primitives.Context;
import org.openhab.io.context.primitives.User;

public class AtHomeContextCriteria extends Criteria{

	/* User meets the criteria of being at home if the following is satisfied
	 * if first context update simply check if distance to home is < radius
	 * else if last context distance to home >= radius and current context update < radius then return true
	 * else return false
	 * @see org.openhab.io.context.interpretation.Criteria#meetsCriteria(org.openhab.io.context.primitives.User)
	 */
	@Override
	public boolean meetsCriteria(User u) {
		if(u.getRecentContexts().isEmpty()) { // No recent contexts
			return u.getCurrentContext().getLocation().getDistanceToHome() < User.radius;
		}
	    Context prevContext = u.getRecentContexts().peekFirst();
	    if(prevContext.getLocation().getDistanceToHome() >= User.radius &&
	    		u.getCurrentContext().getLocation().getDistanceToHome() < User.radius) {
	    	return true;
	    }
		return false;
	}

}
