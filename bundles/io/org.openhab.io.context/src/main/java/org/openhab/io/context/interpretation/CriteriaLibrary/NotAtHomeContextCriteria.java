package org.openhab.io.context.interpretation.CriteriaLibrary;

import org.openhab.io.context.interpretation.Criteria;
import org.openhab.io.context.primitives.User;

public class NotAtHomeContextCriteria extends Criteria {

	@Override
	public boolean meetsCriteria(User u) {
		return u.getCurrentContext().getLocation().getDistanceToHome() > User.radius;
	}

}
