package org.openhab.io.context.interpretation;

import java.util.ArrayList;
import java.util.Iterator;

import org.openhab.io.context.ContextService;
import org.openhab.io.context.data_access.GeoDAO;
import org.openhab.io.context.data_access.SQLDAO;
import org.openhab.io.context.location.LocationList;
import org.openhab.io.context.primitives.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Criteria {
	protected SQLDAO sqldao;
	protected GeoDAO geodao;
	protected static final Logger logger = LoggerFactory.getLogger(ContextService.class);
	public Criteria() {
		sqldao = SQLDAO.getInstance();
		geodao = GeoDAO.getInstance();
	}
    public abstract boolean meetsCriteria(User u);
    
    public boolean meetsCriteria(User u, String location) {
		ArrayList<double[]> locations = sqldao.getLocations(location,u);
		Iterator<double[]> i = locations.iterator();
		while(i.hasNext()) {
			double[] work = i.next();
			double distance = LocationList.distance(u.getCurrentContext().getLocation().getLatitude(),
					                                 u.getCurrentContext().getLocation().getLongitude(), 
					                                 work[0], work[1], 'K');
			logger.info("Distance ="+distance);
			if(distance < 100) return true;
		}
		return false;
    }
}
