package org.openhab.io.context.interpretation;

import org.openhab.io.context.ContextService;
import org.openhab.io.context.data_access.GeoDAO;
import org.openhab.io.context.data_access.SQLDAO;
import org.openhab.io.context.primitives.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Criteria {
	protected SQLDAO sqldao;
	protected GeoDAO geodao;
	protected static final Logger logger = LoggerFactory.getLogger(ContextService.class);
	public Criteria() {
		sqldao = SQLDAO.getInstance();
		geodao = GeoDAO.getInstance();
	}
    public abstract boolean meetsCriteria(User u);
}
