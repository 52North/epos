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

import net.opengis.fes.x20.BinarySpatialOpType;
import net.opengis.fes.x20.FilterDocument;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.junit.Assert;
import org.junit.Test;
import org.n52.oxf.xmlbeans.tools.XmlUtil;

public class EMLHelperTest {

	@Test
	public void shouldReplacePhenomenonChars() throws Exception {
		XmlObject xo = readValueReference();
		EMLHelper.replaceForExpression(xo);
		String result = XmlUtil.stripText(xo);
		System.out.println(result);
		Assert.assertTrue("Result not as expected!", result.equals("urn__test__value_asdf/geometry"));
	}
	
	

	private XmlObject readValueReference() throws XmlException, IOException {
		FilterDocument fes = FilterDocument.Factory.parse(getClass().getResourceAsStream("fes.xml"));
		BinarySpatialOpType within = (BinarySpatialOpType) fes.getFilter().getSpatialOps();
		return within.getExpression();
	}
}
