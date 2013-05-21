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
package org.n52.epos.pattern.eml.filter.expression;

import java.util.HashSet;

import org.apache.xmlbeans.XmlObject;
import org.n52.epos.pattern.eml.filterlogic.esper.customFunctions.MethodNames;


/**
 * 
 * @author Thomas Everding
 *
 */
public class DistanceToExpression extends ABinaryFilterExpression{
	
	private boolean initialized = false;
	
	/**
	 * 
	 * Constructor for OGC filter encoding 2 mul functions
	 * 
	 * @param args arguments of the addition function.
	 * @param propertyNames name of the known event properties
	 */
	public DistanceToExpression(XmlObject[] args, HashSet<Object> propertyNames) {
		//check arguments
		if (args.length < 2) {
			throw new RuntimeException("illegal argument count for distance to function");
		}
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
			StringBuilder sb = new StringBuilder();
			sb.append(MethodNames.SPATIAL_METHODS_PREFIX);
			sb.append("distanceTo(");
			sb.append(this.first.createExpressionString(complexPatternGuard));
			sb.append(", ");
			sb.append(this.second.createExpressionString(complexPatternGuard));
			sb.append(")"); //distance to
			
			result = sb.toString();
		}
		return result;
	}

}
