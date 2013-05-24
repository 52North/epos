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
package org.n52.epos.pattern.eml;

import java.io.IOException;

import net.opengis.fes.x20.FilterDocument;

import org.apache.xmlbeans.XmlException;
import org.junit.Assert;
import org.junit.Test;
import org.n52.epos.filter.EposFilter;
import org.n52.epos.filter.FilterInstantiationException;
import org.n52.epos.filter.FilterInstantiationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FESFilterInstantiationTest {

	private static final Logger logger = LoggerFactory.getLogger(FESFilterInstantiationTest.class);
	
	@Test
	public void shouldInstantiationEMLPatternFilter() throws XmlException, IOException, FilterInstantiationException {
		FilterDocument eml = FilterDocument.Factory.parse(getClass().getResourceAsStream("fes.xml"));
		
		EposFilter filter = FilterInstantiationRepository.Instance.instantiate(eml);
		
		Assert.assertTrue("filter is null!", filter != null);
		Assert.assertTrue("Not a PatternFilter!", filter instanceof EMLPatternFilter);
		
		EMLPatternFilter pattern = (EMLPatternFilter) filter;
		Assert.assertTrue("Filter should create a final output!", pattern.getPatterns().get(0).createsFinalOutput());
		Assert.assertTrue("Filter should use the original event as output!", pattern.getPatterns().get(0).usesOriginalEventAsOutput());
		
		logger.info(pattern.getPatterns().get(0).createStringRepresentation());
	}
	
}
