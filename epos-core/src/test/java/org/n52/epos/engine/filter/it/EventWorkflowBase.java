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
import java.util.ArrayList;
import java.util.List;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.epos.engine.EposEngine;
import org.n52.epos.engine.rules.RuleInstance;
import org.n52.epos.event.EposEvent;
import org.n52.epos.rules.Rule;
import org.n52.epos.rules.RuleListener;
import org.n52.epos.transform.TransformationException;
import org.n52.epos.transform.TransformationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventWorkflowBase {

	private static final Logger logger = LoggerFactory
			.getLogger(EventWorkflowBase.class);

	private static final long waitTime = 5000;

	private Object mutex = new Object();
	private List<EposEvent> results = new ArrayList<EposEvent>();

	protected Rule createBasicRule() {
		return new RuleInstance(new TestRuleListener());
	}

	protected EposEvent waitForFirstResult() {
		oneTimeWait();

		EposEvent result = null;

		if (results.size() > 0) {
			result = results.get(0);
		}

		results.clear();

		return result;
	}

	protected List<EposEvent> waitForResult() {
		oneTimeWait();

		List<EposEvent> resultsCopy = new ArrayList<EposEvent>(results);
		results.clear();

		return resultsCopy;
	}

	private void oneTimeWait() {
		synchronized (mutex) {
			if (results.size() == 0) {
				try {
					mutex.wait(waitTime);
				} catch (InterruptedException e) {
					logger.warn(e.getMessage(), e);
				}
			}
		}
	}

	protected List<EposEvent> pushEvents(String... fileNames)
			throws TransformationException, XmlException, IOException {
		List<EposEvent> result = new ArrayList<EposEvent>();

		for (String fn : fileNames) {
			result.add((EposEvent) TransformationRepository.Instance.transform(
					readXmlContent(fn), EposEvent.class));
			EposEngine.getInstance().filterEvent(result.get(result.size()-1));
		}

		return result;
	}

	protected XmlObject readXmlContent(String resource) throws XmlException,
			IOException {
		return XmlObject.Factory
				.parse(getClass().getResourceAsStream(resource));
	}

	private class TestRuleListener implements RuleListener {

		@Override
		public void onMatchingEvent(EposEvent event) {
			onMatchingEvent(event, null);
		}

		@Override
		public void onMatchingEvent(EposEvent event,
				Object desiredOutputToConsumer) {
			logger.info("Desired Output: {}", desiredOutputToConsumer);

			synchronized (mutex) {
				results.add(event);
				mutex.notifyAll();
			}
		}

		@Override
		public Object getEndpointReference() {
			return null;
		}

	}
}
