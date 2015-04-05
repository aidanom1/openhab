package org.openhab.io.context.primitives;

public class Activity {
    private String activity = "Nothing yet";
    
    public String toString() {
    	return activity;
    }
    
    public boolean equals(Activity a)
    {
    	if(activity.equalsIgnoreCase(a.toString())) {
    		return true;
    	}
    	else {
    		return false;
    	}
    }
}
