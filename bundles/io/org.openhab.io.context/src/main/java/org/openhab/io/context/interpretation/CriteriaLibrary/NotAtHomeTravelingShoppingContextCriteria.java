package org.openhab.io.context.interpretation.CriteriaLibrary;

import org.openhab.io.context.interpretation.Criteria;
import org.openhab.io.context.location.LocationList;
import org.openhab.io.context.primitives.User;

public class NotAtHomeTravelingShoppingContextCriteria extends Criteria {

	@Override
	public boolean meetsCriteria(User u) {
		return LocationList.enrouteToAny(u, getAllLocations(u,"SHOPPING"));
	}

}
