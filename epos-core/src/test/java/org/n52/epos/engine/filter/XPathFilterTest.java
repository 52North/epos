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
package org.n52.epos.engine.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.xpath.XPathExpressionException;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.n52.epos.event.EposEvent;
import org.n52.epos.filter.ActiveFilter;
import org.n52.epos.filter.EposFilter;
import org.n52.epos.filter.FilterInstantiationException;
import org.n52.epos.filter.FilterInstantiationRepository;
import org.mockito.Mockito;

import static org.hamcrest.CoreMatchers.*;

public class XPathFilterTest {

	@Mock
	private EposEvent eventObject;
	
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void shouldPassXPathFilter() throws XmlException, IOException, XPathExpressionException,
				FilterInstantiationException {
		Mockito.when(eventObject.getOriginalObject()).thenReturn(readXmlObject());
		
		Map<String,String> map = new HashMap<String, String>();
		map.put("fes20", "http://www.opengis.net/fes/2.0");
		XPathConfiguration conf = new XPathConfiguration("//fes20:Literal", map);
		EposFilter filter = FilterInstantiationRepository.Instance.instantiate(conf);
		
		Assert.assertThat(filter, is(instanceOf(XPathFilter.class)));
		
		XPathFilter xpf = (XPathFilter) filter;
		
		Assert.assertTrue("Filter did not match!", xpf.matches(eventObject));
	}
	
	@Test
	public void shouldNotPassXPathFilter() throws XmlException, IOException, XPathExpressionException {
		Mockito.when(eventObject.getOriginalObject()).thenReturn(readXmlObject());
		
		Map<String,String> map = new HashMap<String, String>();
		map.put("fes20", "http://www.opengis.net/fes/2.0");
		ActiveFilter filter = new XPathFilter("//fes20:Literals", map);
		
		Assert.assertTrue("Filter did match but should not have!", !filter.matches(eventObject));
	}

	private XmlObject readXmlObject() throws XmlException, IOException {
		return XmlObject.Factory.parse(getClass().getResource("xpathTestDocument.xml"));
	}
	
	
}
