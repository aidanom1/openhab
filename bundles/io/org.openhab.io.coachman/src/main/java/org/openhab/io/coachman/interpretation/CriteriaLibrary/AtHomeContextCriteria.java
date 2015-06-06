package org.openhab.io.coachman.interpretation.CriteriaLibrary;

import org.openhab.io.coachman.interpretation.Criteria;
import org.openhab.io.coachman.primitives.Context;
import org.openhab.io.coachman.primitives.User;

public class AtHomeContextCriteria extends Criteria{

	/* User meets the criteria of being at home if the following is satisfied
	 * if first context update simply check if distance to home is < radius
	 * else if last context distance to home >= radius and current context update < radius then return true
	 * else return false
	 * @see org.openhab.io.coachman.interpretation.Criteria#meetsCriteria(org.openhab.io.coachman.primitives.User)
	 */
	@Override
	public boolean meetsCriteria(User u) {
		return u.getCurrentContext().getLocation().getDistanceToHome() < User.radius;
	}

}
