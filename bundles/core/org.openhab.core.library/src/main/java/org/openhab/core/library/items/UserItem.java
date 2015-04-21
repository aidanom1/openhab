/**
 * Copyright (c) 2010-2015, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.core.library.items;

import java.util.ArrayList;
import java.util.List;

import org.openhab.core.items.GenericItem;
import org.openhab.core.library.types.StringType;
import org.openhab.core.library.types.ContextType;
import org.openhab.core.types.Command;
import org.openhab.core.types.State;
import org.openhab.core.types.UnDefType;


public class UserItem extends GenericItem {
	
	private static List<Class<? extends State>> acceptedDataTypes = new ArrayList<Class<? extends State>>();
	private static List<Class<? extends Command>> acceptedCommandTypes = new ArrayList<Class<? extends Command>>();

	static {
		acceptedDataTypes.add(ContextType.class);
		acceptedDataTypes.add(UnDefType.class);
		
		acceptedCommandTypes.add(ContextType.class);
	}
	
	public UserItem(String name) {
		super(name);
	}

	public void send(ContextType command) {
		internalSend(command);
	}

	public List<Class<? extends State>> getAcceptedDataTypes() {
		return acceptedDataTypes;
	}

	public List<Class<? extends Command>> getAcceptedCommandTypes() {
		return acceptedCommandTypes;
	}
	
	@Override
	public State getStateAs(Class<? extends State> typeClass) {
		if(typeClass==StringType.class) {
			return state==ContextType.AT_HOME ? new StringType("At Home") : new StringType("Not At Home");
		} else {
			return super.getStateAs(typeClass);
		}
	}
}
