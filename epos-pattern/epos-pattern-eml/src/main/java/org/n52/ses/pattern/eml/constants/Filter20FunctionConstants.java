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
package org.n52.ses.pattern.eml.constants;


/**
 * Constants used in OGC filter encoding 2.0 functions.
 * Every FES2.0 function needs an entry here.
 * 
 * @author Thomas Everding
 *
 */
public class Filter20FunctionConstants {
	
	/**
	 * Name addition function
	 */
	public static final String ADD_FUNC_NAME = "add";
	
	/**
	 * Name of the first argument of the add function
	 */
	public static final String ADD_FUNC_ARG_1_NAME = "firstSummand";
	
	/**
	 * Name of the second argument of the add function
	 */
	public static final String ADD_FUNC_ARG_2_NAME = "secondSummand";
	
	/**
	 * Name subtraction function
	 */
	public static final String SUB_FUNC_NAME = "sub";
	
	/**
	 * Name multiplication function
	 */
	public static final String MUL_FUNC_NAME = "mul";
	
	/**
	 * Name division function
	 */
	public static final String DIV_FUNC_NAME = "div";

	/**
	 * Name of the first argument of the sub function
	 */
	public static final String	SUB_FUNC_ARG_1_NAME	= "minuend";
	
	/**
	 * Name of the second argument of the sub function
	 */
	public static final String	SUB_FUNC_ARG_2_NAME	= "subtrahend";

	/**
	 * Name of the first argument of the mul function
	 */
	public static final String	MUL_FUNC_ARG_1_NAME	= "firstFactor";
	
	/**
	 * Name of the second argument of the mul function
	 */
	public static final String	MUL_FUNC_ARG_2_NAME	= "secondFactor";

	/**
	 * Name of the first argument of the div function
	 */
	public static final String	DIV_FUNC_ARG_1_NAME	= "dividend";
	
	/**
	 * Name of the second argument of the div function
	 */
	public static final String	DIV_FUNC_ARG_2_NAME	= "divisor";

	/**
	 * Name of the distance to function
	 */
	public static final Object	DISTANCE_TO_NAME	= "distanceTo";

}
