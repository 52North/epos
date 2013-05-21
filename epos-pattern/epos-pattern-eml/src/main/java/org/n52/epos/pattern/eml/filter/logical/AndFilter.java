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
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * Part of the diploma thesis of Thomas Everding.
 * @author Thomas Everding
 */


package org.n52.epos.pattern.eml.filter.logical;

import java.util.HashSet;

import org.n52.epos.pattern.eml.filter.IFilterElement;


import net.opengis.fes.x20.BinaryLogicOpType;


/**
 * Representation of and filters.
 * 
 * @author Thomas Everding
 *
 */
public class AndFilter extends ABinaryLogicFilter {
	
	/**
	 * 
	 * Constructor
	 *
	 * @param binaryOp filter definition
	 * @param propertyNames names of the properties used in this filter / pattern
	 */
	public AndFilter(BinaryLogicOpType binaryOp, HashSet<Object > propertyNames) {
		this.initialize(binaryOp, propertyNames);
	}

	@Override
	public String createExpressionString(boolean complexPatternGuard) {
		StringBuilder result = new StringBuilder("(");

		for (IFilterElement	elem : this.elements) {
			result.append("(");
			result.append(elem.createExpressionString(complexPatternGuard));
			result.append(")");
			result.append(" and ");
		}
		
		//remove last 'and'
		result.delete(result.length() - 4, result.length());
		result.append(")");
		
		return result.toString();
	}
	
}
