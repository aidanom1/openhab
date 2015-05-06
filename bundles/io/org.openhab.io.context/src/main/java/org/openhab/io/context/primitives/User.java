package org.openhab.io.context.primitives;



import java.util.LinkedList;
import org.openhab.core.events.EventPublisher;
import org.openhab.core.library.types.ContextType;
import org.openhab.core.library.types.StringType;
import org.openhab.io.context.ContextGenerator;
import org.openhab.io.context.ContextService;
import org.openhab.io.context.data_access.SQLDAO;
import org.openhab.io.context.interpretation.ContextChangeInterpreter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class User {
	private static final Logger logger = LoggerFactory.getLogger(ContextService.class);

    private Context currentContext = null;;
    private String name;
    private String email;
    public static int radius;
    private LinkedList<Context> recentContexts;
    protected EventPublisher eventPublisher;
    private SQLDAO sqldao;
    
    public User(String name, String email, EventPublisher eventPublisher2)
    {
    	this.name = name;
    	this.email = email;
    	sqldao = new SQLDAO();
    	recentContexts = new LinkedList<Context>();
    	this.eventPublisher = eventPublisher2;
    	ContextGenerator c =  ContextGenerator.getInstance();
    	currentContext = c.getCurrentContext(this);
    	processContext();
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
			return true;
		} else{
			if(ContextService.DEMO_MODE == false) {
			    logger.info(name+" no update : distance to home is "+newContext.getLocation().getDistanceToHome());
			}
			return false; // no new context
		}

	}
	

	@Override
	public String toString()
	{
		return name+" :"+email;
	}

	
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
		sqldao.logUserContext(currentContext);
	}

	
	public LinkedList<Context> getRecentContexts() {
		return recentContexts;
	}

	public Context getCurrentContext() {
		return currentContext;
	}

}
