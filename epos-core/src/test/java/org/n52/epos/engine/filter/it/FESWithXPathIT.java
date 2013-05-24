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

public class FESWithXPathIT extends EventWorkflowBase {

	@Test
	public void shouldReturnEvent()
			throws PassiveFilterAlreadyPresentException,
			FilterInstantiationException, XmlException, IOException,
			XPathExpressionException, TransformationException {
		Rule rule = createBasicRule();
		rule.setPassiveFilter((PassiveFilter) FilterInstantiationRepository.Instance
				.instantiate(readXmlContent("FESFilter.xml")));

		XPathFilter xpath = FilterFactory.createXPathFilter();
		rule.addActiveFilter(xpath);

		EposEngine.getInstance().registerRule(rule);

		List<EposEvent> inputs = pushEvents("FESEvent1.xml");

		EposEvent result = waitForFirstResult();

		Assert.assertTrue("Not the expected result!", result == inputs.get(0));

		EposEngine.getInstance().unregisterRule(rule);
	}
}
