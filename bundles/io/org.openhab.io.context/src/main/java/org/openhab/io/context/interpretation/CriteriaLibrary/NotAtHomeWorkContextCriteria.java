package org.openhab.io.context.interpretation.CriteriaLibrary;

import java.util.ArrayList;
import java.util.Iterator;

import org.openhab.io.context.interpretation.Criteria;
import org.openhab.io.context.location.LocationList;
import org.openhab.io.context.primitives.User;

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
		ArrayList<double[]> locations = sqldao.getLocations("WORK",u);
		Iterator<double[]> i = locations.iterator();
		while(i.hasNext()) {
			double[] work = i.next();
			double distance = LocationList.distance(u.getCurrentContext().getLocation().getLatitude(),
					                                 u.getCurrentContext().getLocation().getLongitude(), // Don't forget to reverse lat,lnk
					                                 work[1], work[0], 'K');
			if(distance < 100) return true;
		}
		return false;
	}

}
