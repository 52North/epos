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
package org.n52.epos.fes.comparison;

import java.util.Objects;
import org.n52.epos.fes.StatementPartial;
import org.n52.epos.fes.operands.Operand;
import org.n52.epos.fes.operands.ValueReferenceOperand;

/**
 *
 * @author <a href="mailto:m.rieke@52north.org">Matthes Rieke</a>
 */
public abstract class BiComparisonFilter implements StatementPartial {

    protected final Operand one;
    protected final Operand two;

    public BiComparisonFilter(Operand one, Operand two) {
        Objects.requireNonNull(one);
        Objects.requireNonNull(two);
        Class<? extends Operand> oneClass = one.getClass();
        Class<? extends Operand> twoClass = two.getClass();
        if (!oneClass.isAssignableFrom(ValueReferenceOperand.class) &&
                !twoClass.isAssignableFrom(ValueReferenceOperand.class) &&
                oneClass != twoClass &&
                !oneClass.isAssignableFrom(twoClass) && 
                !twoClass.isAssignableFrom(oneClass)) {
            throw new IllegalStateException(String.format("Types are not intercomparable: %s vs %s",
                    oneClass, twoClass));
        }
        
        this.one = one;
        this.two = two;
    }

}
