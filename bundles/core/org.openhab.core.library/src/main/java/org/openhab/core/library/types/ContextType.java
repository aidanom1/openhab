package org.openhab.core.library.types;

import org.openhab.core.types.Command;
import org.openhab.core.types.State;
import org.openhab.core.types.PrimitiveType;

public enum ContextType implements PrimitiveType, State, Command {
	AT_HOME,NOT_AT_HOME;
		
	public String format(String pattern) {
		return String.format(pattern, this.toString());
	}

}
