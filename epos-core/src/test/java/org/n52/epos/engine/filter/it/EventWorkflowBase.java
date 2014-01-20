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
