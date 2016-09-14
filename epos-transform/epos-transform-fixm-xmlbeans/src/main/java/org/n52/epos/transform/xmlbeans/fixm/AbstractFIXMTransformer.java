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
package org.n52.epos.transform.xmlbeans.fixm;

import javax.xml.namespace.QName;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.epos.event.EposEvent;
import org.n52.epos.transform.EposTransformer;
import org.n52.epos.transform.TransformationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

public abstract class AbstractFIXMTransformer implements EposTransformer {
    
        private static final Logger LOG = LoggerFactory.getLogger(AbstractFIXMTransformer.class);
	
	@Override
	public boolean supportsInput(Object input, String contentType) {
		if (input instanceof XmlObject) {
			return supportsXmlBeansInput((XmlObject) input);
		}
		return supportsInput(input, getSupportedQName(), contentType);
	}
	
	protected boolean supportsInput(Object input, QName qn, String contentType) {
		if (input instanceof Element) {
                    Element elem = (Element) input;

                    return elem.getLocalName().equals(qn.getLocalPart()) &&
                                    elem.getNamespaceURI().equals(qn.getNamespaceURI());
		}
                
                if (input instanceof CharSequence) {
                    if ("application/xml".equals(contentType)) {
                        return true;
                    }
                }
		
                return false;
	}
	
	@Override
	public EposEvent transform(Object input, String contentType) throws TransformationException {
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
                else if (input instanceof CharSequence) {
                    try {
                        xo = XmlObject.Factory.parse(input.toString());
                    } catch (XmlException ex) {
                        LOG.warn("Could not parse XML", ex);
                        throw new TransformationException(ex);
                    }
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
