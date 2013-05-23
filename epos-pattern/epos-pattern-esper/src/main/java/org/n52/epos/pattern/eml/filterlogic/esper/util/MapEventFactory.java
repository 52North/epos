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
package org.n52.epos.pattern.eml.filterlogic.esper.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.n52.epos.event.MapEposEvent;


/**
 * Creates MapEvent from different inputs
 * 
 * @author Thomas Everding
 *
 */
public class MapEventFactory {
	
//	private static Logger logger = Logger.getLogger(MapEventFactory.class.getName());
	
	/**
	 * Parses a {@link MapEvent} from a map.
	 * 
	 * @param map the map to parse
	 * @param createCausality indication if causality shall be parsed
	 * 
	 * @return a new {@link MapEvent} with the content of the map
	 */
	@SuppressWarnings("unchecked")
	public static MapEposEvent parseFromMap (Map<String, Object> map, boolean createCausality) {
//		//log all keys
//		StringBuilder sb = new StringBuilder();
//		sb.append("available keys in map: ");
//		for (String key : map.keySet()) {
//			sb.append("\n\t" + key);
//		}
//		logger.info(sb.toString());
		
		//parse time stamps
		long start;
		long end;
		if (map.containsKey(MapEposEvent.START_KEY)) {
			start = Long.parseLong(map.get(MapEposEvent.START_KEY).toString());
		}
		else {
			start = new Date().getTime();
		}
		
		if (map.containsKey(MapEposEvent.END_KEY)) {
			end = Long.parseLong(map.get(MapEposEvent.END_KEY).toString());
			if (start > end) {
				end = start;
			}
		}
		else {
			end = start;
		}
		
		//create result
		MapEposEvent event = new MapEposEvent(start, end);
		
		//copy content
		for (String key : map.keySet()) {
			if (key.equals(MapEposEvent.START_KEY) || key.equals(MapEposEvent.END_KEY)) {
				//already copied
			}
			else if (key.equals(MapEposEvent.THIS_KEY)) {
				//ignore to prevent recursions
			}
			else if (key.equals(MapEposEvent.CAUSALITY_KEY)) {
				if (createCausality) {
					//copy causality
					Vector<MapEposEvent> causality = (Vector<MapEposEvent>) map.get(key);
					
					for (MapEposEvent ancestor : causality) {
						event.addCausalAncestor(ancestor);
					}
				}
			}
			else if (key.equals(MapEposEvent.CAUSAL_ANCESTOR_1_KEY) || key.equals(MapEposEvent.CAUSAL_ANCESTOR_2_KEY)) {
				if (createCausality) {
					//add causal ancestors
					if (map.get(key) instanceof HashMap<?, ?>) {
						MapEposEvent ancestorEvent = parseFromMap((Map<String, Object>) map.get(key), createCausality);
						event.addCausalAncestor(ancestorEvent);
					}
					else if (map.get(key) instanceof MapEposEvent) {
						event.addCausalAncestor((MapEposEvent) map.get(key));
					}
				}
			}
			else {
				//fallback / usual: just put it into the result
				event.put(key, map.get(key));
//					logger.info("putting key into MapEvent: " + key);
			}
		}
		return event;
	}

}
