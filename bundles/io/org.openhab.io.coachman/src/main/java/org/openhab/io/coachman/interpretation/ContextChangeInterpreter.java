package org.openhab.io.coachman.interpretation;

import org.openhab.core.library.types.ContextType;
import org.openhab.io.coachman.ContextService;
import org.openhab.io.coachman.primitives.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContextChangeInterpreter {
	private static final Logger logger = LoggerFactory.getLogger(ContextService.class);
	
	public String getContextAsString(ContextType t)
	{
		switch(t) {
	    case AT_HOME       :                 return "AT_HOME";
	    case NOT_AT_HOME   :                 return "NOT_AT_HOME";
	    case AT_HOME_SOCIAL:                 return "AT_HOME_SOCIAL";
	    case AT_HOME_SLEEP :                 return "AT_HOME_SLEEP";
	    case AT_HOME_WORK  :                 return "AT_HOME_WORK";
	    case AT_HOME_SCHOOL:                 return "AT_HOME_SCHOOL";
	    case NOT_AT_HOME_HOLIDAYS:           return "NOT_AT_HOME_HOLIDAYS";
	    case NOT_AT_HOME_SCHOOL:             return "NOT_AT_HOME_SCHOOL";
	    case NOT_AT_HOME_SHOPPING:           return "NOT_AT_HOME_SHOPPING";
    	case NOT_AT_HOME_SOCIAL:             return "NOT_AT_HOME_SOCIAL";
	    case NOT_AT_HOME_SOCIAL_CINEMA:      return "NOT_AT_HOME_SOCIAL_CINEMA";
	    case NOT_AT_HOME_SOCIAL_DINING:      return "NOT_AT_HOME_SOCIAL_DINING";
	    case NOT_AT_HOME_TRAVELING:          return "NOT_AT_HOME_TRAVELING";
	    case NOT_AT_HOME_TRAVELING_HOLIDAYS: return "NOT_AT_HOME_TRAVELING_HOLIDAYS";
	    case NOT_AT_HOME_TRAVELING_HOME:     return "NOT_AT_HOME_TRAVELING_HOME";
	    case NOT_AT_HOME_TRAVELING_SCHOOL:   return "NOT_AT_HOME_TRAVELING_SCHOOL";
	    case NOT_AT_HOME_TRAVELING_SHOPPING: return "NOT_AT_HOME_TRAVELING_SHOPPING";
	    case NOT_AT_HOME_TRAVELING_SOCIAL:   return "NOT_AT_HOME_TRAVELING_SOCIAL";
	    case NOT_AT_HOME_TRAVELING_WORK:     return "NOT_AT_HOME_TRAVELING_WORK";
	    case NOT_AT_HOME_WORK:               return "NOT_AT_HOME_WORK";
	    default:
	}
	return "LIMBO";		
	}
	
	
	public ContextType getContext(User u) {
		ContextType context = null;
		ContextCriteriaFactory fact = new ContextCriteriaFactory();
		if(fact.getCriteria(ContextType.AT_HOME).meetsCriteria(u)) {
			context = ContextType.AT_HOME;
			logger.info(u.getName()+" is "+getContextAsString(context));
			if(fact.getCriteria(ContextType.AT_HOME_SOCIAL).meetsCriteria(u)) {
				context = ContextType.AT_HOME_SOCIAL;
				logger.info(u.getName()+" is "+getContextAsString(context));
			}
			else if(fact.getCriteria(ContextType.AT_HOME_SLEEP).meetsCriteria(u)) {
				context = ContextType.AT_HOME_SLEEP;
				logger.info(u.getName()+" is "+getContextAsString(context));
			}
			else if(fact.getCriteria(ContextType.AT_HOME_WORK).meetsCriteria(u)) {
				context = ContextType.AT_HOME_WORK;
				logger.info(u.getName()+" is "+getContextAsString(context));
			}
			else if(fact.getCriteria(ContextType.AT_HOME_SCHOOL).meetsCriteria(u)) {
				context = ContextType.AT_HOME_SCHOOL;
				logger.info(u.getName()+" is "+getContextAsString(context));
			}
		}
		else if(fact.getCriteria(ContextType.NOT_AT_HOME).meetsCriteria(u)) {
			context = ContextType.NOT_AT_HOME;
			logger.info(u.getName()+" is "+getContextAsString(context));
			if(fact.getCriteria(ContextType.NOT_AT_HOME_SOCIAL).meetsCriteria(u)) {
				context = ContextType.NOT_AT_HOME_SOCIAL;
				logger.info(u.getName()+" is "+getContextAsString(context));
			}
			else if(fact.getCriteria(ContextType.NOT_AT_HOME_WORK).meetsCriteria(u)) {
				context = ContextType.NOT_AT_HOME_WORK;
				logger.info(u.getName()+" is "+getContextAsString(context));
			}
			else if(fact.getCriteria(ContextType.NOT_AT_HOME_SCHOOL).meetsCriteria(u)) {
				context = ContextType.NOT_AT_HOME_SCHOOL;
				logger.info(u.getName()+" is "+getContextAsString(context));
			}
			else if(fact.getCriteria(ContextType.NOT_AT_HOME_SHOPPING).meetsCriteria(u)) {
				context = ContextType.NOT_AT_HOME_SHOPPING;
				logger.info(u.getName()+" is "+getContextAsString(context));
			}
			else if(fact.getCriteria(ContextType.NOT_AT_HOME_TRAVELING).meetsCriteria(u)) {
				context = ContextType.NOT_AT_HOME_TRAVELING;
				logger.info(u.getName()+" is "+getContextAsString(context));
				if(fact.getCriteria(ContextType.NOT_AT_HOME_TRAVELING_HOME).meetsCriteria(u)) {
					context = ContextType.NOT_AT_HOME_TRAVELING_HOME;
					logger.info(u.getName()+" is "+getContextAsString(context));
				}
				else if(fact.getCriteria(ContextType.NOT_AT_HOME_TRAVELING_WORK).meetsCriteria(u)) {
					context = ContextType.NOT_AT_HOME_TRAVELING_HOME;
					logger.info(u.getName()+" is "+getContextAsString(context));
				}
				else if(fact.getCriteria(ContextType.NOT_AT_HOME_TRAVELING_SCHOOL).meetsCriteria(u)) {
					context = ContextType.NOT_AT_HOME_TRAVELING_HOME;
					logger.info(u.getName()+" is "+getContextAsString(context));
				}
				else if(fact.getCriteria(ContextType.NOT_AT_HOME_TRAVELING_SOCIAL).meetsCriteria(u)) {
					context = ContextType.NOT_AT_HOME_TRAVELING_HOME;
					logger.info(u.getName()+" is "+getContextAsString(context));
				}
				else if(fact.getCriteria(ContextType.NOT_AT_HOME_TRAVELING_SHOPPING).meetsCriteria(u)) {
					context = ContextType.NOT_AT_HOME_TRAVELING_HOME;
					logger.info(u.getName()+" is "+getContextAsString(context));
				}
			}
		}
        return context;
	}
	public ContextType getContext1(User u) {
		ContextType context = null;
		ContextCriteriaFactory fact = new ContextCriteriaFactory();
		if(fact.getCriteria(ContextType.AT_HOME).meetsCriteria(u)) {
			context = ContextType.AT_HOME;
			logger.info(u.getName()+" matched criteria ContextType.AT_HOME;");
			if(fact.getCriteria(ContextType.AT_HOME_SOCIAL).meetsCriteria(u)) {
				context = ContextType.AT_HOME_SOCIAL;
				logger.info(u.getName()+" matched criteria ContextType.AT_HOME_SOCIAL;");
			}
			else if(fact.getCriteria(ContextType.AT_HOME_SLEEP).meetsCriteria(u)) {
				context = ContextType.AT_HOME_SLEEP;
				logger.info(u.getName()+" matched criteria ContextType.AT_HOME_SLEEP;");
			}
			else if(fact.getCriteria(ContextType.AT_HOME_WORK).meetsCriteria(u)) {
				context = ContextType.AT_HOME_WORK;
				logger.info(u.getName()+" matched criteria ContextType.AT_HOME_WORK;");
			}
			else if(fact.getCriteria(ContextType.AT_HOME_SCHOOL).meetsCriteria(u)) {
				context = ContextType.AT_HOME_SCHOOL;
				logger.info(u.getName()+" matched criteria ContextType.AT_HOME_SCHOOL;");
			}
		}
		else if(fact.getCriteria(ContextType.NOT_AT_HOME).meetsCriteria(u)) {
			context = ContextType.NOT_AT_HOME;
			logger.info(u.getName()+" matched criteria ContextType.NOT_AT_HOME;");
			if(fact.getCriteria(ContextType.NOT_AT_HOME_SOCIAL).meetsCriteria(u)) {
				context = ContextType.NOT_AT_HOME_SOCIAL;
			}
			else if(fact.getCriteria(ContextType.NOT_AT_HOME_WORK).meetsCriteria(u)) {
				logger.info(u.getName()+" matched criteria ContextType.NOT_AT_HOME_WORK;");
				context = ContextType.NOT_AT_HOME_WORK;
			}
			else if(fact.getCriteria(ContextType.NOT_AT_HOME_SCHOOL).meetsCriteria(u)) {
				context = ContextType.NOT_AT_HOME_SCHOOL;
			}
			else if(fact.getCriteria(ContextType.NOT_AT_HOME_SHOPPING).meetsCriteria(u)) {
				context = ContextType.NOT_AT_HOME_SHOPPING;
			}
			else if(fact.getCriteria(ContextType.NOT_AT_HOME_TRAVELING).meetsCriteria(u)) {
				logger.info(u.getName()+" matched criteria ContextType.NOT_AT_HOME_TRAVELING;");
				context = ContextType.NOT_AT_HOME_TRAVELING;
				
				if(fact.getCriteria(ContextType.NOT_AT_HOME_TRAVELING_HOME).meetsCriteria(u)) {
					logger.info(u.getName()+" matched criteria ContextType.NOT_AT_HOME_TRAVELING_HOME;");
					context = ContextType.NOT_AT_HOME_TRAVELING_HOME;
				}
			}
		}
		logger.info("returning "+context);
        return context;
	}
}
