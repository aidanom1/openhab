package org.openhab.io.context.interpretation;

import org.openhab.io.context.primitives.User;

public interface Criteria {
    public boolean meetsCriteria(User u);
}
