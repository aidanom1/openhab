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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class User extends AbstractBinding  {
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
    	logContext(currentContext);
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
		logger.debug("Updaing context");
		ContextGenerator c =  ContextGenerator.getInstance();
		Context newContext = c.getCurrentContext(this);
		if(!currentContext.equalsIgnoreTime(newContext)) {
			logger.info(newContext.toString());
		    if(currentContext.getLocation().getDistanceToHome() < 20
		    		&& newContext.getLocation().getDistanceToHome() >= 20) // NOT_AT_HOME
		    {
		    	eventPublisher.postUpdate(name,ContextType.NOT_AT_HOME);
		    }
		    else if(currentContext.getLocation().getDistanceToHome() >= 20
		    		&& newContext.getLocation().getDistanceToHome() < 20) // AT_HOME
		    {
		    	eventPublisher.postUpdate(name,ContextType.AT_HOME);
		    }
		    currentContext = newContext; // New context!!
			logContext(newContext); 
			return true;
		} else{
			logger.info(name+" no update : distance to home is "+newContext.getLocation().getDistanceToHome());
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
