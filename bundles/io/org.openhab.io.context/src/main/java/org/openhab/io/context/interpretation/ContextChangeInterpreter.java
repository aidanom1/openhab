package org.openhab.io.context.interpretation;

import org.openhab.io.context.primitives.User;

public class ContextChangeInterpreter {

	public String getContext(User u) {
		String context = "NO_UPDATE";
		ContextCriteriaFactory fact = new ContextCriteriaFactory();
		if(fact.getCriteria("AT_HOME").meetsCriteria(u)) {
			context = "AT_HOME";
			if(fact.getCriteria("AT_HOME_SOCIAL").meetsCriteria(u)) {
				context = "AT_HOME_SOCIAL";
			}
			else if(fact.getCriteria("AT_HOME_SLEEP").meetsCriteria(u)) {
				context = "AT_HOME_SLEEP";
			}
			else if(fact.getCriteria("AT_HOME_WORK").meetsCriteria(u)) {
				context = "AT_HOME_WORK";
			}
			else if(fact.getCriteria("AT_HOME_SCHOOL").meetsCriteria(u)) {
				context = "AT_HOME_SCHOOL";
			}
		}
		else if(fact.getCriteria("NOT_AT_HOME").meetsCriteria(u)) {
			// User is NOT_AT_HOME
			context = "NOT_AT_HOME";
			if(fact.getCriteria("NOT_AT_HOME_TRAVELLING").meetsCriteria(u)) {
				// User is travelling
				context = "NOT_AT_HOME_TRAVELLING";
				if(fact.getCriteria("NOT_AT_HOME_TRAVELLING_HOME").meetsCriteria(u)) {
					// User is on the way home
					context = "NOT_AT_HOME_TRAVELLING_HOME";
				}
			}
			else if(fact.getCriteria("NOT_AT_HOME_SOCIAL").meetsCriteria(u)) {
				context = "NOT_AT_HOME_SOCIAL";
			}
			else if(fact.getCriteria("NOT_AT_HOME_WORK").meetsCriteria(u)) {
				context = "NOT_AT_HOME_WORK";
			}
			else if(fact.getCriteria("NOT_AT_HOME_SCHOOL").meetsCriteria(u)) {
				context = "NOT_AT_HOME_SCHOOL";
			}
			else if(fact.getCriteria("NOT_AT_HOME_SHOPPING").meetsCriteria(u)) {
				context = "NOT_AT_HOME_SHOPPING";
			}
		}
        return context;
	}
}
