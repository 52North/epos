package org.n52.epos.engine.rules;

import java.util.List;

import org.n52.epos.event.EposEvent;
import org.n52.epos.filter.EposFilter;
import org.n52.epos.filter.PassiveFilter;
import org.n52.epos.rules.RuleListener;

public class RuleInstance {

	
	private List<EposFilter> filters;
	private RuleListener listener;

	public RuleInstance(List<EposFilter> filters, RuleListener listener) {
		this.filters = filters;
		this.listener = listener;
	}
	
	public boolean actsAsynchronous() {
		PassiveFilter pf = findPassiveFilter();
		return pf != null;
	}

	private PassiveFilter findPassiveFilter() {
		for (EposFilter ef : this.filters) {
			if (ef instanceof PassiveFilter)
				return (PassiveFilter) ef;
		}
		return null;
	}
	
	public void onAllFiltersMatch(EposEvent event) {
		this.listener.onMatchingEvent(event);
	}
	
}
