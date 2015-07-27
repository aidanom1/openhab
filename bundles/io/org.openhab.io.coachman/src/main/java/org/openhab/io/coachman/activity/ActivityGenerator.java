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

package org.openhab.io.coachman.activity;

import java.util.List;

import org.openhab.io.coachman.ContextService;
import org.openhab.io.coachman.activity.oauth.CalendarConfigurationImpl;
import org.openhab.io.coachman.data_access.CalDAO;
import org.openhab.io.coachman.data_access.GeoDAO;
import org.openhab.io.coachman.data_access.SQLDAO;
import org.openhab.io.coachman.primitives.Activity;
import org.openhab.io.coachman.primitives.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.api.client.util.Lists;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;


/**
 * 
 */
public class ActivityGenerator {
	private static final Logger logger = LoggerFactory
			.getLogger(ContextService.class);


	private boolean isinit = false;

	private SQLDAO sqldao;
    private GeoDAO geodao;
    private CalDAO caldao;
    
	/** Authorizes the installed application to access user's protected data. */
	public ActivityGenerator() {
		sqldao = SQLDAO.getInstance();
		geodao = GeoDAO.getInstance();
        caldao = CalDAO.getInstance();
        if(caldao.initialised()) {
        	isinit = true;
        }


	}

	public Activity getUserActivity(User u) {
		logger.info("Fetching current event for "+u.getName());
		Event e = caldao.getCurrentEvent(u);
		Activity a = new Activity();
		if (e == null)
			return a;
		if(e.getDescription() != null)
			a.setDescription(e.getDescription());
		if (e.getSummary() != null)
			a.setSummary(e.getSummary());
		logEvent(e, u);
	    logger.debug(e.getSummary());
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
