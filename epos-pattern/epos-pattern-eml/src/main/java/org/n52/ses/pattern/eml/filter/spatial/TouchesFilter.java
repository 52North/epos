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
package org.n52.ses.pattern.eml.filter.spatial;


import net.opengis.fes.x20.BinarySpatialOpType;

/**
 * 
 * @author Matthes Rieke <m.rieke@uni-muenster.de>
 *
 */
public class TouchesFilter extends ABinarySpatialFilter {

	/**
	 * 
	 * Constructor
	 *
	 * @param bsOp FES binary spatial operator
	 */ 
	public TouchesFilter(BinarySpatialOpType bsOp) {
		super(bsOp);
	}

	@Override
	public String createExpressionString(boolean complexPatternGuard) {
			return createExpressionForBinaryFilter("touches");
	}

	@Override
	public void setUsedProperty(String nodeValue) {
		/*empty*/
	}

}