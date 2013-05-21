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

package org.n52.epos.pattern.eml.filter;

import java.util.HashSet;

import org.n52.epos.pattern.eml.filter.comparison.AComparisonFilter;
import org.n52.epos.pattern.eml.filter.logical.ALogicFilter;
import org.n52.epos.pattern.eml.filter.spatial.ASpatialFilter;
import org.n52.epos.pattern.eml.filter.temporal.ATemporalFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.opengis.fes.x20.FilterType;


/**
 * Representation of a filter. (a complete guard)
 * 
 * @author Thomas Everding
 * 
 */
public class StatementFilter implements IFilterElement {

	/*
	 * Logger instance for this class
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(StatementFilter.class);

	private IFilterElement child;

	/**
	 * the used property of this Filter
	 */
	protected String usedProperty = null;


	/**
	 * 
	 * Constructor
	 * 
	 * @param filter the OGC filter encoding complaint filter statement
	 * @param propertyNames all found property names of this filter or pattern
	 */
	public StatementFilter(FilterType filter, HashSet<Object> propertyNames) {
		this.initialize(filter, propertyNames);
	}

	/**
	 * initializes the filter
	 * 
	 * @param filter
	 *            Filter definition
	 */
	private void initialize(FilterType filter, HashSet<Object> propertyNames) {
		if (filter.isSetLogicOps()) {
			// parse logic operator
			this.child = ALogicFilter.FACTORY.buildLogicFilter(filter
					.getLogicOps(), propertyNames);
		} else if (filter.isSetComparisonOps()) {
			// parse comparison operator
			this.child = AComparisonFilter.FACTORY.buildComparisonFilter(filter
					.getComparisonOps(), propertyNames);
		} else if (filter.isSetSpatialOps()) {
			//parse spatial filter
			this.child = ASpatialFilter.FACTORY.buildSpatialFilter(filter.getSpatialOps());
		} else if (filter.isSetTemporalOps()) {
			//parse temporal filter
			this.child = ATemporalFilter.FACTORY.buildTemporalFilter(filter.getTemporalOps());
		} else {
			logger.warn("operator type not supported");
			return;
		}
	}

	@Override
	public String createExpressionString(boolean complexPatternGuard) {
		return this.child.createExpressionString(complexPatternGuard);
	}
	
	@Override
	public void setUsedProperty(String nodeValue) {
		this.usedProperty = nodeValue;
	}

}
