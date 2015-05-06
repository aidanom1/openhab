package org.openhab.io.context.primitives;

public class Activity {
    private String activity = "Nothing yet";
    
    @Override
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

	public void setDescription(String description) {
		// TODO Auto-generated method stub
		if(activity.equals("Nothing yet"))
		    activity = description+=" : ";
		else
			activity += description+= " : ";
		
	}

	public void setSummary(String summary) {
		
		// TODO Auto-generated method stub
		if(activity.equals("Nothing yet"))
		    activity = summary+=" : ";
		else
			activity += summary+= " : ";
	}
    
    
}
