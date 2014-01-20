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
