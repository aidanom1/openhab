package org.openhab.io.coachman.interpretation.CriteriaLibrary;




import org.openhab.io.coachman.interpretation.Criteria;
import org.openhab.io.coachman.primitives.User;


public class NotAtHomeWorkContextCriteria extends Criteria {

	/**
	 * Returns whether the user meets the criteria of being at work. The criteria
	 * of not at home has already been met. The definition of being at work is
	 * whether the use is 100 meters or less then a location designated as WORK
	 * @param u The user we are tested
	 * @return  true if user meets criteria, false otherwise
	 */
	@Override
	public boolean meetsCriteria(User u) {
		return meetsCriteria(u,"WORK");
	}

}
