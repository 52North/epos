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

package org.n52.epos.pattern.eml.filter.comparison;

import java.util.HashSet;

import org.n52.epos.pattern.eml.filter.expression.AFilterExpression;
import org.n52.epos.pattern.eml.filterlogic.esper.customFunctions.MethodNames;

import net.opengis.fes.x20.PropertyIsBetweenType;


/**
 * Filter to compare a value and a range.
 * 
 * @author Thomas Everding
 *
 */
public class BetweenFilter extends AComparisonFilter{
	
	private AFilterExpression lower;
	
	private AFilterExpression upper;
	
	private AFilterExpression test;
	
	
	/**
	 * 
	 * Constructor
	 *
	 * @param betweenOp filter definition
	 * @param propertyNames names of the properties used in this filter / pattern
	 */
	public BetweenFilter(PropertyIsBetweenType betweenOp, HashSet<Object > propertyNames) {
		//TODO parse expression
		
		this.test  = AFilterExpression.FACTORY.buildFilterExpression(betweenOp.getExpression(), propertyNames, this);
		this.lower = AFilterExpression.FACTORY.buildFilterExpression(betweenOp.getLowerBoundary().getExpression(), propertyNames, this);
		this.upper = AFilterExpression.FACTORY.buildFilterExpression(betweenOp.getUpperBoundary().getExpression(), propertyNames, this);
	}

	@Override
	public String createExpressionString(boolean complexPatternGuard) {
		String result = "";
		
		if (this.lower.getUsedProperty() != null || this.upper.getUsedProperty() != null || this.test.getUsedProperty() != null) {
			result += "(";
			
			boolean first = true;
			String usedProp;
			String usedEvent = "";
			String usedField = "";
			if (this.lower.getUsedProperty() != null) {
				usedProp = this.lower.getUsedProperty();
				if (usedProp.contains(".")) {
					usedEvent = usedProp.substring(0, usedProp.indexOf(".")+1);
					usedField = usedProp.substring(usedProp.indexOf(".")+1, usedProp.length());
				} else {
					usedField = usedProp;
				}
				
				result += MethodNames.PROPERTY_EXISTS_NAME + "("+ usedEvent +"this, \"" + usedField + "\") ";
				first = false;
			}
			
			if (this.upper.getUsedProperty() != null) {
				
				if (!first) {
					result += "AND ";
				}
				
				usedProp = this.upper.getUsedProperty();
				if (usedProp.contains(".")) {
					usedEvent = usedProp.substring(0, usedProp.indexOf(".")+1);
					usedField = usedProp.substring(usedProp.indexOf(".")+1, usedProp.length());
				} else {
					usedField = usedProp;
				}
				
				result += MethodNames.PROPERTY_EXISTS_NAME + "("+ usedEvent +"this, \"" + usedField + "\") ";
				first = false;
			}
			
			if (this.test.getUsedProperty() != null) {
				
				if (!first) {
					result += "AND ";
				}
				
				
				usedProp = this.test.getUsedProperty();
				if (usedProp.contains(".")) {
					usedEvent = usedProp.substring(0, usedProp.indexOf(".")+1);
					usedField = usedProp.substring(usedProp.indexOf("."), usedProp.length());
				} else {
					usedField = usedProp;
				}
				
				result += MethodNames.PROPERTY_EXISTS_NAME + "("+ usedEvent +"this, \"" + usedField + "\") ";
				first = false;
			}
			
			result += ") AND ";
		}
		
		result += "("
						+ this.test.createExpressionString(complexPatternGuard)
						+ " between "
						+ this.lower.createExpressionString(complexPatternGuard)
						+ " and "
						+ this.upper.createExpressionString(complexPatternGuard)
						+ ")";
		return result;
	}
}
