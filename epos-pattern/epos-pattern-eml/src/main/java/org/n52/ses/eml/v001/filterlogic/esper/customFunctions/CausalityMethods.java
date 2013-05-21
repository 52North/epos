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

import java.util.Vector;

import org.n52.epos.event.MapEposEvent;



/**
 * provides methods to perform causality test
 * 
 * @author Thomas Everding
 *
 */
public class CausalityMethods {
	
	/**
	 * Checks if an event is a causal ancestor of another event
	 * 
	 * @param event the possible ancestor
	 * @param causalVector the causal vector of the other event
	 * 
	 * @return <code>true</code> if the event is a causal ancestor
	 */
	@SuppressWarnings("unchecked")
	public static boolean isCausalAncestorOf(Object event, Object causalVector) {
		Vector<Object> vec = (Vector<Object>) causalVector;
		
		MapEposEvent eventMap = (MapEposEvent) event;
		String id = eventMap.get(MapEposEvent.SENSORID_KEY).toString() + eventMap.get(MapEposEvent.START_KEY).toString();
		
		String testID;
		MapEposEvent e;
		for (Object o : vec) {
			e = (MapEposEvent) o;
			testID = e.get(MapEposEvent.SENSORID_KEY).toString() + e.get(MapEposEvent.START_KEY).toString();
			
			if (testID.equals(id)) {
				return true;
			}
		}

		return false;
	}
	
	
	/**
	 * Checks if an event is a causal ancestor of another event
	 * 
	 * @param event the possible ancestor
	 * @param causalVector the causal vector of the other event
	 * 
	 * @return <code>true</code> if the event is not a causal ancestor
	 */
	public static boolean isNotCausalAncestorOf(Object event, Object causalVector) {
		return !isCausalAncestorOf(event, causalVector);
	}
}
