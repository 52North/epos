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
package org.n52.epos.pattern.eml;


import java.util.Map;

import net.opengis.eml.x001.EMLDocument.EML;

import org.n52.epos.event.MapEposEvent;



/**
 * interface for EML logic controllers (e.g. esper controller)
 *
 */
public interface ILogicController {

	
	/**
	 * Initializes the controller
	 * 
	 * @param eml the EML to execute
	 * @param unitConverter the unit converter
	 * @throws Exception 
	 */
	void initialize(EML eml) throws Exception;

	/**
	 * send a new event to the engine
	 * 
	 * @param inputName the name of the event type
	 * @param event the new event
	 */
	void sendEvent(String inputName, MapEposEvent event);

	/**
	 * registers a new event type
	 * 
	 * @param eventName name of the new event type
	 * @param eventProperties map containing the names and the types of the event properties
	 */
	void registerEvent(String eventName, Map<String, Object> eventProperties);

	/**
	 * get a map containing all data types of an event
	 * 
	 * @param eventName name of the event (only the event name)
	 * 
	 * @return a map containing all data types of an event 
	 * or the class of the data type if the event is an input event
	 */
	Object getEventDatatype(String eventName);

	/**
	 * Searches for the data type of a property.
	 * 
	 * @param fullPropertyName the full EML name of the property
	 * 
	 * @return a java.lang.Class or a Map containing Classes and/or further Maps
	 */
	Object getDatatype(String fullPropertyName);

	/**
	 * Returns the newEventName of a given pattern
	 * 
	 * @param patternID id of the pattern
	 * @param selectFunctionNumber number of the select function which results are counted
	 * 
	 * @return the newEventName of the pattern
	 */
	String getNewEventName(String patternID, int selectFunctionNumber);
	
	void removeFromEngine();
}
