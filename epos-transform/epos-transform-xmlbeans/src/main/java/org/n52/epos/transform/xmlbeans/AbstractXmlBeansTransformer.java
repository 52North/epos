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

import javax.xml.namespace.QName;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.epos.event.EposEvent;
import org.n52.epos.transform.EposTransformer;
import org.n52.epos.transform.TransformationException;
import org.w3c.dom.Element;

public abstract class AbstractXmlBeansTransformer implements EposTransformer {
	
	@Override
	public boolean supportsInput(Object input) {
		if (input instanceof XmlObject) {
			return supportsXmlBeansInput((XmlObject) input);
		}
		return supportsInput(input, getSupportedQName());
	}
	
	protected boolean supportsInput(Object input, QName qn) {
		if (!(input instanceof Element)) {
			return false;
		}
		
		Element elem = (Element) input;
		
		return elem.getLocalName().equals(qn.getLocalPart()) &&
				elem.getNamespaceURI().equals(qn.getNamespaceURI());
	}
	
	@Override
	public EposEvent transform(Object input) throws TransformationException {
		XmlObject xo = null;
		if (input instanceof Element) {
			try {
				xo = parseToXmlObject((Element) input);
			} catch (XmlException e) {
				throw new TransformationException(e);
			}
		}
		else if (input instanceof XmlObject) {
			xo = (XmlObject) input;
		}
		
		return transformXmlBeans(xo);
	}
	
	protected XmlObject parseToXmlObject(Element elem) throws XmlException {
		return XmlObject.Factory.parse(elem);
	}
	
	@Override
	public short getPriority() {
		/*
		 * provide a medium priority by default
		 */
		return 0;
	}

	protected abstract EposEvent transformXmlBeans(XmlObject xo) throws TransformationException;
	
	protected abstract boolean supportsXmlBeansInput(XmlObject input);
	
	protected abstract QName getSupportedQName();
}
