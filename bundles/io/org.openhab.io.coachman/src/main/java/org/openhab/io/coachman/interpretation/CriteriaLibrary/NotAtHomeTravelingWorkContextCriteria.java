package org.openhab.io.coachman.interpretation.CriteriaLibrary;

import org.openhab.io.coachman.interpretation.Criteria;
import org.openhab.io.coachman.location.LocationList;
import org.openhab.io.coachman.primitives.User;

public class NotAtHomeTravelingWorkContextCriteria extends Criteria {

	@Override
	public boolean meetsCriteria(User u) {
		return LocationList.enrouteToAny(u, getAllLocations(u,"WORK"));
	}

}
