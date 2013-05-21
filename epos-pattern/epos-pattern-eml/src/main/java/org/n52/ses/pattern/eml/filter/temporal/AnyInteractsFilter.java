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
package org.n52.ses.pattern.eml.filter.temporal;

import net.opengis.fes.x20.BinaryTemporalOpType;
import net.opengis.fes.x20.TemporalOpsType;

import org.joda.time.Interval;
import org.n52.ses.pattern.eml.filterlogic.esper.customFunctions.MethodNames;
import org.n52.ses.pattern.eml.filterlogic.esper.customFunctions.TemporalMethods;

/**
 * Implementation of the FES2.0 AnyInteracts filter 
 * that checks for any interactions (intersections)
 * of a time primitive against a time interval.
 * 
 * @author Thomas Everding
 *
 */
public class AnyInteractsFilter extends ATemporalFilter {

	/**
	 * 
	 * Constructor
	 *
	 * @param temporalOp the FES temporal operator
	 */
	public AnyInteractsFilter(TemporalOpsType temporalOp) {
		super(temporalOp);
	}


	@Override
	public String createExpressionString(boolean complexPatternGuard) {

		//get reference interval
		BinaryTemporalOpType anyInteracts = (BinaryTemporalOpType) this.temporalOp;
		Interval intersectsInterval = this.parseGMLTimePeriodFromBinaryTemporalOp(anyInteracts);
		
		//build expression
		StringBuilder sb = new StringBuilder();
		
		//add property check
		sb.append("(");
		sb.append(MethodNames.PROPERTY_EXISTS_NAME);
		sb.append("(this, \"");
		sb.append(anyInteracts.getValueReference());
		sb.append("\") AND "); //property check close
		
		//add any interacts
		sb.append(MethodNames.ANY_INTERACTS_OPERATION);
		sb.append("(this, \"");
		
		//add test time reference
		sb.append(anyInteracts.getValueReference());
		
		//add reference interval
		sb.append("\", \"");
		sb.append(intersectsInterval.getStartMillis());
		sb.append(TemporalMethods.INTERVAL_SEPARATOR);
		sb.append(intersectsInterval.getEndMillis());
		sb.append("\")"); //any interacts close
		
		sb.append(")"); //all close
		
		return sb.toString();
	}


	@Override
	public void setUsedProperty(String nodeValue) {
		/*empty*/
	}

}
