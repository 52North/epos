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
