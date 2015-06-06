package org.openhab.io.coachman.interpretation.CriteriaLibrary;

import org.openhab.io.coachman.interpretation.Criteria;
import org.openhab.io.coachman.primitives.User;

public class NotAtHomeContextCriteria extends Criteria {

	@Override
	public boolean meetsCriteria(User u) {
		return u.getCurrentContext().getLocation().getDistanceToHome() > User.radius;
	}

}
