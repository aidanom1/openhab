package org.openhab.io.context.primitives;

import java.util.Date;
/* Generates context for a given user at a given time */
public class ContextGenerator {
	public static Context generateContext(User u, Date d)
	{
		return new Context();
	}

}
