/**
 * Copyright (C) 2012
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
import org.junit.Assert;
import org.junit.Test;
import org.n52.epos.engine.EposEngine;
import org.n52.epos.engine.rules.RuleInstance;
import org.n52.epos.event.EposEvent;
import org.n52.epos.filter.FilterInstantiationException;
import org.n52.epos.filter.FilterInstantiationRepository;
import org.n52.epos.filter.PassiveFilter;
import org.n52.epos.rules.PassiveFilterAlreadyPresentException;
import org.n52.epos.rules.Rule;
import org.n52.epos.rules.RuleListener;
import org.n52.epos.transform.TransformationException;
import org.n52.epos.transform.TransformationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OvershootUndershootRuleIT {

	private static final Logger logger = LoggerFactory.getLogger(OvershootUndershootRuleIT.class);
	protected Object mutex = new Object();
	protected EposEvent result;


	@Test
	public void shouldCompleteRoundtripForNotification() throws XmlException, IOException,
			InterruptedException, PassiveFilterAlreadyPresentException, FilterInstantiationException, TransformationException {
		RuleListener notificationReceiver = initializeConsumer();
		
		subscribe(notificationReceiver);
		
		Thread.sleep(1000);
		
		notification();
		
		synchronized (mutex) {
			if (result == null) {
				mutex.wait(5000);
			}	
		}

		Assert.assertNotNull("Did not receive result back!", result);
		logger.info("Received event back: {}", result);
	}



	private void notification() throws XmlException, TransformationException, IOException {
		List<XmlObject> notis = readNotifications();
		for (XmlObject xmlObject : notis) {
			EposEngine.getInstance().filterEvent((EposEvent) TransformationRepository.Instance.transform(
					xmlObject, EposEvent.class));
		}
	}



	private RuleListener initializeConsumer() throws IOException, InterruptedException {
		return new RuleListener() {
			
			@Override
			public void onMatchingEvent(EposEvent event) {
				synchronized (mutex) {
					result = event;
					mutex.notifyAll();
				}
			}
		};
	}

	private Rule subscribe(RuleListener notificationReceiver) throws PassiveFilterAlreadyPresentException, FilterInstantiationException, XmlException, IOException {
		RuleInstance rule = new RuleInstance(notificationReceiver);
		rule.setPassiveFilter((PassiveFilter)
				FilterInstantiationRepository.Instance.instantiate(readSubscription()));
		EposEngine.getInstance().registerRule(rule);
		return rule;
	}


	private List<XmlObject> readNotifications() throws XmlException, IOException {
		List<XmlObject> result = new ArrayList<XmlObject>();
		result.add(readXmlContent("Overshoot_Notify1.xml"));
		result.add(readXmlContent("Overshoot_Notify2.xml"));
		return result;
	}

	private XmlObject readXmlContent(String resource) throws XmlException, IOException {
		return XmlObject.Factory.parse(getClass().getResourceAsStream(resource));
	}



	public XmlObject readSubscription() throws XmlException, IOException {
		return readXmlContent("Overshoot_Subscribe1.xml");
	}


}
