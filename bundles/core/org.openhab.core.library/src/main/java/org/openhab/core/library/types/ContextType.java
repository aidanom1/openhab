package org.openhab.core.library.types;

import org.openhab.core.types.Command;
import org.openhab.core.types.State;
import org.openhab.core.types.PrimitiveType;

public enum ContextType implements PrimitiveType, State, Command {
	AT_HOME,
	AT_HOME_SOCIAL,
	AT_HOME_SLEEP,
	AT_HOME_WORK,
	AT_HOME_SCHOOL,
	NOT_AT_HOME,
	NOT_AT_HOME_TRAVELING,
	NOT_AT_HOME_TRAVELING_HOME,
	NOT_AT_HOME_TRAVELING_SOCIAL,
	NOT_AT_HOME_TRAVELING_WORK,
	NOT_AT_HOME_TRAVELING_SCHOOL,
	NOT_AT_HOME_TRAVELING_SHOPPING,
	NOT_AT_HOME_TRAVELING_HOLIDAYS,
	NOT_AT_HOME_SOCIAL,
	NOT_AT_HOME_SOCIAL_CINEMA,
	NOT_AT_HOME_SOCIAL_DINING,
	NOT_AT_HOME_WORK,
	NOT_AT_HOME_SCHOOL,
	NOT_AT_HOME_SHOPPING,
	NOT_AT_HOME_HOLIDAYS;
		
	public String format(String pattern) {
		return String.format(pattern, this.toString());
	}

}
