package org.openhab.io.context.activity;

import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.calendar.*;

public class UserCalendar extends com.google.api.services.calendar.Calendar{
	String API_KEY = "AIzaSyBGAZA2p6mbK9k2LGNJji_U1BK1dancDnc";
	public UserCalendar(HttpTransport arg0, JsonFactory arg1,
			HttpRequestInitializer arg2) {
		super(arg0, arg1, arg2);
		Calendar.Builder t = new Calendar.Builder(arg0, arg1, arg2);
		t.setCalendarRequestInitializer(new CalendarRequestInitializer(API_KEY));
		// TODO Auto-generated constructor stub
	}

}
