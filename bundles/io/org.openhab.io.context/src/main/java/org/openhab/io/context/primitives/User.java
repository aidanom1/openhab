package org.openhab.io.context.primitives;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.LinkedList;

import org.openhab.core.events.EventPublisher;
import org.openhab.core.library.types.ContextType;
import org.openhab.core.library.types.StringType;
import org.openhab.io.context.ContextGenerator;
import org.openhab.io.context.ContextService;
import org.openhab.io.context.interpretation.ContextChangeInterpreter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.openhab.io.rest.RESTApplication;
public class User {
	private static final Logger logger = LoggerFactory.getLogger(ContextService.class);

    private Context currentContext = null;;
    private String name;
    private String email;
    public static int radius;
    private LinkedList<Context> recentContexts;
    protected EventPublisher eventPublisher;
    
    public User(String name, String email, EventPublisher eventPublisher2)
    {
    	this.name = name;
    	this.email = email;
    	recentContexts = new LinkedList<Context>();
    	this.eventPublisher = eventPublisher2;
    	ContextGenerator c =  ContextGenerator.getInstance();
    	currentContext = c.getCurrentContext(this);
    	logger.debug("currentContext = c.getCurrentContext(this);");
    	processContext();
    	logger.debug("processContext();");
    	logContext(currentContext);
    	logger.debug("logContext(currentContext);");
    	
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
		logger.debug("Updating context");
		ContextGenerator c =  ContextGenerator.getInstance();
		Context newContext = c.getCurrentContext(this);
		if(!currentContext.equalsIgnoreTime(newContext)) {
			logger.info(newContext.toString());
			recentContexts.addFirst(currentContext);
			if(recentContexts.size() > 16) recentContexts.removeLast();
		    currentContext = newContext; // New context!!
	    	processContext();
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
	private Connection connection = null;
	
	private void processContext() {
		if(currentContext == null) return;
		if(currentContext.getActivity() == null) return;
		if(currentContext.getLocation() == null) return;
		if(currentContext.getDate() == null) return;
		if(currentContext.getUser() == null) return;
		ContextChangeInterpreter cci = new ContextChangeInterpreter();
		ContextType highLevelContext = cci.getContext(this);
		if(highLevelContext != null) {
			eventPublisher.postUpdate(name,highLevelContext);
			eventPublisher.postUpdate(name+"_Context", new StringType(cci.getContextAsString(highLevelContext)));
		}		
	}
	
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
	
	public LinkedList<Context> getRecentContexts() {
		return recentContexts;
	}

	public Context getCurrentContext() {
		return currentContext;
	}

}
