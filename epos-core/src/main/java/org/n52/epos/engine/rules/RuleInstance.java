/**
 * Copyright (C) 2013-2014 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 as published
 * by the Free Software Foundation.
 *
 * If the program is linked with libraries which are licensed under one of
 * the following licenses, the combination of the program with the linked
 * library is not considered a "derivative work" of the program:
 *
 *     - Apache License, version 2.0
 *     - Apache Software License, version 1.0
 *     - GNU Lesser General Public License, version 3
 *     - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *     - Common Development and Distribution License (CDDL), version 1.0
 *
 * Therefore the distribution of the program linked with libraries licensed
 * under the aforementioned licenses, is permitted by the copyright holders
 * if the distribution is compliant with both the GNU General Public
 * icense version 2 and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 */
package org.n52.epos.engine.rules;

import java.util.ArrayList;
import java.util.List;

import org.n52.epos.event.EposEvent;
import org.n52.epos.filter.ActiveFilter;
import org.n52.epos.filter.EposFilter;
import org.n52.epos.filter.PassiveFilter;
import org.n52.epos.rules.PassiveFilterAlreadyPresentException;
import org.n52.epos.rules.Rule;
import org.n52.epos.rules.RuleListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RuleInstance implements Rule {

	private static final Logger logger = LoggerFactory.getLogger(RuleInstance.class);
	
	private List<EposFilter> activeFilters = new ArrayList<EposFilter>();
	private PassiveFilter passiveFilter;
	private RuleListener listener;

	public RuleInstance(RuleListener listener) {
		this.listener = listener;
	}
	
	@Override
	public void addActiveFilter(ActiveFilter f) {
		this.activeFilters.add(f);
	}
	
	@Override
	public void setPassiveFilter(PassiveFilter f) throws PassiveFilterAlreadyPresentException {
		if (hasPassiveFilter()) {
			throw new PassiveFilterAlreadyPresentException(
					"Only one PassiveFilter per RuleInstance instance allowed.");
		}
		
		this.passiveFilter = f;
	}
	
	@Override
	public boolean hasPassiveFilter() {
		return this.passiveFilter != null;
	}
	
	private void onAllFiltersMatch(EposEvent event, Object desiredOutputToConsumer) {
		logger.debug("All activeFilters matched. Calling listener.");
		
		Object output = null;
		if (desiredOutputToConsumer == null) {
			output = event.getOriginalObject();
		}
		else {
			output = desiredOutputToConsumer;
		}
		
		this.listener.onMatchingEvent(event, output);
	}

	@Override
	public void filter(EposEvent event) {
		filter(event, null);
	}
	
	@Override
	public void filter(EposEvent event, Object desiredOutputToConsumer) {
		logger.debug("Received Event, evaluating activeFilters...");
		for (EposFilter filter : this.activeFilters) {
			if (filter instanceof ActiveFilter) {
				if (!((ActiveFilter) filter).matches(event)) {
					logger.debug("Filter {} did not match, disregarding event.", filter);
					return;
				}
			}
		}
		
		onAllFiltersMatch(event, desiredOutputToConsumer);
	}

	@Override
	public PassiveFilter getPassiveFilter() {
		return this.passiveFilter;
	}

	@Override
	public RuleListener getRuleListener() {
		return this.listener;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[RuleInstance] ActiveFilters: ");
		sb.append(this.activeFilters);
		sb.append("; PassiveFilter: ");
		sb.append(this.passiveFilter);
		return sb.toString();
	}
	
}
