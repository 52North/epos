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
 * This program is free software; you can redistribute and/or modify it under
 * the terms of the GNU General Public License version 2 as published by the
 * Free Software Foundation.
 *
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied
 * WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program (see gnu-gpl v2.txt). If not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
 * visit the Free Software Foundation web page, http://www.fsf.org.
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
