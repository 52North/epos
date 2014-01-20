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
 * This program is free software; you can redistribute and/or modify it under
 * the terms of the GNU General Public License version 2 as published by the
 * Free Software Foundation.
 *
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied
 * WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program (see gnu-gpl v2.txt). If not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
 * visit the Free Software Foundation web page, http://www.fsf.org.
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
