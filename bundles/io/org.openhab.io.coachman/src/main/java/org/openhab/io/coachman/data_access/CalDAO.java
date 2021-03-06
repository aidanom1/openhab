package org.openhab.io.coachman.data_access;

import java.util.List;

import org.openhab.io.coachman.ContextService;
import org.openhab.io.coachman.activity.oauth.CalendarConfigurationImpl;
import org.openhab.io.coachman.primitives.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;

public class CalDAO {
	private static final Logger logger = LoggerFactory.getLogger(ContextService.class);
	private static CalDAO instance = null;
	private CalendarConfigurationImpl t;
	public static CalDAO getInstance() {
		if(instance == null) {
			instance = new CalDAO();
		}
		return instance;
	}

	public boolean initialised() {
		try {
			t = new CalendarConfigurationImpl();
			t.initialize();
			if (t.isConfiguredCorrectly()) {
				return true;
			}
		} catch (Exception e) {
			logger.debug("org.openhab.core.context.activity " + e.toString());
			return false;
		}
		return false;
	}
	
	/*************************************
	 * 
	 * @param u - User object 
	 * @return Event - Current event for User u
	 */
	public Event getCurrentEvent(User u) {
		Events e = t.getUserEvents(u);
		if(e == null) return null;
		logger.debug(e.getSummary());
		List<Event> items = e.getItems();

		for (Event event : items) {
			logger.debug(event.getSummary());
			EventDateTime start = event.getStart();
			EventDateTime end = event.getEnd();
			if (start.getDateTime().getValue() <= System.currentTimeMillis()
					&& end.getDateTime().getValue() > System
							.currentTimeMillis()) {
				return event;
			}
		}
		return null;
	}
}
