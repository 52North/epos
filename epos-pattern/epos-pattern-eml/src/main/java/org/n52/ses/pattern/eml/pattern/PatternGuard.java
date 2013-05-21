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

package org.n52.ses.pattern.eml.pattern;

import java.util.HashSet;

import net.opengis.fes.x20.FilterType;

import org.n52.ses.pattern.eml.Constants;
import org.n52.ses.pattern.eml.filter.StatementFilter;


/**
 * representation of a guard
 * 
 * @author Thomas Everding
 *
 */
public class PatternGuard {
	
	private StatementFilter filter;
	
	private String statement = "";
	
//	private long maxListeningDuration = -1;

	/**
	 * @param filter the filter to set
	 * @param propertyNames all found property names of this pattern
	 */
	public void setFilter(FilterType filter, HashSet<Object> propertyNames) {
		this.filter = new StatementFilter(filter, propertyNames);
	}
	
	
	/**
	 * creates the esper statement for this guard
	 * @param complexPatternGuard if <code>true</code> the property names are used with the event names, else only the
	 * property names are used
	 * 
	 * @return the guard as esper where clause
	 */
	public String createStatement(boolean complexPatternGuard) {
		if (!this.statement.equals("")) {
			//statement already created
			return this.statement;
		}
		
//		if (complexPatternGuard) {
//			//create statement for complex patterns
//			this.statement += Constants.EPL_WHERE 
//							  + " "
//							  + this.filter.createExpressionString(complexPatternGuard);
//			
//			return this.statement;
//		}
		
		//create statement for simple patterns
		this.statement = Constants.EPL_WHERE
						 + " ";
		
//		if (propertyName != null) {
//			String usedEvent = "";
//			String usedField = "";
//			if (propertyName.contains(".")) {
//				usedEvent = propertyName.substring(0, propertyName.indexOf(".")+1);
//				usedField = propertyName.substring(propertyName.indexOf(".")+1, propertyName.length());
//			} else {
//				usedField = propertyName;
//			}
//			
//			this.statement += "(" + MethodNames.PROPERTY_EXISTS_NAME + "("+ usedEvent +"this, \"" + usedField + "\")) AND "; 
//		
//		}
		this.statement += this.filter.createExpressionString(complexPatternGuard);
		
		return this.statement;
	}


//	/**
//	 * sets the max Listening Duration
//	 * @param maxListeningDuration maximum duration for listening
//	 */
//	public void setMaxListeningDuration(long maxListeningDuration) {
//		this.maxListeningDuration = maxListeningDuration;
//	}
	
}
