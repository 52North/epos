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
package org.n52.ses.pattern.eml.filter.comparison;

import java.util.HashSet;

import javax.xml.namespace.QName;

import org.n52.ses.pattern.eml.filter.logical.ALogicFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.opengis.fes.x20.BinaryComparisonOpType;
import net.opengis.fes.x20.ComparisonOpsType;
import net.opengis.fes.x20.PropertyIsBetweenType;


/**
 * Builds comparison filters.
 * 
 * @author Thomas Everding
 *
 */
public class ComparisonFilterFactory {
	
	/*
	 * Logger instance for this class
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(ComparisonFilterFactory.class);
	
	private static final QName BETWEEN_QNAME = new QName("http://www.opengis.net/fes/2.0", "PropertyIsBetween");
	
	private static final QName EQUAL_QNAME = new QName("http://www.opengis.net/fes/2.0", "PropertyIsEqualTo");
	
	private static final QName GREATER_QNAME = new QName("http://www.opengis.net/fes/2.0", "PropertyIsGreaterThan");
	
	private static final QName GREATER_OR_EQUAL_QNAME = new QName("http://www.opengis.net/fes/2.0", "PropertyIsGreaterThanOrEqualTo");
	
	private static final QName LESS_QNAME = new QName("http://www.opengis.net/fes/2.0", "PropertyIsLessThan");
	
	private static final QName LESS_OR_EQUAL_QNAME = new QName("http://www.opengis.net/fes/2.0", "PropertyIsLessThanOrEqualTo");
	
	private static final QName NOT_EQUAL_QNAME = new QName("http://www.opengis.net/fes/2.0", "PropertyIsNotEqualTo");
	
	/**
	 * Builds a new comparison filter
	 * 
	 * @param comparisonOp definition of the filter
	 * @param propertyNames names of the properties used in this filter / pattern
	 * 
	 * @return the new {@link ALogicFilter}
	 */
	public AComparisonFilter buildComparisonFilter(ComparisonOpsType comparisonOp, HashSet<Object > propertyNames) {
		//TODO
		
		QName coQName = comparisonOp.newCursor().getName();
		
		/*
		 * non binary operators
		 */
		
		//check between
		if (BETWEEN_QNAME.equals(coQName)) {
			PropertyIsBetweenType betweenOp = (PropertyIsBetweenType) comparisonOp;
			return new BetweenFilter(betweenOp, propertyNames);
		}
		
		/*
		 * binary operators
		 */
		BinaryComparisonOpType binaryOp = (BinaryComparisonOpType) comparisonOp;
		
		//check equal
		if (EQUAL_QNAME.equals(coQName)) {
			return new EqualToFilter(binaryOp, propertyNames);
		}
		
		//check not equal
		else if (NOT_EQUAL_QNAME.equals(coQName)) {
			return new NotEqualToFilter(binaryOp, propertyNames);
		}
		
		//check greater
		else if (GREATER_QNAME.equals(coQName)) {
			return new GreaterThanFilter(binaryOp, propertyNames);
		}
		
		//check greater or equal
		else if (GREATER_OR_EQUAL_QNAME.equals(coQName)) {
			return new GreaterThanOrEqualToFilter(binaryOp, propertyNames);
		}
		
		//check less
		else if (LESS_QNAME.equals(coQName)) {
			return new LessThanFilter(binaryOp, propertyNames);
		}
		
		//check less or equal
		else if (LESS_OR_EQUAL_QNAME.equals(coQName)) {
			return new LessThanOrEqualToFilter(binaryOp, propertyNames);
		}
		
		logger.warn("unable to build comparison filter for '" + comparisonOp.newCursor().getName().toString() + "'");
		return null;
	}
	
}
