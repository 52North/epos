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

package org.n52.ses.eml.v001.filterlogic.esper.customFunctions;

/**
 * contains the names of the custom functions (methods)
 * 
 * @author Thomas Everding
 *
 */
public class MethodNames {
	
	/**
	 * name for the method to test if an event is the causal ancestor of another event
	 */
	public static final String IS_CAUSAL_ANCESTOR_NAME = "CausalityMethods.isCausalAncestorOf";
	
	/**
	 * name for the method to test if an event is not the causal ancestor of another event
	 */
	public static final String IS_NOT_CAUSAL_ANCESTOR_NAME = "CausalityMethods.isNotCausalAncestorOf";
	
	/**
	 * name for the method to test if a property exists in a received event
	 */
	public static final String PROPERTY_EXISTS_NAME = "PropertyMethods.propertyExists";
	
	/**
	 * operation name to be used in esper statements for the any interacts filter
	 */
	public static final String ANY_INTERACTS_OPERATION = "TemporalMethods.anyInteracts";
	
	/**
	 * prefix for the spatial methods
	 */
	public static final String SPATIAL_METHODS_PREFIX = "SpatialMethods.";
}
