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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.xml.namespace.QName;
import net.opengis.fes.x20.BinaryComparisonOpType;
import net.opengis.fes.x20.ComparisonOperatorType;
import net.opengis.fes.x20.ComparisonOperatorsType;
import net.opengis.fes.x20.FilterType;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.n52.epos.fes.StatementPartial;
import org.n52.epos.fes.decoding.FesDecoder;
import org.n52.epos.fes.logical.AndOperator;

/**
 *
 * @author <a href="mailto:m.rieke@52north.org">Matthes Rieke</a>
 */
public class FesXmlbeansFilterTypeDecoder implements FesDecoder<FilterType> {

    @Override
    public StatementPartial decode(FilterType filter) {
        List<StatementPartial> partials = new ArrayList<>();
        
        if (filter.isSetComparisonOps()) {
            if (filter.getComparisonOps() instanceof BinaryComparisonOpType) {
                BinaryComparisonOpType bcops = (BinaryComparisonOpType) filter.getComparisonOps();
                QName qName = bcops.newCursor().getName();
                partials.add(parseComparisonOps(bcops, qName));
            }
        }
        
        if (partials.size() == 1) {
            return partials.get(0);
        }
        else {
            return wrapWithAnd(partials);
        }
    }

    private StatementPartial parseComparisonOps(BinaryComparisonOpType element, QName qName) {
        StatementPartial result = new ComparsionOpsDecoder().decodeBinaryComarisonOperator(element, qName);
        return result;
    }

    protected StatementPartial wrapWithAnd(List<StatementPartial> partials) {
        AndOperator current = new AndOperator();
        AndOperator root = current;
        AndOperator next;
        for (int i = 0; i < partials.size(); i++) {
            StatementPartial p = partials.get(i);
            
            if (i == partials.size() - 2) {
                //second last and last
                next = new AndOperator();
                next.setOne(p);
                next.setTwo(partials.get(i+1));
                current.setTwo(next);
                break;
            }
            else {
                if (i == 0) {
                    //first
                    current.setOne(p);
                }
                else {
                    next = new AndOperator();
                    next.setOne(p);
                    current.setTwo(next);
                    current = next;
                }
            }
            
            
        };
        
        return root;
    }
    
}
