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
