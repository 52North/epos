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
package org.n52.epos.fes.decoding.xml;

import java.util.Objects;
import net.opengis.fes.x20.LiteralType;
import net.opengis.gml.x32.QuantityDocument;
import net.opengis.gml.x32.QuantityDocument.Quantity;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.n52.epos.fes.decoding.OperandDecoder;
import org.n52.epos.fes.operands.CategoryOperand;
import org.n52.epos.fes.operands.Operand;
import org.n52.epos.fes.operands.QuantityOperand;
import org.n52.epos.fes.operands.ValueReferenceOperand;

/**
 *
 * @author <a href="mailto:m.rieke@52north.org">Matthes Rieke</a>
 */
public class FesXmlbeansOperandDecoder implements OperandDecoder<XmlObject> {


    @Override
    public Operand decode(XmlObject object) {
        Objects.requireNonNull(object);
        
        if (object instanceof XmlString) {
            return new ValueReferenceOperand(((XmlString) object).getStringValue());
        }
        
        if (object instanceof LiteralType) {
            return decodeLiteral((LiteralType) object);
        }
        
        throw new UnsupportedOperationException(String.format("Type '%s' not supported yet", object.getDomNode().getLocalName()));
    }

    private Operand decodeLiteral(LiteralType literalType) {
        XmlObject child = resolveChild(literalType);
        
        if (child == null) {
            return new CategoryOperand(extractStringValue(literalType));
        }
        
        if (child instanceof Quantity) {
            Quantity q = (Quantity) child;
            return new QuantityOperand(q.getDoubleValue(), q.getUom());
        }
        
        if (child instanceof XmlString) {
            return new CategoryOperand(((XmlString) child).getStringValue());
        }
        
        throw new UnsupportedOperationException(String.format("Literal subType '%s' not supported yet", child.getDomNode().getLocalName()));
    }
    

    private XmlObject resolveChild(XmlObject xo) {
        Objects.requireNonNull(xo);
        XmlCursor cur = xo.newCursor();
        
        XmlObject result = null;
        if (cur.toFirstChild()) {
            result = cur.getObject();
        }
        cur.dispose();
        
        return result;
    }

    private String extractStringValue(LiteralType literalType) {
        Objects.requireNonNull(literalType);
        
        XmlCursor cur = literalType.newCursor();
        String result = cur.getTextValue();
        cur.dispose();
        
        return result;
    }

}
