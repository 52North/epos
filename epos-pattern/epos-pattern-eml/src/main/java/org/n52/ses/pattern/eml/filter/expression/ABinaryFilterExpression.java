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

package org.n52.ses.pattern.eml.filter.expression;

import java.util.HashSet;

import org.apache.xmlbeans.XmlObject;




/**
 * Representation of a binary expression.
 * 
 * @author Thomas Everding
 * 
 */
public abstract class ABinaryFilterExpression extends AFilterExpression {
	
	/**
	 * Indicates that multiple properties are used in a binary expression. Can be returned by "GetUsedProperties".
	 */
	public static final String MULTIPLE_USED_PROPERTIES_IDENTIFIER = "### Mulitple used Properties ###";

	/**
	 * left part of comparison
	 */
	protected AFilterExpression first;
	
	/**
	 * right part of comparison
	 */
	protected AFilterExpression second;
	
	
	
	
//	/**
//	 * initializes the expression for OGC filter encoding 1
//	 */
//	protected void initialize(BinaryOperatorType binaryOp, HashSet<Object> propertyNames) {
//		XmlObject[] innerExpressions = binaryOp.getExpressionArray();
//		
//		this.first = FACTORY.buildFilterExpression(innerExpressions[0], propertyNames, this);
//		this.second = FACTORY.buildFilterExpression(innerExpressions[1], propertyNames, this);
//	}
	
	
	/**
	 * initializes the expression for OGC filter encoding 2
	 * 
	 * @param firstArg first function argument (fes:expression)
	 * @param secondArg second function argument (fes:expression)
	 * @param propertyNames the used propertyNames in this expression
	 */
	protected void initialize (XmlObject firstArg, XmlObject secondArg, HashSet<Object> propertyNames) {
		//parse inner elements
		this.first = FACTORY.buildFilterExpression(firstArg, propertyNames, this);
		this.second = FACTORY.buildFilterExpression(secondArg, propertyNames, this);
	}
	
	
	/**
	 * @return the number of used properties
	 */
	public int getUsedPropertyCount() {
		if (this.first.getUsedProperty() != null) {
			if (this.second.getUsedProperty() != null) {
				return 2;
			}
			return 1;
		}
		
		return 0;
	}
	
	/**
	 * @return a 2-length String-array with the used properties. 
	 */
	public String[] getUsedPropertyArray() {
		String [] result = new String[2];
		
		if (this.first.getUsedProperty() != null) {
			result[0] = this.first.getUsedProperty();
			
			if (this.second.getUsedProperty() != null) {
				result[1] = this.second.getUsedProperty();
			}
		}
		else {
			if (this.second.getUsedProperty() != null) {
				result[0] = this.second.getUsedProperty();
			}
		}
		
		return result;
	}
	
	/**
	 * An {@link ABinaryFilterExpression} receives its usedProperties from its child objects.
	 */
	@Override
	public String getUsedProperty() {
		if (this.first.getUsedProperty() != null) {
			
			if (this.second.getUsedProperty() == null) {
				return this.first.getUsedProperty();	
			}
			//TODO both have used properties?!
			//Thomas: Refactor to use a Sting[] or something similar instead as return type
			//what should be done here?
			return MULTIPLE_USED_PROPERTIES_IDENTIFIER;
			
		}
		else if (this.second.getUsedProperty() != null) {
			return this.second.getUsedProperty();
		}

		return null;
	}
}
