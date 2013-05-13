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
package org.n52.epos.transform.xmlbeans;


import org.apache.xmlbeans.XmlObject;
import org.junit.Assert;
import org.n52.epos.event.EposEvent;
import org.n52.epos.transform.TransformationRepsitory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ConversionTest {
	
	private static final Class<?> TARGET_CLASS = EposEvent.class;
	private static final String OM_DOCUMENT = "om20observation.xml";
	private static final Logger logger = LoggerFactory.getLogger(ConversionTest.class);

	@org.junit.Test
	public void testConversion() throws Exception {
		Class<?> processorInputClass = resolveProcesserInputClass();
		Object result = TransformationRepsitory.Instance.transform(XmlObject.Factory.parse(
				getClass().getResourceAsStream(OM_DOCUMENT)), processorInputClass);
		process(result);
	}

	private static void process(Object cast) {
		try {
			TARGET_CLASS.cast(cast);
		} catch (Exception e) {
			Assert.fail("Result could not be casted to EposEvent!");
		}
		
		logger.info(cast.toString());
	}

	private static Class<?> resolveProcesserInputClass() {
		// TODO dynamic resolution of class (e.g. XmlObject for SES-facade,
		// MapEvent for built-in, ...)
		return TARGET_CLASS;
	}

}
