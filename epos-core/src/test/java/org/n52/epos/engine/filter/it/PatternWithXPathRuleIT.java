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

import java.io.IOException;
import java.util.List;

import javax.xml.xpath.XPathExpressionException;

import org.apache.xmlbeans.XmlException;
import org.junit.Assert;
import org.junit.Test;
import org.n52.epos.engine.EposEngine;
import org.n52.epos.engine.filter.XPathFilter;
import org.n52.epos.event.EposEvent;
import org.n52.epos.filter.FilterInstantiationException;
import org.n52.epos.filter.FilterInstantiationRepository;
import org.n52.epos.filter.PassiveFilter;
import org.n52.epos.rules.PassiveFilterAlreadyPresentException;
import org.n52.epos.rules.Rule;
import org.n52.epos.transform.TransformationException;

import static org.hamcrest.CoreMatchers.equalTo;

public class PatternWithXPathRuleIT extends EventWorkflowBase {

	@Test
	public void testPatternWithXPathWorkflow()
			throws PassiveFilterAlreadyPresentException,
			FilterInstantiationException, XmlException, IOException,
			XPathExpressionException, TransformationException {
		Rule rule = createRule("om:OM_Observation/om:result[@uom='test_unit_7_4']",
				"Overshoot_Rule1.xml");
		EposEngine.getInstance().registerRule(rule);

		List<EposEvent> inputs = pushEvents("Overshoot_Notify1.xml",
				"Overshoot_Notify2.xml");

		List<EposEvent> outputs = waitForResult();

		EposEngine.getInstance().unregisterRule(rule);
		
		Assert.assertTrue("Result not available!",
				outputs != null && !outputs.isEmpty());
		
		Assert.assertThat(outputs.get(0).getOriginalObject(), equalTo(inputs.get(1).getOriginalObject()));
	}
	
	@Test
	public void failPatternWithXPathWorkflow()
			throws PassiveFilterAlreadyPresentException,
			FilterInstantiationException, XmlException, IOException,
			XPathExpressionException, TransformationException {
		Rule rule = createRule("om:OM_Observation/om:result[@uom='test_unit_7_4']",
				"Overshoot_Rule2.xml");
		EposEngine.getInstance().registerRule(rule);

		pushEvents("Overshoot_Notify1.xml",
				"Overshoot_Notify2.xml");

		List<EposEvent> outputs = waitForResult();
		
		EposEngine.getInstance().unregisterRule(rule);

		Assert.assertTrue("Result should be empty!",
				outputs == null || outputs.isEmpty());
	}

	private Rule createRule(String xpathString, String passivePatternFileName)
			throws PassiveFilterAlreadyPresentException,
			FilterInstantiationException, XmlException, IOException,
			XPathExpressionException {
		Rule rule = createBasicRule();

		rule.setPassiveFilter((PassiveFilter) FilterInstantiationRepository.Instance
				.instantiate(readXmlContent(passivePatternFileName)));
		XPathFilter xpath = FilterFactory.createXPathFilter();
		xpath.setExpression(xpathString);
		rule.addActiveFilter(xpath);

		return rule;
	}

}
