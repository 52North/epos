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

package org.n52.ses.pattern.eml.filter;

/**
 * Standard methods for all filter elements
 * 
 * @author Thomas Everding
 * 
 */
public interface IFilterElement {
	
	/**
	 * Creates the esper String for this expression
	 * 
	 * @param complexPatternGuard if <code>true</code> the guard is used for a complex pattern, else for a simple
	 * pattern
	 * @return the esper string for this expression
	 */
	public String createExpressionString(boolean complexPatternGuard);

	/**
	 * Sets if a property is used in a filter statement. It has to be checked if it exists.
	 * 
	 * @param nodeValue the property name
	 */
	public void setUsedProperty(String nodeValue);	
}
