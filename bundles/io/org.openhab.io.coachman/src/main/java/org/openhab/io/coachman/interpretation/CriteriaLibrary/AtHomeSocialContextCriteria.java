package org.openhab.io.coachman.interpretation.CriteriaLibrary;

import org.openhab.io.coachman.interpretation.Criteria;
import org.openhab.io.coachman.primitives.User;

public class AtHomeSocialContextCriteria extends Criteria {

	@Override
	public boolean meetsCriteria(User u) {
		return matchesEvent(u,"SOCIAL");
	}

}
