package org.openhab.io.context.interpretation;

import org.openhab.io.context.data_access.GeoDAO;
import org.openhab.io.context.data_access.SQLDAO;
import org.openhab.io.context.primitives.User;

public abstract class Criteria {
	protected SQLDAO sqldao;
	protected GeoDAO geodao;
	
	public Criteria() {
		sqldao = SQLDAO.getInstance();
		geodao = GeoDAO.getInstance();
	}
    public abstract boolean meetsCriteria(User u);
}
