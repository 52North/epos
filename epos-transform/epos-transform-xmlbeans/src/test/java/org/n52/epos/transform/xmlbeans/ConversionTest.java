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
package org.n52.epos.transform.xmlbeans;


import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.xmlbeans.XmlObject;
import org.junit.Assert;
import org.n52.epos.event.EposEvent;
import org.n52.epos.transform.TransformationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class ConversionTest {
	
	private static final Class<?> TARGET_CLASS = EposEvent.class;
	private static final String OM_DOCUMENT = "om20observation.xml";
	private static final Logger logger = LoggerFactory.getLogger(ConversionTest.class);

	@org.junit.Test
	public void testConversion() throws Exception {
		Class<?> processorInputClass = resolveProcesserInputClass();
		Object result = TransformationRepository.Instance.transform(XmlObject.Factory.parse(
				getClass().getResourceAsStream(OM_DOCUMENT)), processorInputClass);
		process(result);
	}
	
	@org.junit.Test
	public void testConversionOfElement() throws Exception {
		Class<?> processorInputClass = resolveProcesserInputClass();
		Object result = TransformationRepository.Instance.transform(readElement(), processorInputClass);
		process(result);
	}

	private Element readElement() throws SAXException, IOException, ParserConfigurationException {
		DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
		fac.setNamespaceAware(true);
		return fac.newDocumentBuilder().parse(getClass().getResourceAsStream(OM_DOCUMENT)).getDocumentElement();
	}

	private static void process(Object cast) {
		try {
			TARGET_CLASS.cast(cast);
		} catch (Exception e) {
			Assert.fail("Result could not be casted to "+TARGET_CLASS.getCanonicalName());
		}
		
		logger.info(cast.toString());
	}

	private static Class<?> resolveProcesserInputClass() {
		// TODO dynamic resolution of class (e.g. XmlObject for SES-facade,
		// MapEvent for built-in, ...)
		return TARGET_CLASS;
	}

}
