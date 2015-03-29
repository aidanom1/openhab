package org.openhab.io.context.primitives;

public class User {
    private Location currentLocation;
    private String name;
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
	public Location getCurrentLocation() {
		return currentLocation;
	}
	public void setCurrentLocation(Location currentLocation) {
		this.currentLocation = currentLocation;
	}
}
