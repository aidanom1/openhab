package org.openhab.io.context.primitives;

import org.openhab.io.context.LocationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class User {
	private static final Logger logger = LoggerFactory.getLogger(LocationService.class);
    private Context currentContext;
    private String name;
    private Location currentLocation;
    public User(String name)
    {
    	this.name = name;
    }
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Context getCurrentLocation() {
		return currentContext;
	}
	public void setCurrentContext(Context currentContext) {
		this.currentContext = currentContext;
	}
	
	/*
	 * Set current location - no need to do this if we have moved less than 5 meters from our last location
	 */
	public Boolean setCurrentLocation(Location currentLocation) {
		if(this.currentLocation == null)
		{
			this.currentLocation = currentLocation;
		    return true;
		}
		if(currentLocation == null)
		{
		    return false;
		}
		if(!currentLocation.equals(this.currentLocation) &&
				currentLocation.differenceInMeters(this.currentLocation) > 5)
		{
			this.currentLocation = currentLocation;
			return true;
		}
		return false;
	}
	
	public String toString()
	{
		return name+" "+currentLocation;
	}
}
