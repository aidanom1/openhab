package org.openhab.io.context.interpretation.CriteriaLibrary;

import org.openhab.io.context.interpretation.Criteria;
import org.openhab.io.context.primitives.User;

public class NotAtHomeSchoolContextCriteria extends Criteria {

	@Override
	public boolean meetsCriteria(User u) {
		return meetsCriteria(u,"SCHOOL");
	}

}
