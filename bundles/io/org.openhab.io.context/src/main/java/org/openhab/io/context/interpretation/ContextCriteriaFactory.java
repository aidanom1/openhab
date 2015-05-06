package org.openhab.io.context.interpretation;

import org.openhab.core.library.types.ContextType;
import org.openhab.io.context.interpretation.CriteriaLibrary.AtHomeContextCriteria;
import org.openhab.io.context.interpretation.CriteriaLibrary.AtHomeSchoolContextCriteria;
import org.openhab.io.context.interpretation.CriteriaLibrary.AtHomeSleepContextCriteria;
import org.openhab.io.context.interpretation.CriteriaLibrary.AtHomeSocialContextCriteria;
import org.openhab.io.context.interpretation.CriteriaLibrary.AtHomeWorkContextCriteria;
import org.openhab.io.context.interpretation.CriteriaLibrary.NotAtHomeContextCriteria;
import org.openhab.io.context.interpretation.CriteriaLibrary.NotAtHomeHolidaysContextCriteria;
import org.openhab.io.context.interpretation.CriteriaLibrary.NotAtHomeSchoolContextCriteria;
import org.openhab.io.context.interpretation.CriteriaLibrary.NotAtHomeShoppingContextCriteria;
import org.openhab.io.context.interpretation.CriteriaLibrary.NotAtHomeSocialCinemaContextCriteria;
import org.openhab.io.context.interpretation.CriteriaLibrary.NotAtHomeSocialContextCriteria;
import org.openhab.io.context.interpretation.CriteriaLibrary.NotAtHomeSocialDiningContextCriteria;
import org.openhab.io.context.interpretation.CriteriaLibrary.NotAtHomeTravelingContextCriteria;
import org.openhab.io.context.interpretation.CriteriaLibrary.NotAtHomeTravelingHolidaysContextCriteria;
import org.openhab.io.context.interpretation.CriteriaLibrary.NotAtHomeTravelingHomeContextCriteria;
import org.openhab.io.context.interpretation.CriteriaLibrary.NotAtHomeTravelingSchoolContextCriteria;
import org.openhab.io.context.interpretation.CriteriaLibrary.NotAtHomeTravelingShoppingContextCriteria;
import org.openhab.io.context.interpretation.CriteriaLibrary.NotAtHomeTravelingSocialContextCriteria;
import org.openhab.io.context.interpretation.CriteriaLibrary.NotAtHomeTravelingWorkContextCriteria;
import org.openhab.io.context.interpretation.CriteriaLibrary.NotAtHomeWorkContextCriteria;

public class ContextCriteriaFactory {

	public Criteria getCriteria(ContextType crit) {
		switch(crit) {
		    case AT_HOME       :                 return new AtHomeContextCriteria();                        // implemented
		    case NOT_AT_HOME   :                 return new NotAtHomeContextCriteria();                     // implemented
		    case AT_HOME_SOCIAL:                 return new AtHomeSocialContextCriteria();
		    case AT_HOME_SLEEP :                 return new AtHomeSleepContextCriteria();
		    case AT_HOME_WORK  :                 return new AtHomeWorkContextCriteria();
		    case AT_HOME_SCHOOL:                 return new AtHomeSchoolContextCriteria();
		    case NOT_AT_HOME_HOLIDAYS:           return new NotAtHomeHolidaysContextCriteria();
		    case NOT_AT_HOME_SCHOOL:             return new NotAtHomeSchoolContextCriteria();
		    case NOT_AT_HOME_SHOPPING:           return new NotAtHomeShoppingContextCriteria();
	    	case NOT_AT_HOME_SOCIAL:             return new NotAtHomeSocialContextCriteria();
		    case NOT_AT_HOME_SOCIAL_CINEMA:      return new NotAtHomeSocialCinemaContextCriteria();
		    case NOT_AT_HOME_SOCIAL_DINING:      return new NotAtHomeSocialDiningContextCriteria();
		    case NOT_AT_HOME_TRAVELING:          return new NotAtHomeTravelingContextCriteria();           // implemented
		    case NOT_AT_HOME_TRAVELING_HOLIDAYS: return new NotAtHomeTravelingHolidaysContextCriteria();
		    case NOT_AT_HOME_TRAVELING_HOME:     return new NotAtHomeTravelingHomeContextCriteria();
		    case NOT_AT_HOME_TRAVELING_SCHOOL:   return new NotAtHomeTravelingSchoolContextCriteria();
		    case NOT_AT_HOME_TRAVELING_SHOPPING: return new NotAtHomeTravelingShoppingContextCriteria();
		    case NOT_AT_HOME_TRAVELING_SOCIAL:   return new NotAtHomeTravelingSocialContextCriteria();
		    case NOT_AT_HOME_TRAVELING_WORK:     return new NotAtHomeTravelingWorkContextCriteria();
		    case NOT_AT_HOME_WORK:               return new NotAtHomeWorkContextCriteria();                // implemented
		    default:
		}
		return null;
	}
}
