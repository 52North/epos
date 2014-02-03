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

import javax.xml.xpath.XPathExpressionException;

import org.apache.xmlbeans.XmlException;
import org.junit.Assert;
import org.junit.Test;
import org.n52.epos.engine.EposEngine;
import org.n52.epos.engine.filter.XPathFilter;
import org.n52.epos.event.EposEvent;
import org.n52.epos.filter.FilterInstantiationException;
import org.n52.epos.rules.Rule;
import org.n52.epos.test.EventFactory;
import org.n52.epos.transform.TransformationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XPathFilterIT extends EventWorkflowBase {
	
	private static final Logger logger = LoggerFactory.getLogger(XPathFilterIT.class);

	@Test
	public void eventShouldMatchXPathFilter()
			throws FilterInstantiationException, XmlException,
			IOException, TransformationException, InterruptedException {
		Rule rule = createRule();
		EposEngine.getInstance().registerRule(rule);
		EposEvent inputEvent = EventFactory.createOMEvent();
		EposEngine.getInstance().filterEvent(inputEvent);
		
		EposEvent result = waitForFirstResult();
		
		EposEngine.getInstance().unregisterRule(rule);
		
		Assert.assertNotNull("Did not receive expected Event back!", result);
		logger.info("Received Event back: {}", result);
		Assert.assertTrue("Received event is not the original one!", result == inputEvent);
	}
	
	@Test
	public void eventShouldFailXPathFilter()
			throws FilterInstantiationException, XmlException,
			IOException, TransformationException, InterruptedException, XPathExpressionException {
		Rule rule = createBasicRule();
		XPathFilter xpath = FilterFactory.createXPathFilter();
		rule.addActiveFilter(xpath);
		xpath.setExpression("//om:observedProperty[@xlink:href='http://fail.to']");
		EposEngine.getInstance().registerRule(rule);
		EposEvent inputEvent = EventFactory.createOMEvent();
		EposEngine.getInstance().filterEvent(inputEvent);
		
		EposEvent result = waitForFirstResult();
		
		EposEngine.getInstance().unregisterRule(rule);
		
		Assert.assertNull("Expected no result!", result);
	}

	private Rule createRule() throws FilterInstantiationException {
		Rule result = createBasicRule();
		result.addActiveFilter(FilterFactory.createXPathFilter());
		return result;
	}
	
	
}
