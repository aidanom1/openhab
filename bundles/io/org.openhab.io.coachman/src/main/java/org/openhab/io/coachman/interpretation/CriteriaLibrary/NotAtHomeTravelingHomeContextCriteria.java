package org.openhab.io.coachman.interpretation.CriteriaLibrary;

import org.openhab.io.coachman.ContextService;
import org.openhab.io.coachman.interpretation.Criteria;
import org.openhab.io.coachman.location.LocationList;
import org.openhab.io.coachman.primitives.Location;
import org.openhab.io.coachman.primitives.User;

public class NotAtHomeTravelingHomeContextCriteria extends Criteria {

	@Override
	public boolean meetsCriteria(User u) {
        Location home = new Location();
        home.setLatitude(ContextService.HOME_LATITUDE);
        home.setLongitude(ContextService.HOME_LONGITUDE);

        
		return LocationList.enrouteTo(u, home);
	}

}
