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
