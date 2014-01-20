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
