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
package org.n52.epos.fes.logical;

import java.util.Arrays;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.n52.epos.fes.StatementPartial;
import org.n52.epos.fes.comparison.PropertyIsGreaterThan;
import org.n52.epos.fes.operands.QuantityOperand;

/**
 *
 * @author <a href="mailto:m.rieke@52north.org">Matthes Rieke</a>
 */
public class BiLogicalOperatorTest {

    @Test
    public void testWrapWithAnd() {
        StatementPartial partial = BiLogicalOperator.wrapInAnd(Arrays.asList(new StatementPartial[] {
            new PropertyIsGreaterThan(new QuantityOperand(1.0), new QuantityOperand(2.0)),
            new PropertyIsGreaterThan(new QuantityOperand(3.0), new QuantityOperand(4.0)),
            new PropertyIsGreaterThan(new QuantityOperand(5.0), new QuantityOperand(6.0)),
            new PropertyIsGreaterThan(new QuantityOperand(7.0), new QuantityOperand(8.0))
        }));
        String stmt = partial.getStatementPartial();
        Assert.assertThat(stmt, CoreMatchers.equalTo("(1.0 > 2.0) and ((3.0 > 4.0) and ((5.0 > 6.0) and (7.0 > 8.0)))"));
        
        partial = BiLogicalOperator.wrapInAnd(Arrays.asList(new StatementPartial[] {
            new PropertyIsGreaterThan(new QuantityOperand(1.0), new QuantityOperand(2.0)),
            new PropertyIsGreaterThan(new QuantityOperand(3.0), new QuantityOperand(4.0)),
            new PropertyIsGreaterThan(new QuantityOperand(5.0), new QuantityOperand(6.0)),
            new PropertyIsGreaterThan(new QuantityOperand(7.0), new QuantityOperand(8.0)),
            new PropertyIsGreaterThan(new QuantityOperand(9.0), new QuantityOperand(10.0))
        }));
        stmt = partial.getStatementPartial();
        Assert.assertThat(stmt, CoreMatchers.equalTo("(1.0 > 2.0) and ((3.0 > 4.0) and ((5.0 > 6.0) and ((7.0 > 8.0) and (9.0 > 10.0))))"));
    }
    
}
