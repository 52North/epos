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

import javax.xml.xpath.XPathExpressionException;

import org.apache.xmlbeans.XmlException;
import org.junit.Assert;
import org.junit.Test;
import org.n52.epos.engine.EposEngine;
import org.n52.epos.engine.rules.RuleInstance;
import org.n52.epos.event.EposEvent;
import org.n52.epos.rules.Rule;
import org.n52.epos.rules.RuleListener;
import org.n52.epos.transform.TransformationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XPathFilterIT {
	
	private static final Logger logger = LoggerFactory.getLogger(XPathFilterIT.class);
	private Object mutex = new Object();
	private EposEvent result;

	@Test
	public void eventShouldMatchXPathFilter()
			throws XPathExpressionException, XmlException,
			IOException, TransformationException, InterruptedException {
		Rule rule = createRule();
		EposEngine.getInstance().registerRule(rule);
		EposEvent inputEvent = EventFactory.createOMEvent();
		EposEngine.getInstance().filterEvent(inputEvent);
		
		synchronized (this.mutex) {
			if (this.result == null) {
				this.mutex.wait(5000);
			}
		}
		
		Assert.assertNotNull("Did not receive expected Event back!", this.result);
		logger.info("Received Event back: {}", this.result);
		Assert.assertTrue("Received event is not the original one!", this.result == inputEvent);
	}

	private Rule createRule() throws XPathExpressionException {
		Rule result = new RuleInstance(new TestRuleListener());
		result.addActiveFilter(FilterFactory.createXPathFilter());
		return result;
	}
	
	private class TestRuleListener implements RuleListener  {

		@Override
		public void onMatchingEvent(EposEvent event) {
			synchronized (mutex) {
				result = event;
			}
		}
		
	}
	
}
