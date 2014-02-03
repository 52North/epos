/**
 * Copyright (C) 2013-2014 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 as published
 * by the Free Software Foundation.
 *
 * If the program is linked with libraries which are licensed under one of
 * the following licenses, the combination of the program with the linked
 * library is not considered a "derivative work" of the program:
 *
 *     - Apache License, version 2.0
 *     - Apache Software License, version 1.0
 *     - GNU Lesser General Public License, version 3
 *     - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *     - Common Development and Distribution License (CDDL), version 1.0
 *
 * Therefore the distribution of the program linked with libraries licensed
 * under the aforementioned licenses, is permitted by the copyright holders
 * if the distribution is compliant with both the GNU General Public
 * icense version 2 and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 */
package org.n52.epos.engine.esper.util;

import java.util.Date;
import java.util.Map;
import java.util.Vector;

import org.n52.epos.event.MapEposEvent;

import com.espertech.esper.event.map.MapEventBean;


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
					if (map.get(key) instanceof Map<?, ?>) {
						MapEposEvent ancestorEvent = parseFromMap((Map<String, Object>) map.get(key), createCausality);
						event.addCausalAncestor(ancestorEvent);
					}
					else if (map.get(key) instanceof MapEposEvent) {
						event.addCausalAncestor((MapEposEvent) map.get(key));
					}
					else if (map.get(key) instanceof MapEventBean) {
						MapEventBean ancestorBean = (MapEventBean) map.get(key);
						event.addCausalAncestor(parseFromMap(ancestorBean.getProperties(), createCausality));
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
