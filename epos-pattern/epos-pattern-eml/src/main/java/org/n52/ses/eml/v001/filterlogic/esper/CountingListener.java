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

package org.n52.ses.eml.v001.filterlogic.esper;

import java.util.HashMap;
import java.util.Vector;

import org.n52.epos.event.MapEposEvent;
import org.n52.epos.pattern.eml.ILogicController;
import org.n52.ses.eml.v001.Constants;

import com.espertech.esper.client.UpdateListener;
import com.espertech.esper.client.EventBean;


/**
 * Listener for the counting statement of repetitive patterns.
 * 
 * @author Thomas Everding
 *
 */
public class CountingListener implements UpdateListener {
	
	private ILogicController controller;
	
	private String inputEventName;

	private String eventName;
	
	
	/**
	 * 
	 * Constructor
	 *
	 * @param controller the esper controller
	 * @param inputEventName name of the event which is counted
	 */
	public CountingListener(ILogicController controller, String inputEventName) {
		this.controller = controller;
		this.inputEventName = inputEventName;
		
		this.initialize();
	}

	
	/**
	 * initializes this listener
	 */
	private void initialize() {
		//register counting event at esper engine
		HashMap<String, Object> eventProperties = new HashMap<String, Object>();
		eventProperties.put(MapEposEvent.START_KEY, Long.class);
		eventProperties.put(MapEposEvent.END_KEY, Long.class);
		eventProperties.put(MapEposEvent.CAUSALITY_KEY, Vector.class);
		
		this.eventName = this.inputEventName + Constants.REPETIVITE_COUNT_EVENT_SUFFIX;
		this.controller.registerEvent(this.eventName, eventProperties);
	}

	@Override
	public void update(EventBean[] newEvents, EventBean[] oldEvents) {
		if (newEvents == null) {
			//no new events
			return;
		}
		
		//handle all events
		for (EventBean bean : newEvents) {
			this.handleEvent(bean);
		}
	}
	
	
	/**
	 * handles a single new event
	 * 
	 * @param bean the new event
	 */
	private synchronized void handleEvent(EventBean bean) {
		//create new event, property values are regardless
		MapEposEvent event = new MapEposEvent(1, 1);
		
		//send event
		this.controller.sendEvent(this.eventName, event);
	}


	/**
	 * @return the inputEventName
	 */
	public String getInputEventName() {
		return this.inputEventName;
	}
	
}
