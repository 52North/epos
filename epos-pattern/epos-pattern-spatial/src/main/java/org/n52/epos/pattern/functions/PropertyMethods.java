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
package org.n52.epos.pattern.functions;

import java.util.Map;

/**
 * Provides methods to check properties
 */
public class PropertyMethods {
	
	/**
	 * check if a property exists
	 * 
	 * @param event the event that should contain the property
	 * @param propertyName the property name
	 * 
	 * @return true, if the event contains the property
	 */
	@SuppressWarnings("rawtypes")
	public static boolean propertyExists(Object event, Object propertyName) {
		Map eventMap = (Map) event;
		if (eventMap.containsKey(propertyName.toString())) {
			if (eventMap.get(propertyName.toString()) == null) {
				return false;
			}
		}
		else {
			return false;
		}
		return true;
	}
	

}
