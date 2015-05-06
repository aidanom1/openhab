/*
 * Copyright (c) 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.openhab.io.context.activity;

import java.util.List;
import org.openhab.io.context.ContextService;
import org.openhab.io.context.activity.oauth.CalendarConfigurationImpl;
import org.openhab.io.context.data_access.GeoDAO;
import org.openhab.io.context.data_access.SQLDAO;
import org.openhab.io.context.primitives.Activity;
import org.openhab.io.context.primitives.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.api.client.util.Lists;
import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;


/**
 * 
 */
public class ActivityGenerator {
	private static final Logger logger = LoggerFactory
			.getLogger(ContextService.class);

	static final java.util.List<Calendar> addedCalendarsUsingBatch = Lists
			.newArrayList();

	private boolean isinit = false;
	private CalendarConfigurationImpl t;
	private SQLDAO sqldao;
    private GeoDAO geodao;
    
	/** Authorizes the installed application to access user's protected data. */
	public ActivityGenerator() {
		sqldao = SQLDAO.getInstance();
		geodao = GeoDAO.getInstance();

		try {
			t = new CalendarConfigurationImpl();
			t.initialize();
			if (t.isConfiguredCorrectly()) {
				isinit = true;
			}
		} catch (Exception e) {
			logger.debug("org.openhab.core.context.activity " + e.toString());
		}

	}

	public Activity getUserActivity(User u) {
		com.google.api.services.calendar.model.Events e = t.getUserEvents(u);
		Activity a = new Activity();
		if (e == null)
			return a;
		List<Event> items = e.getItems();
		for (Event event : items) {
			EventDateTime start = event.getStart();
			EventDateTime end = event.getEnd();
			if (start.getDateTime().getValue() <= System.currentTimeMillis()
					&& end.getDateTime().getValue() > System
							.currentTimeMillis()) {
				if (event.getDescription() != null)
					a.setDescription(event.getDescription());
				if (event.getSummary() != null)
					a.setSummary(event.getSummary());
				logEvent(event, u);
			}
			logger.debug(event.getSummary());
		}
		return a;
	}

	private void logEvent(Event event, User u2) {

		if (event.getSummary() == null)
			return;

		if (!event.getSummary().equals("WORK")
				&& !event.getSummary().equals("SOCIAL")
				&& !event.getSummary().equals("SCHOOL")
				&& !event.getSummary().equals("SHOPPING"))
			return;
		String location = event.getLocation();
		if (location == null)
			return;
		String summary = event.getSummary();
		double coord[] = geodao.getCoordinates(location);
		sqldao.logEvent(u2, summary, location, coord[0], coord[1]);

	}

	public boolean initialised() {
		return isinit;
	}

}
