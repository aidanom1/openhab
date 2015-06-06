package org.openhab.io.coachman.interpretation.CriteriaLibrary;

import org.openhab.io.coachman.interpretation.Criteria;
import org.openhab.io.coachman.primitives.User;

public class NotAtHomeHolidaysContextCriteria extends Criteria {

	@Override
	public boolean meetsCriteria(User u) {
		return meetsCriteria(u,"HOLIDAYS");
	}

}
