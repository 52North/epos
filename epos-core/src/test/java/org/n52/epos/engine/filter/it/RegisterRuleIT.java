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
package org.n52.epos.engine.filter.it;


import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.n52.epos.engine.EposEngine;
import org.n52.epos.engine.rules.RuleInstance;
import org.n52.epos.filter.ActiveFilter;
import org.n52.epos.filter.FilterInstantiationException;
import org.n52.epos.rules.Rule;
import org.n52.epos.rules.RuleListener;

public class RegisterRuleIT {

	@Mock
	private RuleListener listener;
	
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void registerRule() throws FilterInstantiationException {
		EposEngine engine = EposEngine.getInstance();
		Rule rule = createRule();
		engine.registerRule(rule);
		engine.unregisterRule(rule);
	}

	private Rule createRule() throws FilterInstantiationException {
		ActiveFilter xpath = FilterFactory.createXPathFilter();
		RuleInstance result = new RuleInstance(listener);
		result.addActiveFilter(xpath);
		return result;
	}
	

	
}
