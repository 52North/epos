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
package org.n52.epos.fes;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.n52.epos.fes.comparison.PropertyIsGreaterThan;
import org.n52.epos.fes.logical.AndOperator;
import org.n52.epos.fes.logical.NotOperator;
import org.n52.epos.fes.operands.QuantityOperand;
import org.n52.epos.fes.operands.ValueReferenceOperand;

/**
 *
 * @author <a href="mailto:m.rieke@52north.org">Matthes Rieke</a>
 */
public class HierarchicalFilterTest {

    @Test
    public void testComplexFilterStatements() {
        QuantityOperand op1 = new QuantityOperand(23.5);
        QuantityOperand op2 = new QuantityOperand(5.23);
        ValueReferenceOperand vr1 = new ValueReferenceOperand("procedure");
        ValueReferenceOperand vr2 = new ValueReferenceOperand("observedProperty");
        
        PropertyIsGreaterThan greaterThan = new PropertyIsGreaterThan(op1, vr1);
        PropertyIsGreaterThan greaterThan2 = new PropertyIsGreaterThan(vr2, op2);
        NotOperator not = new NotOperator(new AndOperator(greaterThan, greaterThan2));
        
        Assert.assertThat(not.getStatementPartial(), CoreMatchers.equalTo(
                "not((23.5 > procedure) and (observedProperty > 5.23))"));
    }
    
}
