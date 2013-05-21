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
import org.n52.oxf.xmlbeans.tools.XmlUtil;
import org.w3c.dom.Element;




/**
 * Represents an expression for a property name
 * 
 * @author Thomas Everding
 * 
 */
public class ValueReferenceExpression extends AFilterExpression {
	
	private String valueReference;
	
	
	/**
	 * 
	 * Constructor
	 * 
	 * @param expressionType name of a property
	 * @param propertyNames hash map with the known property names
	 */
	public ValueReferenceExpression(XmlObject expressionType, HashSet<Object> propertyNames) {
		Element elem = (Element) expressionType.getDomNode();
		String name = XmlUtil.toString(elem.getFirstChild()).trim();
		//replaceAll(":", "__")
		this.valueReference = name.replaceAll("/", ".");
		
		if (!propertyNames.contains(this.valueReference)) {
			propertyNames.add(this.valueReference);
		}
	}
	

	@Override
	public String createExpressionString(boolean complexPatternGuard) {
		if (complexPatternGuard) {
//			StringBuilder log = new StringBuilder();
//			log.append("ValueReference in complex pattern guard found");
//			log.append("\n\t value reference: " + this.valueReference);
//			logger.info(log.toString());
			
			String result = this.valueReference;//.replaceAll("\\.", ":");
			return result;
		}
		
		//remove event name part
		int i;
		if (((i = this.valueReference.indexOf(".")) > 0)) {
			return this.valueReference.substring(i + 1);
		}
		
		return this.valueReference;
	}
	
}
