package org.openhab.io.context.primitives;

import org.openhab.io.context.ContextGenerator;
import org.openhab.io.context.ContextService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class User {
	private static final Logger logger = LoggerFactory.getLogger(ContextService.class);
    private Context currentContext;
    private String name;
    private String email;
    public User(String name, String email)
    {
    	this.name = name;
    	this.email = email;
    	ContextGenerator c =  ContextGenerator.getInstance();
    	currentContext = c.getCurrentContext(this);
    	logger.debug(currentContext.toString());
    	
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
	
	public boolean updateContext()
	{
		ContextGenerator c =  ContextGenerator.getInstance();
		Context t = c.getCurrentContext(this);
		if(!currentContext.equalsIgnoreTime(t)) {
			logger.debug(t.toString());
			currentContext = t; // New context!!
			return true;
		}
		return false; // no new context
	}
	

	public String toString()
	{
		return name+" :"+email;
	}
}
