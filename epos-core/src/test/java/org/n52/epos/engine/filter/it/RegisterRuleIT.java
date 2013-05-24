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
package org.n52.epos.engine.filter.it;


import javax.xml.xpath.XPathExpressionException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.n52.epos.engine.EposEngine;
import org.n52.epos.engine.rules.RuleInstance;
import org.n52.epos.filter.ActiveFilter;
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
	public void registerRule() throws XPathExpressionException {
		EposEngine engine = EposEngine.getInstance();
		Rule rule = createRule();
		engine.registerRule(rule);
		engine.unregisterRule(rule);
	}

	private Rule createRule() throws XPathExpressionException {
		ActiveFilter xpath = FilterFactory.createXPathFilter();
		RuleInstance result = new RuleInstance(listener);
		result.addActiveFilter(xpath);
		return result;
	}
	

	
}
