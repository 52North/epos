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

package org.n52.epos.pattern.eml.filter.expression;

import java.util.HashSet;

import org.apache.xmlbeans.XmlObject;



/**
 * Represents an expression for a multiplication 
 * 
 * @author Thomas Everding
 *
 */
public class MulExpression extends ABinaryFilterExpression{
	
	private boolean initialized = false;
	
//	/**
//	 * 
//	 * Constructor for OGC filter encoding 1 mul expressions
//	 *
//	 * @param binaryOp the expression definition
//	 * @param propertyNames name of the known event properties
//	 */
//	public MulExpression(BinaryOperatorType binaryOp, HashSet<Object > propertyNames) {
//		this.initialize(binaryOp, propertyNames);
//		this.initialized = true;
//	}
	
	
	/**
	 * 
	 * Constructor for OGC filter encoding 2 mul functions
	 * 
	 * @param args arguments of the addition function.
	 * @param propertyNames name of the known event properties
	 */
	public MulExpression(XmlObject[] args, HashSet<Object> propertyNames) {
		if (args.length < 2) {
			throw new RuntimeException("illegal argument count for mul function");
		}
		
		//set arguments
		XmlObject firstArg = args[0];
		XmlObject secondArg = args[1];
		
		//initialize
		if (firstArg != null && secondArg != null) {
			this.initialize(firstArg, secondArg, propertyNames);
			this.initialized = true;
		}
	}
	

	@Override
	public String createExpressionString(boolean complexPatternGuard) {
		String result = "";
		
		if (this.initialized) {
			result = "("
					+ this.first.createExpressionString(complexPatternGuard)
					+ " * "
					+ this.second.createExpressionString(complexPatternGuard)
					+ ")";
		}
		return result;
	}
	
}
