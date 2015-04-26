package org.openhab.io.context.primitives;


import org.openhab.core.binding.AbstractBinding;
import org.openhab.core.events.*;
import org.openhab.core.library.types.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.text.SimpleDateFormat;


import org.openhab.io.context.ContextGenerator;
import org.openhab.io.context.ContextService;
//import org.openhab.io.rest.RESTApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;;
public class User {
	private static final Logger logger = LoggerFactory.getLogger(ContextService.class);

    private Context currentContext = null;;
    private String name;
    private String email;
    public static int radius;
    
    protected EventPublisher eventPublisher;
    public User(String name, String email, EventPublisher eventPublisher2)
    {
    	this.name = name;
    	this.email = email;
    	this.eventPublisher = eventPublisher2;
    	ContextGenerator c =  ContextGenerator.getInstance();
    	currentContext = c.getCurrentContext(this);

    	if(currentContext.getLocation().getDistanceToHome() < radius) {
    		eventPublisher.postUpdate(name,ContextType.AT_HOME);
    		updateContext("AT_HOME");
    	}
    	else {
    		eventPublisher.postUpdate(name,ContextType.NOT_AT_HOME);
    		updateContext("NOT_AT_HOME");
    	}
    	logContext(currentContext);
    	
    }

    private void updateContext(String context2) {
	    String user = name+"_Context";
	    String update = context2;
	    StringType st = new StringType(update);
	    if(eventPublisher == null) {
		    logger.info("eventPublisher == null");
	    }
	    try {
		    eventPublisher.postUpdate(user, st);
	    }
	    catch(Exception e) {
		    logger.info(e.toString());
	    }	
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
		logger.debug("Updaing context");
		ContextGenerator c =  ContextGenerator.getInstance();
		Context newContext = c.getCurrentContext(this);
		if(!currentContext.equalsIgnoreTime(newContext)) {
			logger.info(newContext.toString());
			
		    if(currentContext.getLocation().getDistanceToHome() < radius
		    		&& newContext.getLocation().getDistanceToHome() >= radius) // NOT_AT_HOME
		    {
		    	eventPublisher.postUpdate(name,ContextType.NOT_AT_HOME);
		    	updateContext("NOT_AT_HOME");
		    }
		    else if(currentContext.getLocation().getDistanceToHome() >= radius
		    		&& newContext.getLocation().getDistanceToHome() < radius) // AT_HOME
		    {
		    	eventPublisher.postUpdate(name,ContextType.AT_HOME);
		    	updateContext("AT_HOME");
		    }
		    
		    currentContext = newContext; // New context!!
	    	logContext(currentContext);
			return true;
		} else{
			if(ContextService.DEMO_MODE == false) {
			    logger.info(name+" no update : distance to home is "+newContext.getLocation().getDistanceToHome());
			}
			return false; // no new context
		}

	}
	

	public String toString()
	{
		return name+" :"+email;
	}
	private String driverClass = "com.mysql.jdbc.Driver";
	private String url = "jdbc:mysql://127.0.0.1:3306/openhab";
	private String user = "openhab";
	private String password = "openhab";
	private Connection connection = null;
	
	public void logContext(Context c)
	{
		if(c == null) return;
		if(c.getActivity() == null) return;
		if(c.getLocation() == null) return;
		if(c.getDate() == null) return;
		if(c.getUser() == null) return;
		SimpleDateFormat f = new SimpleDateFormat("EEE HH:mm:ss dd/MM/yyyy");
		try {
			Class.forName(driverClass).newInstance();
		    connection = DriverManager.getConnection(url, "openhab", "openhab");
		    Statement st = connection.createStatement();
	        st = connection.createStatement();
	       
	        String query = "insert into context (user,time,location,activity) values ('"+
	                           c.getUser().toString()+"','"+
	        		           f.format(c.getDate().getTime())+"','"+
	                           c.getLocation().toString()+"','"+
	        		           c.getActivity().toString()+"');";
			logger.debug(query);
			int t = st.executeUpdate(query);
			st.close();
		}
		catch(Exception e) {
			logger.debug("Error writing context to database "+e.toString());
		}
	}

}
