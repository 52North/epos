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

import org.apache.xmlbeans.XmlException;
import org.junit.Assert;
import org.junit.Test;
import org.n52.epos.engine.EposEngine;
import org.n52.epos.event.EposEvent;
import org.n52.epos.filter.FilterInstantiationException;
import org.n52.epos.filter.FilterInstantiationRepository;
import org.n52.epos.filter.PassiveFilter;
import org.n52.epos.rules.PassiveFilterAlreadyPresentException;
import org.n52.epos.rules.Rule;
import org.n52.epos.transform.TransformationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OvershootUndershootRuleIT extends EventWorkflowBase {

	private static final Logger logger = LoggerFactory.getLogger(OvershootUndershootRuleIT.class);

	@Test
	public void shouldCompleteRoundtripForNotification() throws XmlException, IOException,
			InterruptedException, PassiveFilterAlreadyPresentException, FilterInstantiationException, TransformationException {
		executeRoundtrip("Overshoot_Rule1.xml");
	}

	@Test
	public void shouldCompleteRoundtripForNotificationWithUOM() throws XmlException, IOException,
			InterruptedException, PassiveFilterAlreadyPresentException, FilterInstantiationException, TransformationException {
		executeRoundtrip("Overshoot_Rule2.xml");
	}
	
	private void removeRule(Rule rule) {
		EposEngine.getInstance().unregisterRule(rule);
	}
	
	private void executeRoundtrip(String passiveFilterFileName) throws PassiveFilterAlreadyPresentException,
				FilterInstantiationException, XmlException, IOException, TransformationException {
		Rule rule = registerRule(passiveFilterFileName);
		
		pushEvents("Overshoot_Notify1.xml", "Overshoot_Notify2.xml");
		
		EposEvent result = waitForFirstResult();
		
		Assert.assertNotNull("Did not receive result back!", result);
		logger.info("Received event back: {}", result);
		
		removeRule(rule);
		
		result = null;
		pushEvents("Overshoot_Notify1.xml", "Overshoot_Notify2.xml");
		
		result = waitForFirstResult();
		
		Assert.assertNull("Received a result. But the Rule should have been removed!", result);
	}

	private Rule registerRule(String file) throws PassiveFilterAlreadyPresentException, FilterInstantiationException, XmlException, IOException {
		Rule rule = createBasicRule();
		rule.setPassiveFilter((PassiveFilter)
				FilterInstantiationRepository.Instance.instantiate(readXmlContent(file)));
		EposEngine.getInstance().registerRule(rule);
		return rule;
	}

}
