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


import org.n52.epos.event.MapEposEvent;
import org.n52.epos.rules.Rule;
import org.n52.ses.eml.v001.pattern.SelFunction;
import org.n52.ses.eml.v001.pattern.Statement;
import org.n52.ses.eml.v001.util.ThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.espertech.esper.client.UpdateListener;
import com.espertech.esper.client.EventBean;

/**
 * listener for a single esper statement
 * 
 * @author Thomas Everding
 * 
 */
public class StatementListener implements UpdateListener {

	private Statement statement;

	private EsperController controller;

	private boolean doOutput;

	private Rule rule;

	private static int instanceCount = 1;

	private int instanceNumber;

	private static final Logger logger = LoggerFactory
			.getLogger(StatementListener.class);

	/**
	 * 
	 * Constructor
	 * 
	 * @param statement
	 *            one {@link Statement}, used to configure this listener
	 * @param controller
	 *            the esper controller with the esper engine
	 */
	public StatementListener(Statement statement, EsperController controller) {
		this.statement = statement;
		this.controller = controller;

		this.initialize();
	}

	/**
	 * 
	 * Constructor
	 * 
	 * @param statement
	 *            statement one {@link Statement}, used to configure this
	 *            listener
	 * @param controller
	 *            the esper controller with the esper engine
	 * @param sub
	 *            the subscription manager
	 */
	public StatementListener(Statement statement, EsperController controller,
			Rule sub) {
		this(statement, controller);
		this.rule = sub;
	}

	/**
	 * initializes the listener
	 */
	@SuppressWarnings("unchecked")
	private void initialize() {
		// set instance number
		this.instanceNumber = instanceCount;
		instanceCount++;

		// check for output
		if (this.statement.getSelectFunction().getOutputName().equals("")) {

			// TODO (hack for static EML) fix output for StaticEMLDocument
			if (this.statement.getSelectFunction().getNewEventName().equals("")) {
				this.doOutput = true;
			} else {
				this.doOutput = false;
			}
		} else {
			this.doOutput = true;
		}

		// register new event at esper engine
		SelFunction sel = this.statement.getSelectFunction();
		if (!sel.getNewEventName().equals("")) {
			String eventName = sel.getNewEventName();

			// common attributes
			// HashMap<String, Object> eventProperties = new HashMap<String,
			// Object>();
			// eventProperties.put(MapEvent.START_KEY, Long.class);
			// eventProperties.put(MapEvent.END_KEY, Long.class);
			// eventProperties.put(MapEvent.CAUSALITY_KEY, Vector.class);

			HashMap<String, Object> eventProperties = this.controller
					.getEventProperties();

			// register every event attribute
			// TODO for string as result value maybe start debugging here
			if (sel.isSingleValueOutput()) {
				for (String key : sel.getDataTypes().keySet()) {
					if (!eventProperties.containsKey(key))
						eventProperties.put(key, sel.getDataTypes().get(key));
				}
			} else {
				// nested properties
				HashMap<String, Object> nestedMap = new HashMap<String, Object>();
				for (String key : sel.getDataTypes().keySet()) {
					nestedMap.put(key, sel.getDataTypes().get(key));
				}

				if (nestedMap.get(MapEposEvent.VALUE_KEY) instanceof HashMap) {
					// get inner map
					nestedMap = (HashMap<String, Object>) nestedMap
							.get(MapEposEvent.VALUE_KEY);
				}

				// add nested properties
				for (String key : nestedMap.keySet()) {
					if (key.equals(MapEposEvent.START_KEY)
							|| key.equals(MapEposEvent.END_KEY)
							|| key.equals(MapEposEvent.CAUSALITY_KEY)) {
						// do nothing
					} else {
						if (!eventProperties.containsKey(key))
							eventProperties.put(key, nestedMap.get(key));
					}

				}
			}

			// logger.info("registering event properties as outputs from statement: "
			// + statement.getStatement());
			//
			// for (String key : eventProperties.keySet()) {
			// logger.info("key '" + key + "' has the type '" +
			// eventProperties.get(key) + "'");
			// }
			this.controller.registerEvent(eventName, eventProperties);
		}
	}

	@Override
	public void update(EventBean[] newEvents, EventBean[] oldEvents) {
		/*
		 * new matches for the pattern received
		 * 
		 * handle every match
		 */
		if (newEvents != null) {
			for (EventBean newEvent : newEvents) {
				this.handleMatch(newEvent);
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
		UpdateHandlerThread handler = new UpdateHandlerThread(this, newEvent);

		// handle match in its own thread using a ThreadPool
		ThreadPool tp = ThreadPool.getInstance();
		tp.execute(handler);
	}

	// /**
	// * Sends the received result to the controller for output
	// *
	// * @param resultEvent the result to send
	// */
	// public synchronized void doOutput(MapEvent resultEvent) {
	// String outputName = this.statement.getSelectFunction().getOutputName();
	//
	// //load output description
	// if (this.outDescription == null) {
	// if (this.getOutDescriptionPerformed) {
	// //output description not found
	// return;
	// }
	//
	// //try to find output description
	// this.outDescription = this.controller.getOutputDescription(outputName);
	// this.getOutDescriptionPerformed = true;
	//
	// if (this.outDescription == null) {
	// //not found
	// return;
	// }
	// }
	//
	// //send output (the whole event or only the value)
	// if (this.outDescription.getDataType().equals(SupportedDataTypes.EVENT)) {
	// //send event
	// this.controller.doOutput(outputName, resultEvent);
	// }
	// else {
	// //send only value
	// this.controller.doOutput(outputName,
	// resultEvent.get(MapEvent.VALUE_KEY));
	// }
	// }

	/**
	 * Sends the received result to the controller for output
	 * 
	 * @param resultEvent
	 *            the result to send
	 */
	public synchronized void doOutput(MapEposEvent resultEvent) {
		if (logger.isDebugEnabled())
			logger.debug("performing output for statement:\n"
					+ this.statement.getStatement());

		boolean sent = false;

		// check if it is allowed to use the original message.
		// check also if it is used for GENESIS
		if (this.statement.getSelectFunction().allowsOriginalMessageAsResult()) {
			// get original message
			Object origMessage = resultEvent.getOriginalObject();
			if (origMessage != null) {
				try {
					// get message and forward to SESSubscriptionManager
					StatementListener.logger.info("sending original message");
					this.rule.filter(resultEvent);
					sent = true;
				} catch (Throwable t) {
					// any other exception occurred, sent is false -> do nothing
				}
			}
		}

		if (!sent) {
			StatementListener.logger
					.warn("An error occured while sending a NotificationMessage"
							+ " with the SubscriptionManager. It was not sent.");
		}
	}

	/**
	 * @return the statement
	 */
	public Statement getStatement() {
		return this.statement;
	}

	/**
	 * @return the controller
	 */
	public EsperController getController() {
		return this.controller;
	}

	/**
	 * @return the doOutput
	 */
	public boolean isDoOutput() {
		return this.doOutput;
	}

	/**
	 * @return the instanceNumber
	 */
	public int getInstanceNumber() {
		return this.instanceNumber;
	}
}