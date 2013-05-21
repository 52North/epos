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

package org.n52.ses.eml.v001.filter.logical;

import java.util.HashSet;

import javax.xml.namespace.QName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import net.opengis.fes.x20.BinaryLogicOpType;
import net.opengis.fes.x20.LogicOpsType;
import net.opengis.fes.x20.UnaryLogicOpType;


/**
 * Builds logic filters.
 * 
 * @author Thomas Everding
 *
 */
public class LogicFilterFactory {
	
	/*
	 * Logger instance for this class
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(LogicFilterFactory.class);
	
	private static final QName AND_QNAME = new QName("http://www.opengis.net/fes/2.0", "And");
	
	private static final QName OR_QNAME = new QName("http://www.opengis.net/fes/2.0", "Or");
	
	private static final QName NOT_QNAME = new QName("http://www.opengis.net/fes/2.0", "Not");
	
	/**
	 * Builds a new logic filter
	 * 
	 * @param logicOp definition of the filter
	 * @param propertyNames names of the properties used in this filter / pattern
	 * 
	 * @return the new {@link ALogicFilter}
	 */
	public ALogicFilter buildLogicFilter(LogicOpsType logicOp, HashSet<Object > propertyNames) {
		//TODO
		QName loQName = logicOp.newCursor().getName();
		
		/*
		 * non binary operators 
		 */
		
		//check Not
		if (NOT_QNAME.equals(loQName)) {
			//create new NotFilter
			UnaryLogicOpType unaryOp = (UnaryLogicOpType) logicOp;
			return new NotFilter(unaryOp, propertyNames);
		}
		
		/*
		 * binary operators
		 */
		BinaryLogicOpType binaryOp = (BinaryLogicOpType) logicOp;
		
		//check And
		if (AND_QNAME.equals(loQName)) {
			//create new AndFilter
			return new AndFilter(binaryOp, propertyNames);
		}
		
		//check Or
		else if (OR_QNAME.equals(loQName)) {
			//create new OrFilter
			return new OrFilter(binaryOp, propertyNames);
		}
		
		LogicFilterFactory.logger.warn("unable to build filter expression for '" + loQName.toString() + "'");
		return null;
	}
	
}
