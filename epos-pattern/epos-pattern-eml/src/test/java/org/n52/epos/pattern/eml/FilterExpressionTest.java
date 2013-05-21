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
import java.util.HashSet;

import javax.xml.namespace.QName;

import net.opengis.fes.x20.BinarySpatialOpType;
import net.opengis.fes.x20.FilterDocument;
import net.opengis.fes.x20.LiteralDocument;
import net.opengis.fes.x20.LiteralType;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.n52.ses.eml.v001.filter.IFilterElement;
import org.n52.ses.eml.v001.filter.expression.AFilterExpression;
import org.n52.ses.eml.v001.filter.expression.FilterExpressionFactory;

public class FilterExpressionTest {

	@Mock
	IFilterElement parent;
	
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void testExpressionCreation() throws XmlException, IOException {
		FilterExpressionFactory feFactory = new FilterExpressionFactory();
		
		AFilterExpression result = feFactory.buildFilterExpression(readLiteral(), new HashSet<Object>(), parent);
		Assert.assertNotNull("Result for Literal not available.", result);
		
		result = feFactory.buildFilterExpression(readValueReference(), new HashSet<Object>(), parent);
		Assert.assertNotNull("Result for ValueReference not available.", result);
	}

	private XmlObject readValueReference() throws XmlException, IOException {
		FilterDocument fes = FilterDocument.Factory.parse(getClass().getResourceAsStream("fes.xml"));
		BinarySpatialOpType within = (BinarySpatialOpType) fes.getFilter().getSpatialOps();
		return within.getExpression();
	}

	private XmlObject readLiteral() {
		LiteralDocument xml = LiteralDocument.Factory.newInstance();
		LiteralType literal = xml.addNewLiteral();
		literal.setType(new QName("http://test.xml", "element"));
		return literal;
	}
	
}
