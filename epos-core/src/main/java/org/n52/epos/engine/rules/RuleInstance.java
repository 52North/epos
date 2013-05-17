/**
 * Copyright (C) 2013
 * by 52 North Initiative for Geospatial Open Source Software GmbH
 *
 * Contact: Andreas Wytzisk
 * 52 North Initiative for Geospatial Open Source Software GmbH
 * Martin-Luther-King-Weg 24
 * 48155 Muenster, Germany
 * info@52north.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.n52.epos.engine.rules;

import java.util.ArrayList;
import java.util.List;

import org.n52.epos.event.EposEvent;
import org.n52.epos.filter.ActiveFilter;
import org.n52.epos.filter.EposFilter;
import org.n52.epos.filter.PassiveFilter;
import org.n52.epos.pattern.PatternEngine;
import org.n52.epos.rules.PassiveFilterAlreadyPresentException;
import org.n52.epos.rules.Rule;
import org.n52.epos.rules.RuleListener;

public class RuleInstance implements Rule {

	
	private List<EposFilter> filters = new ArrayList<EposFilter>();
	private RuleListener listener;

	public RuleInstance(RuleListener listener) {
		this.listener = listener;
	}
	
	@Override
	public void addActiveFilter(ActiveFilter f) {
		this.filters.add(f);
	}
	
	@Override
	public void setPassiveFilter(PassiveFilter f) throws PassiveFilterAlreadyPresentException {
		if (hasPassiveFilter()) {
			throw new PassiveFilterAlreadyPresentException(
					"Only one PassiveFilter per RuleInstance instance allowed.");
		}
		
		this.filters.add(f);
	}
	
	@Override
	public boolean hasPassiveFilter() {
		return findPassiveFilter() != null;
	}

	private PassiveFilter findPassiveFilter() {
		for (EposFilter ef : this.filters) {
			if (ef instanceof PassiveFilter)
				return (PassiveFilter) ef;
		}
		return null;
	}
	
	private void onAllFiltersMatch(EposEvent event) {
		this.listener.onMatchingEvent(event);
	}

	/**
	 * evaluate all registered {@link ActiveFilter}
	 * instances against event. If all pass, {@link #onAllFiltersMatch(EposEvent)}
	 * is called.
	 * {@link PassiveFilter} instances are not taken into consideration,
	 * as they match only from inside a {@link PatternEngine}.
	 * 
	 * @param event the event to filter
	 */
	@Override
	public void filter(EposEvent event) {
		for (EposFilter filter : this.filters) {
			if (filter instanceof ActiveFilter) {
				if (!((ActiveFilter) filter).matches(event)) {
					return;
				}
			}
		}
		
		onAllFiltersMatch(event);
	}

	@Override
	public PassiveFilter getPassiveFilter() {
		return findPassiveFilter();
	}

	@Override
	public RuleListener getRuleListener() {
		return this.listener;
	}
	
}
