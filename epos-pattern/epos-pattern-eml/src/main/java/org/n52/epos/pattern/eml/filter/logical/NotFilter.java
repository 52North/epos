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


package org.n52.epos.pattern.eml.filter.logical;

import java.util.HashSet;

import org.n52.epos.pattern.eml.filter.IFilterElement;
import org.n52.epos.pattern.eml.filter.comparison.AComparisonFilter;
import org.n52.epos.pattern.eml.filter.spatial.ASpatialFilter;
import org.n52.epos.pattern.eml.filter.temporal.ATemporalFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.opengis.fes.x20.UnaryLogicOpType;


/**
 * Representation of not filters.
 * 
 * @author Thomas Everding
 *
 */
public class NotFilter extends ALogicFilter{
	
	/*
	 * Logger instance for this class
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(NotFilter.class);
	
	private IFilterElement element;
	
	
	/**
	 * 
	 * Constructor
	 *
	 * @param unaryOp filter definition
	 * @param propertyNames names of the properties used in this filter / pattern
	 */
	public NotFilter(UnaryLogicOpType unaryOp, HashSet<Object > propertyNames) {
		if (unaryOp.isSetComparisonOps()) {
			//element is comparison operator
			this.element = AComparisonFilter.FACTORY.buildComparisonFilter(unaryOp.getComparisonOps(), propertyNames);
		}
		else if (unaryOp.isSetLogicOps()) {
			//element is logical operator
			this.element = ALogicFilter.FACTORY.buildLogicFilter(unaryOp.getLogicOps(), propertyNames);
		}
		else if (unaryOp.isSetSpatialOps()) {
			//element is spatial operator
			this.element = ASpatialFilter.FACTORY.buildSpatialFilter(unaryOp.getSpatialOps());
		}
		else if (unaryOp.isSetTemporalOps()) {
			//element is temporal operator
			this.element = ATemporalFilter.FACTORY.buildTemporalFilter(unaryOp.getTemporalOps());
		}
		else {
			//not supported
			NotFilter.logger.warn("the operator type is not supported");
			throw new RuntimeException("the operator type is not supported");
		}
	}


	@Override
	public String createExpressionString(boolean complexPatternGuard) {
		String result = "( not ("
						+ this.element.createExpressionString(complexPatternGuard)
						+ "))";
		return result;
	}
}
