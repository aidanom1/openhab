package org.openhab.io.context.interpretation.CriteriaLibrary;

import java.util.ArrayList;
import java.util.LinkedList;

import org.openhab.io.context.ContextService;
import org.openhab.io.context.interpretation.Criteria;
import org.openhab.io.context.location.LocationList;
import org.openhab.io.context.primitives.Context;
import org.openhab.io.context.primitives.Location;
import org.openhab.io.context.primitives.User;

public class NotAtHomeTravelingHomeContextCriteria extends Criteria {

	@Override
	public boolean meetsCriteria(User u) {
        Location home = new Location();
        home.setLatitude(ContextService.HOME_LATITUDE);
        home.setLongitude(ContextService.HOME_LONGITUDE);

        
		return LocationList.enrouteTo(u, home);
	}

}
