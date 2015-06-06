package org.openhab.io.coachman.interpretation;

import java.util.ArrayList;
import java.util.Iterator;

import org.openhab.io.coachman.ContextService;
import org.openhab.io.coachman.data_access.CalDAO;
import org.openhab.io.coachman.data_access.GeoDAO;
import org.openhab.io.coachman.data_access.SQLDAO;
import org.openhab.io.coachman.location.LocationList;
import org.openhab.io.coachman.primitives.Location;
import org.openhab.io.coachman.primitives.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.services.calendar.model.Event;

public abstract class Criteria {
	protected SQLDAO sqldao;
	protected GeoDAO geodao;
	protected CalDAO caldao;
	protected static final Logger logger = LoggerFactory.getLogger(ContextService.class);
	public Criteria() {
		sqldao = SQLDAO.getInstance();
		geodao = GeoDAO.getInstance();
		caldao = CalDAO.getInstance();
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
			if(distance < 100) return true;
		}
		return false;
    }
    
    public ArrayList<Location> getAllLocations(User u, String location) {
		ArrayList<double[]> locationCoords = sqldao.getLocations(location,u);
		ArrayList<Location> locations = new ArrayList<Location>();
		Iterator<double[]> i = locationCoords.iterator();
		while(i.hasNext()) {
			Location l = new Location();
			double[] work = i.next();
			l.setLatitude(work[0]);
			l.setLongitude(work[1]);
			locations.add(l);
		}
		return locations;
    }
    
    public boolean matchesEvent(User u, String event) {
    	logger.debug("Getting event");
    	Event now = caldao.getCurrentEvent(u);
    	if(now == null) return false;
    	if(now.getSummary().equals(event)) {
    		logger.info(now.getSummary()+" == "+event);
    		return true;
    	}
    	else {
    		logger.info(now.getSummary()+" == "+event);
    		return false;
    	}
    }
}
