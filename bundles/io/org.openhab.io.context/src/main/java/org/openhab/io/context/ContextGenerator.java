package org.openhab.io.context;

import java.util.Calendar;
import java.util.Date;

import org.openhab.io.context.activity.ActivityGenerator;
import org.openhab.io.context.location.LocationList;
import org.openhab.io.context.primitives.Context;
import org.openhab.io.context.primitives.Location;
import org.openhab.io.context.primitives.User;
/* Generates context for a given user at a given time */
public class ContextGenerator {
	
	private static volatile ContextGenerator c;
	private LocationList l;
	private ActivityGenerator a;
	
	private ContextGenerator() {
		//l = new LocationList();
		a = new ActivityGenerator();
	}
	
	public static ContextGenerator getInstance()
	{
		if(c == null || c.initialisedIncorrectly())
		{
					c = new ContextGenerator();
		}
		return c;
	}
	
	private boolean initialisedIncorrectly() {
		if(a.initialised()) {
			return false;
		}
		return true;
	}

	public static Context generateContext(User u, Date d)
	{
		return new Context(u);
	}
	
	/*
	 * This function generates the current context for a given user.
	 */
	public Context getCurrentContext(User u)
	{
		Context c = new Context(u); // Name set
		c.setDate(Calendar.getInstance()); // Time set
		//c.setLocation(l.getUserLocation(u));
		c.setLocation(new Location());
		c.setActivity(a.getUserActivity(u));
		
		return c;
	}

}
