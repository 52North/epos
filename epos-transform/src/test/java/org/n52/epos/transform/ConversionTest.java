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
package org.n52.epos.transform;

import org.n52.epos.event.EposEvent;
import org.n52.sos.event.events.ObservationInsertion;

import com.vividsolutions.jts.util.Assert;

public class ConversionTest {
	
	private static final Class<?> TARGET_CLASS = EposEvent.class;

	@org.junit.Test
	public void testConversion() throws Exception {
		ObservationInsertion ins = new ObservationInsertion(null, null);
		Class<?> processorInputClass = resolveProcesserInputClass();
		Object result = TransformationRepsitory.Instance.transform(ins, processorInputClass);
		process(result);
	}

	private static void process(Object cast) {
		try {
			TARGET_CLASS.cast(cast);
		} catch (Exception e) {
			Assert.shouldNeverReachHere("Result could not be casted to EposEvent!");
		}
	}

	private static Class<?> resolveProcesserInputClass() {
		// TODO dynamic resolution of class (e.g. XmlObject for SES-facade,
		// MapEvent for built-in, ...)
		return TARGET_CLASS;
	}

}
