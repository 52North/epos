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

package org.n52.epos.engine.esper;

import java.util.Date;
import java.util.Map;

import org.n52.epos.engine.esper.util.MapEventFactory;
import org.n52.epos.event.MapEposEvent;
import org.n52.epos.filter.pattern.EventPattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.PropertyAccessException;
import com.espertech.esper.event.map.MapEventBean;



/**
 * Handles updates from a {@link StatementListener}.
 * 
 * @author Thomas Everding
 * 
 */
public class UpdateHandlerThread implements Runnable {
	
	/*
	 * Logger instance for this class
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(UpdateHandlerThread.class);
	
	private EsperController controller;
	
	private EventPattern eventPattern;
	
	private EventBean bean;
	
	private StatementListener listener;
	
	
	/**
	 * 
	 * Constructor
	 * 
	 * @param listener listener that received the update
	 * @param bean the received update
	 */
	public UpdateHandlerThread(StatementListener listener, EventBean bean) {
		this.controller = listener.getController();
		
		this.eventPattern = listener.getEventPattern();
		this.bean = bean;
		this.listener = listener;
	}
	

	@Override
	public void run() {
		if (logger.isDebugEnabled()) {
			logger.debug("Update received for pattern: " + this.eventPattern);
		}
		
		//build new event
		MapEposEvent event = null;
		
		if (this.bean instanceof MapEventBean) {
			MapEventBean selected = (MapEventBean) this.bean;
			String[] propertyNames = selected.getEventType().getPropertyNames();
			
			
			if (propertyNames.length > 1) {
				event = createEventFromComplexSelect(selected);
			}
			else if (propertyNames.length == 1){
				event = createEventFromSimpleSelect(event, propertyNames[0]);
			}
			else {
				event = createEventFromSimpleSelect(event);
			}
			
		}
		else {
			event = createEventFromSimpleSelect(event);
		}
		
		if (event == null) {
			//still nothing selected...
			UpdateHandlerThread.logger.warn("no result generated from pattern update");
			return;
		}
		/*
		 * add original message
		 */
		if (this.bean.getUnderlying() instanceof Map<?, ?>) {
			Map<?, ?> underlyingEvent = (Map<?, ?>) this.bean.getUnderlying();
			Object message = resolveOriginalObject(underlyingEvent);
			if (message != null) {
				event.put(MapEposEvent.ORIGNIAL_OBJECT_KEY, message);
			}
		}
		
		
		try {
			//send event to esper engine for further processing
			if (this.eventPattern.createsNewInternalEvent()) {
				this.controller.sendEvent(this.eventPattern.getNewEventName(), event);
			}
			
			//if output=true send event to output
			if (this.eventPattern.createsFinalOutput()) {
				if (logger.isDebugEnabled())
					logger.debug("performing output for this match");
				this.listener.doOutput(event);
			}
		}
		catch (Throwable e) {
			//log exception
			UpdateHandlerThread.logger.warn(e.getMessage());
			
			StringBuilder log = new StringBuilder();
			
			for (StackTraceElement ste : e.getStackTrace()) {
				log.append("\n" + ste.toString());
			}
			
			UpdateHandlerThread.logger.warn(log.toString());
			
			//forward exception
			throw new RuntimeException(e);
		}
	}


	private Object resolveOriginalObject(Map<?, ?> alert) {
		Object result = alert.get(MapEposEvent.ORIGNIAL_OBJECT_KEY);
		if (result != null)
			return result;
		
		String[] potentialKeys = new String[] {MapEposEvent.VALUE_KEY};
		
		for (String string : potentialKeys) {
			Object value = alert.get(string);
			if (value != null && value instanceof MapEventBean) {
				Object underlying = ((MapEventBean) value).getUnderlying();
				if (underlying instanceof Map<?, ?>) {
					result = ((Map<?, ?>) underlying).get(MapEposEvent.ORIGNIAL_OBJECT_KEY);
				}
			}
			if (result != null) 
				return result;
		}
		
		return null;
	}


	/**
	 * generates a new {@link MapEvent} from a selection with multiple values
	 * @param meBean the selected {@link MapEventBean}
	 * @return the new map event
	 */
	private MapEposEvent createEventFromComplexSelect(MapEventBean meBean) {
		//event selected
		Map<String, Object> properties = meBean.getProperties();
		return parseEventFromMap(properties);
	}


	private MapEposEvent parseEventFromMap(Map<String, Object> properties) {
		return MapEventFactory.parseFromMap(properties, this.eventPattern.createCausality());
	}


	/**
	 * generates a new MapEvent from the selection of a single value
	 * @param event the selected event bean
	 * @return the new map event
	 */
	private MapEposEvent createEventFromSimpleSelect(MapEposEvent event) {
		return this.createEventFromSimpleSelect(event, MapEposEvent.VALUE_KEY);
	}


	/**
	 * generates a new MapEvent from the selection of a single value
	 * @param event the selected event bean
	 * @param propertyName the name of the only property
	 * @return the new map event
	 */
	private MapEposEvent createEventFromSimpleSelect(MapEposEvent event, String propertyName) {
		Date now = new Date();
		
		MapEposEvent result = null;
		try {
			// no event selected, use only the property 'value'
			Object obj = this.bean.get(propertyName);
			
			if (obj == null) {
				UpdateHandlerThread.logger.info("returning null, no value for property '" + propertyName + "'");
				return null;
			}
			
			//check for timer events
			//TODO make global! epos-pattern-eml does not have to use this name
			if (obj.equals("TimerEvent")) {
				//timer event caught
				result = new MapEposEvent(now.getTime(), now.getTime());
				result.put(MapEposEvent.VALUE_KEY, now.getTime());
			}
			else {
				//handle object as usual
				result = new MapEposEvent(now.getTime(), now.getTime());
				
				result.put(MapEposEvent.VALUE_KEY, obj);
			}
		}
		catch (PropertyAccessException ex) {
			//no event property name value found
			UpdateHandlerThread.logger.warn(ex.getMessage());
		}
		catch (Throwable t) {
			UpdateHandlerThread.logger.warn(t.getMessage());
		}
		return result;
	}
}
