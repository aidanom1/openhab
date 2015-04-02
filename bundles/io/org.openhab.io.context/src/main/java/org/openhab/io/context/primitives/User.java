package org.openhab.io.context.primitives;

public class User {
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
	
	public Boolean setCurrentLocation(Location currentLocation) {
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
