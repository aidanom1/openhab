package org.openhab.io.context.primitives;

public class Location {
    private double longitude;
    private double latitude;
    private String locationAsString;
    private double distanceToHome;
    
    public Location() {
    	
    }
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public String getLocationAsString() {
		return locationAsString;
	}
	public void setLocationAsString(String locationAsString) {
		this.locationAsString = locationAsString;
	}
	public double getDistanceToHome() {
		return distanceToHome;
	}
	public void setDistanceToHome(double distanceToHome) {
		this.distanceToHome = distanceToHome;
	}
  
	public String toString() {
		return this.longitude+","+this.latitude;
	}
    
}
