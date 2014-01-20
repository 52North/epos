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


import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.epos.pattern.CustomStatementEvent;
import org.n52.epos.engine.esper.concurrent.ThreadPool;
import org.n52.epos.engine.esper.util.EventModelGenerator;
import org.n52.epos.event.MapEposEvent;
import org.n52.epos.filter.pattern.EventPattern;
import org.n52.epos.filter.pattern.PatternFilter;
import org.n52.epos.rules.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.espertech.esper.client.UpdateListener;
import com.espertech.esper.client.EventBean;

/**
 * listener for a single esper pattern
 * 
 * @author Thomas Everding
 * 
 */
public class StatementListener implements UpdateListener {

	private EventPattern pattern;

	private EsperController controller;

	private Rule rule;

	private static int instanceCount = 1;

	private int instanceNumber;

	private static final Logger logger = LoggerFactory
			.getLogger(StatementListener.class);

	/**
	 * 
	 * Constructor
	 * 
	 * @param pattern
	 *            one {@link Statement}, used to configure this listener
	 * @param controller
	 *            the esper controller with the esper engine
	 */
	public StatementListener(EventPattern statement, EsperController controller) {
		this.pattern = statement;
		this.controller = controller;

		this.initialize();
	}

	/**
	 * 
	 * Constructor
	 * 
	 * @param pattern
	 *            pattern one {@link Statement}, used to configure this
	 *            listener
	 * @param controller
	 *            the esper controller with the esper engine
	 * @param rule
	 *            the subscription manager
	 */
	public StatementListener(EventPattern statement, EsperController controller,
			Rule rule) {
		this(statement, controller);
		this.rule = rule;
	}

	/**
	 * initializes the listener
	 */
	private void initialize() {
		// set instance number
		this.instanceNumber = instanceCount;
		instanceCount++;
	}

	@Override
	public void update(EventBean[] newEvents, EventBean[] oldEvents) {
		/*
		 * new matches for the pattern received
		 * 
		 * handle every match
		 */
		if (newEvents != null && newEvents.length > 0) {
			for (EventBean newEvent : newEvents) {
				this.handleMatch(newEvent);
			}
			
			if (this.pattern.hasCustomStatementEvents()) {
				for (CustomStatementEvent cse : this.pattern.getCustomStatementEvents()) {
					cse.eventFired(newEvents, this.rule);
				}
			}
		}
	}

	/**
	 * handles a single pattern match
	 * 
	 * @param newEvent
	 *            the EventBean representing the match
	 */
	protected synchronized void handleMatch(EventBean newEvent) {
		
		logger.debug("Statement {} matched for Event '{}'",
				this.pattern, newEvent.getUnderlying().toString());
		
		UpdateHandlerThread handler = new UpdateHandlerThread(this, newEvent);
		
		//handle match in its own thread using a ThreadPool
		ThreadPool tp = ThreadPool.getInstance();
		tp.execute(handler);
	}
	
	
//	/**
//	 * Sends the received result to the controller for output
//	 * 
//	 * @param resultEvent the result to send
//	 */
//	public synchronized void doOutput(MapEvent resultEvent) {
//		String outputName = this.statement.getSelectFunction().getOutputName();
//		
//		//load output description
//		if (this.outDescription == null) {
//			if (this.getOutDescriptionPerformed) {
//				//output description not found
//				return;
//			}
//			
//			//try to find output description
//			this.outDescription = this.controller.getOutputDescription(outputName);
//			this.getOutDescriptionPerformed = true;
//			
//			if (this.outDescription == null) {
//				//not found
//				return;
//			}
//		}
//		
//		//send output (the whole event or only the value)
//		if (this.outDescription.getDataType().equals(SupportedDataTypes.EVENT)) {
//			//send event
//			this.controller.doOutput(outputName, resultEvent);
//		}
//		else {
//			//send only value
//			this.controller.doOutput(outputName, resultEvent.get(MapEvent.VALUE_KEY));
//		}
//	}
	
	/**
	 * Sends the received result to the controller for output
	 * 
	 * @param resultEvent
	 *            the result to send
	 */
	public synchronized void doOutput(MapEposEvent resultEvent) {
		if (logger.isDebugEnabled())
			logger.debug("performing output for pattern:\n"
					+ this.pattern);

		// check if it is allowed to use the original message.
		// check also if it is used for GENESIS
		if (this.pattern.usesOriginalEventAsOutput()) {
			StatementListener.logger.info("trying to send original message as output");
			// get original message
			Object origMessage = resultEvent.getOriginalObject();
			if (origMessage != null) {
				// get message and forward to SESSubscriptionManager
				StatementListener.logger.info("sending original message");
				this.rule.filter(resultEvent);
			}
			else {
				logger.warn("Event did not contain the original message!");
			}
		}

		else {
			XmlObject eventDoc = null;

			// generate Event model
			StatementListener.logger.info("generating OGC Event model output");
			EventModelGenerator eventGen = new EventModelGenerator(resultEvent);

			if (this.rule.getPassiveFilter() instanceof PatternFilter) {
				XmlObject xo;
				try {
					xo = XmlObject.Factory.parse(((PatternFilter) this.rule.getPassiveFilter()).serialize().toString());
					eventDoc = eventGen.generateEventDocument(xo);
				} catch (XmlException e) {
					logger.warn(e.getMessage(), e);
				}
			}
			
			if (eventDoc == null) {
				eventDoc = eventGen.generateEventDocument();
			}
			
			resultEvent.setOriginalObject(eventDoc);

			this.rule.filter(resultEvent, eventDoc);
		}
	}

	/**
	 * @return the pattern
	 */
	public EventPattern getEventPattern() {
		return this.pattern;
	}

	/**
	 * @return the controller
	 */
	public EsperController getController() {
		return this.controller;
	}

	/**
	 * @return the instanceNumber
	 */
	public int getInstanceNumber() {
		return this.instanceNumber;
	}
}