package org.n52.epos.rules;

import org.n52.epos.event.EposEvent;

public interface RuleListener {

	/**
	 * This method is called whenever an {@link EposEvent}
	 * matched the Rule to which this listener is attached.
	 * 
	 * @param event the matching event
	 */
	public void onMatchingEvent(EposEvent event);
	
}
