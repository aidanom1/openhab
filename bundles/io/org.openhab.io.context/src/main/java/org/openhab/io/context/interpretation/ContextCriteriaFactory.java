package org.openhab.io.context.interpretation;

public class ContextCriteriaFactory {

	public Criteria getCriteria(String crit) {
		switch(crit) {
		    case "AT_HOME": return new AtHomeContextCriteria();
		    case "NOT_AT_HOME": return new NotAtHomeContextCriteria();
		    case "AT_HOME_SOCIAL": return new AtHomeSocialContextCriteria();
		    case "AT_HOME_SLEEP" : return new AtHomeSleepContextCriteria();
		    case "AT_HOME_WORK" : return new AtHomeWorkContextCriteria();
		    case "AT_HOME_SCHOOL": return new AtHomeSchoolContextCriteria();
		}
		return null;
	}
}
