package org.openhab.io.context.primitives;
import java.util.Date;
/*
 * This class contains a Context for a user. Context is defined as
 * Location
 * Time
 * Activity
 */
public class Context {
    private User user;
    private Location location;
    private Date date;
	public User getUser() {
		return user;
	}
    
}
