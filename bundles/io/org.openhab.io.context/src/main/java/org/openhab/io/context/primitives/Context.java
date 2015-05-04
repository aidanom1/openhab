package org.openhab.io.context.primitives;
import java.text.SimpleDateFormat;
import java.util.Calendar;
/*
 * This class contains a Context for a user. Context is defined as
 * Location
 * Time
 * Activity
 */
public class Context {
	private User user;
	private Location location;
	private Calendar date;
	private Activity activity;
	public User getUser() {
		return user;
	}

	public Context(User u)
	{
		user = u;
		location = new Location();
		activity = new Activity();
	}

	public Location getLocation()
	{
		return location;
	}

	public void setDate(Calendar d)
	{
		date = d;
	}

	public void setLocation(Location l)
	{
		location = l;
	}

	public void setActivity(Activity a)
	{
		activity = a;
	}

	public Calendar getDate() {
		return date;
	}

	public boolean equals(Context c)
	{
		if(this.equalsIgnoreTime(c) &&
				date.equals(c.getDate())) {
			return true;
		}
		else {
			return false;
		}

	}

	public boolean equalsIgnoreTime(Context c)
	{
		// sanity check
		if(user.equals(c.getUser()) &&
				location.equals(c.getLocation()) &&
				activity.equals(c.getActivity()))
		{
			return true;
		}
		else {
			return false;
		}
	}


	public Activity getActivity() {
		return activity;
	}
	
	public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd/MMM/yyyy");
		return "\n*****************************************************************\n"+
	            "*     User     = "+user+"\n"+
				"*     Time     = "+sdf.format(date.getTime())+"\n"+
	            "*     Location = "+location+"\n"+
				"*     Activity = "+activity+"\n"+
	            "******************************************************************";
	}
}
