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
package org.n52.epos.pattern;

import org.n52.epos.event.EposEvent;
import org.n52.epos.filter.PassiveFilter;
import org.n52.epos.rules.Rule;

public interface PatternEngine {

	/**
	 * Inserts an {@link EposEvent} into the pattern
	 * matching mechanism. It shall pass out when the {@link PassiveFilter}
	 * of a {@link Rule} matches. The underlying
	 * pattern matching backend must ensure that
	 * {@link RuleInstance#filter(EposEvent)} is called in that case.
	 * 
	 * @param event the event object
	 */
	public void insertEvent(EposEvent event);
	
	/**
	 * Add a {@link Rule} into the engine which shall
	 * be evaluated on every call of {@link #insertEvent(EposEvent)}.
	 * 
	 * @param rule the rule
	 * @throws NoPassiveFilterPresentException if the rule does not contain
	 * an instance of {@link PassiveFilter} (as an engine only supports that
	 * kind if filters)
	 */
	public void registerRule(Rule rule) throws NoPassiveFilterPresentException;


	/**
	 * An implementation shall remove the given rule.
	 * It shall not be taken into consideration after this
	 * method call.
	 * 
	 * @param rule the rule to be removed
	 */
	public void removeRule(Rule rule);
	
	/**
	 * an implementation shall free all resources to avoid memory leaks.
	 */
	public void shutdown();

}
