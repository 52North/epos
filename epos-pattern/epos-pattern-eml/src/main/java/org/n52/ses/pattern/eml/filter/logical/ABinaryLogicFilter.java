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

package org.n52.ses.pattern.eml.filter.logical;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.n52.ses.pattern.eml.filter.IFilterElement;
import org.n52.ses.pattern.eml.filter.comparison.AComparisonFilter;
import org.n52.ses.pattern.eml.filter.spatial.ASpatialFilter;
import org.n52.ses.pattern.eml.filter.temporal.ATemporalFilter;

import net.opengis.fes.x20.BinaryLogicOpType;
import net.opengis.fes.x20.ComparisonOpsType;
import net.opengis.fes.x20.LogicOpsType;
import net.opengis.fes.x20.SpatialOpsType;
import net.opengis.fes.x20.TemporalOpsType;


/**
 * Representation of binary logic filters.
 * 
 * @author Thomas Everding
 *
 */
public abstract class ABinaryLogicFilter extends ALogicFilter {
	
	
	/**
	 * List {@link IFilterElement}s registered to this Filter.
	 */
	protected List<IFilterElement> elements = new ArrayList<IFilterElement>();
	
	/**
	 * initializes the filter
	 * 
	 * @param binaryOp the filter definition
	 */
//	protected void initialize(BinaryLogicOpType binaryOp, HashSet<Object > propertyNames) {
//		if (binaryOp.getLogicOpsArray().length == 2) {
//			//only logical operators
//			first  = ALogicFilter.FACTORY.buildLogicFilter(binaryOp.getLogicOpsArray(0), propertyNames);
//			second = ALogicFilter.FACTORY.buildLogicFilter(binaryOp.getLogicOpsArray(1), propertyNames);
//		}
//		else if (binaryOp.getComparisonOpsArray().length == 2){
//			//only comparison operators
//			first  = AComparisonFilter.FACTORY.buildComparisonFilter(binaryOp.getComparisonOpsArray(0), propertyNames);
//			second = AComparisonFilter.FACTORY.buildComparisonFilter(binaryOp.getComparisonOpsArray(1), propertyNames);
//		}
//		else if (binaryOp.getSpatialOpsArray().length == 2) {
//			first  = ASpatialFilter.FACTORY.buildSpatialFilter(binaryOp.getSpatialOpsArray(0));
//			second = ASpatialFilter.FACTORY.buildSpatialFilter(binaryOp.getSpatialOpsArray(1));
//		}
//		else if (binaryOp.getTemporalOpsArray().length == 2) {
//			first = ATemporalFilter.FACTORY.buildTemporalFilter(binaryOp.getTemporalOpsArray(0));
//			second = ATemporalFilter.FACTORY.buildTemporalFilter(binaryOp.getTemporalOpsArray(1));
//		}
//		else {
//			if (binaryOp.getLogicOpsArray().length == 1) {
//				first  = ALogicFilter.FACTORY.buildLogicFilter(binaryOp.getLogicOpsArray(0), propertyNames);
//			}
//			if (binaryOp.getComparisonOpsArray().length == 1) {
//				if (first != null) {
//					second = AComparisonFilter.FACTORY.buildComparisonFilter(binaryOp.getComparisonOpsArray(0), propertyNames);
//				} else {
//					first = AComparisonFilter.FACTORY.buildComparisonFilter(binaryOp.getComparisonOpsArray(0), propertyNames);
//				}
//			}
//			if (binaryOp.getSpatialOpsArray().length == 1) {
//				if (first != null) {
//					if (second == null) {
//						second = ASpatialFilter.FACTORY.buildSpatialFilter(binaryOp.getSpatialOpsArray(0));
//					}
//				} else {
//					first = ASpatialFilter.FACTORY.buildSpatialFilter(binaryOp.getSpatialOpsArray(0));
//				}
//			}
////			//one of each
////			first  = ALogicFilter.FACTORY.buildLogicFilter(binaryOp.getLogicOpsArray(0), propertyNames);
////			second = AComparisonFilter.FACTORY.buildComparisonFilter(binaryOp.getComparisonOpsArray(0), propertyNames);
//		}
//	}
	
	/**
	 * Init method for this filter.
	 * 
	 * @param binaryOp the operator type
	 * @param propertyNames set of used property names
	 */
	protected void initialize(BinaryLogicOpType binaryOp, HashSet<Object > propertyNames) {
		if (binaryOp.getLogicOpsArray().length > 0) {
			for (LogicOpsType lops : binaryOp.getLogicOpsArray()) {
				this.elements.add(ALogicFilter.FACTORY.buildLogicFilter(lops, propertyNames));
			}
		}
		if (binaryOp.getComparisonOpsArray().length > 0){
			for (ComparisonOpsType cops : binaryOp.getComparisonOpsArray()) {
				this.elements.add(AComparisonFilter.FACTORY.buildComparisonFilter(cops, propertyNames));
			}
		}
		if (binaryOp.getSpatialOpsArray().length > 0) {
			for (SpatialOpsType sops : binaryOp.getSpatialOpsArray()) {
				this.elements.add(ASpatialFilter.FACTORY.buildSpatialFilter(sops));
			}
		}
		if (binaryOp.getTemporalOpsArray().length > 0) {
			for (TemporalOpsType tops : binaryOp.getTemporalOpsArray()) {
				this.elements.add(ATemporalFilter.FACTORY.buildTemporalFilter(tops));
			}
		}
	}
}
