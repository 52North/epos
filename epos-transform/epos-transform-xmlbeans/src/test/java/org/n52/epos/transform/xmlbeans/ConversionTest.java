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
