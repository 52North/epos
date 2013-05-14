package org.n52.epos.filter;

import org.n52.epos.event.EposEvent;

public interface ActiveFilter extends EposFilter {

	public boolean matches(EposEvent event);
	
}
