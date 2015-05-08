package org.openhab.io.context.interpretation.CriteriaLibrary;

import java.util.Date;

import org.openhab.io.context.interpretation.Criteria;
import org.openhab.io.context.primitives.User;

public class AtHomeSleepContextCriteria extends Criteria {

	/*
	 * Simple algorithm - is it between the hours of 1 a.m. and 6 a.m.? If so, have we moved in the last 30 minutes?
	 * If not, then probably asleep.
	 * (non-Javadoc)
	 * @see org.openhab.io.context.interpretation.Criteria#meetsCriteria(org.openhab.io.context.primitives.User)
	 */
	@SuppressWarnings("deprecation")
	public boolean meetsCriteria(User u) {
		Date now = new Date(System.currentTimeMillis());
		if(now.getHours() > 0 && now.getHours() < 7) {
			if((u.getCurrentContext().getDate().getTime() + 30*60*1000) < now.getTime()) {
				return true;
			}
		}
		return false;
	}

}
