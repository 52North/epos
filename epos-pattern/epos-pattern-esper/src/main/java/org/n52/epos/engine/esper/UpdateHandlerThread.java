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
					logger.debug("performing output for statement: {}", this.eventPattern.createStringRepresentation());
				this.listener.doOutput(event);
			}
		}
		catch (Exception e) {
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
		catch (Exception t) {
			UpdateHandlerThread.logger.warn(t.getMessage());
		}
		return result;
	}
}
