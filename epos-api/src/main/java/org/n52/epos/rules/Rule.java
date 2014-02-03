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
package org.n52.epos.rules;


import org.n52.epos.event.EposEvent;
import org.n52.epos.filter.ActiveFilter;
import org.n52.epos.filter.EposFilter;
import org.n52.epos.filter.PassiveFilter;
import org.n52.epos.pattern.PatternEngine;

public interface Rule {
	
	/**
	 * Adds another filter which shall be considered
	 * when {@link #filter(EposEvent)} is called.
	 * 
	 * @param filter an additional {@link ActiveFilter} instance
	 */
	public void addActiveFilter(ActiveFilter filter);
	
	/**
	 * Sets the {@link PassiveFilter}. A {@link PassiveFilterAlreadyPresentException}
	 * shall be thrown when a {@link PassiveFilter} has
	 * been set before.
	 * 
	 * @param filter
	 * @throws PassiveFilterAlreadyPresentException
	 */
	public void setPassiveFilter(PassiveFilter filter) throws PassiveFilterAlreadyPresentException;
	
	/**
	 * @return the registered {@link PassiveFilter} instance
	 * or null if non is registered.
	 */
	public PassiveFilter getPassiveFilter();
	
	/**
	 * @return true if a {@link PassiveFilter} is registered
	 * in an instance of the Rule.
	 */
	public boolean hasPassiveFilter();

	/**
	 * @return the listener which shall be called when
	 * all {@link EposFilter} match a certain event.
	 */
	public RuleListener getRuleListener();
	
	/**
	 * evaluate all registered {@link ActiveFilter}
	 * instances against the event. If all pass, the {@link RuleListener}
	 * attached to this Rule shall called.
	 * {@link PassiveFilter} instances shall not be taken into consideration,
	 * as they shall match only from inside a {@link PatternEngine}.
	 * 
	 * @param event the event to filter
	 */
	public void filter(EposEvent event);

	/**
	 * same as {@link #filter(EposEvent)}, but the {@link RuleListener}
	 * shall be invoked through {@link RuleListener#onMatchingEvent(EposEvent, Object)}.
	 * 
	 * @param event the event to filter
	 * @param desiredOutputToConsumer the desired output to provide
	 * to the consumer
	 */
	void filter(EposEvent event, Object desiredOutputToConsumer);
	
	
	
}
