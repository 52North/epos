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
package org.n52.epos.engine.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFactoryConfigurationException;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.n52.epos.event.EposEvent;
import org.n52.epos.filter.ActiveFilter;
import org.n52.epos.filter.EposFilter;
import org.n52.epos.filter.FilterInstantiationException;
import org.n52.epos.filter.FilterInstantiationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.mockito.Mockito;

import static org.hamcrest.CoreMatchers.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class XPathFilterTest {

	private static final Logger logger = LoggerFactory.getLogger(XPathFilterTest.class);
	
	@Mock
	private EposEvent eventObject;
	private XmlObject xo;
	
	@Before
	public void init() throws XmlException, IOException, FilterInstantiationException, XPathFactoryConfigurationException {
		MockitoAnnotations.initMocks(this);
		xo = XmlObject.Factory.parse(getClass().getResource("xpathTestDocument.xml"));
		Mockito.when(eventObject.getOriginalObject()).thenReturn(xo);
		
		XPathFactory factory = javax.xml.xpath.XPathFactory.newInstance();
		logger.info("Using {} for XPath matching", factory.getClass().getCanonicalName());
	}
	
	@Test
	public void shouldInstantiateXPathFilter() throws FilterInstantiationException {
		Map<String,String> map = new HashMap<String, String>();
		map.put("fes20", "http://www.opengis.net/fes/2.0");
		XPathConfiguration conf = new XPathConfiguration("//fes20:Literal", map);
		EposFilter filter = FilterInstantiationRepository.Instance.instantiate(conf);
		
		Assert.assertThat(filter, is(instanceOf(XPathFilter.class)));
	}
	
	@Test
	public void shouldPassXPathFilter() throws XmlException, IOException, XPathExpressionException,
				FilterInstantiationException {
		
		Map<String,String> map = new HashMap<String, String>();
		map.put("fes20", "http://www.opengis.net/fes/2.0");
		ActiveFilter filter = new XPathFilter("//fes20:Literal", map);
		
		for (int i = 0; i < 100; i++) {
			Assert.assertTrue("Filter did not match!", filter.matches(eventObject));	
		}
	}
	
	@Test
	public void shouldNotPassXPathFilter() throws XmlException, IOException, XPathExpressionException {
		
		Map<String,String> map = new HashMap<String, String>();
		map.put("fes20", "http://www.opengis.net/fes/2.0");
		ActiveFilter filter = new XPathFilter("//fes20:Literals", map);
		
		for (int i = 0; i < 100; i++) {
			Assert.assertTrue("Filter did match but should not have!", !filter.matches(eventObject));
		}
	}

	
}
