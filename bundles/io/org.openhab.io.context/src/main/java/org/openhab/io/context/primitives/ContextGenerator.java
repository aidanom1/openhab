package org.openhab.io.context.primitives;

import java.util.Calendar;
import java.util.Date;

import org.openhab.io.context.activity.ActivityGenerator;
import org.openhab.io.context.location.LocationList;
/* Generates context for a given user at a given time */
public class ContextGenerator {
	
	private static volatile ContextGenerator c;
	private LocationList l;
	private ActivityGenerator a;
	
	private ContextGenerator() {
		l = new LocationList();
		a = new ActivityGenerator();
	}
	
	public static ContextGenerator getInstance()
	{
		if(c == null)
		{
			synchronized (ContextGenerator.class)
			{
				if(c == null)
				{
					c = new ContextGenerator();
				}
			}
		}
		return c;
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
		c.setLocation(l.getUserLocation(u));
		c.setActivity(a.getUserActivity(u));
		
		return c;
	}

}
