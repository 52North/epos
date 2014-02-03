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
import org.n52.epos.pattern.eml.filter.IFilterElement;
import org.n52.epos.pattern.eml.filter.expression.AFilterExpression;
import org.n52.epos.pattern.eml.filter.expression.FilterExpressionFactory;

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
		System.out.println(result.getUsedProperty());
		Assert.assertTrue("UsedProperty not as expected!", result.getUsedProperty().equals("urn:test:value.asdf.geometry"));
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
