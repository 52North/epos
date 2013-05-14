package org.n52.epos.filter;

import org.n52.epos.event.EposEvent;

public interface PassiveFilter extends EposFilter  {

	public void onTriggeredMatch(EposEvent event);

}
