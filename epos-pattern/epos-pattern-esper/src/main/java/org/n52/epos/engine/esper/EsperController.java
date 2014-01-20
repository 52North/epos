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
/**
 * Part of the diploma thesis of Thomas Everding.
 * @author Thomas Everding
 */

package org.n52.epos.engine.esper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.n52.epos.event.MapEposEvent;
import org.n52.epos.filter.pattern.EventPattern;
import org.n52.epos.filter.pattern.ILogicController;
import org.n52.epos.filter.pattern.PatternFilter;
import org.n52.epos.pattern.functions.SpatialMethods;
import org.n52.epos.rules.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EPStatementException;

/**
 * central class for executing a set of esper EPL statements for a single
 * process
 * 
 * @author Thomas Everding
 * 
 */
public class EsperController implements ILogicController {

	private static final String CUSTOM_ESPER_FUNCTIONS_NAMESPACE = SpatialMethods.class.getPackage().getName() +".*";

	/*
	 * Logger instance for this class
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(EsperController.class);

	private Configuration config;

	private EPServiceProvider epService;

	private HashMap<String, StatementListener> listeners;

	private HashMap<String, CountingListener> countingListeners;

	private HashMap<String, EPStatement> epStatements;

	private Rule rule;

	private HashMap<String, Object> inputEventDataTypes;

	private Map<String,EventPattern> patterns;

	private PatternFilter patternFilter;

	/**
	 * 
	 * Constructor
	 * 
	 */
	public EsperController() {
		this.config = new Configuration();
		this.listeners = new HashMap<String, StatementListener>();
		this.countingListeners = new HashMap<String, CountingListener>();
		// this.timerListeners = new HashMap<String, TimerListener>();
		this.epStatements = new HashMap<String, EPStatement>();
		this.inputEventDataTypes = new HashMap<String, Object>();
	}

	/**
	 * 
	 * Constructor
	 * 
	 * @param sub
	 *            subscription manager
	 */
	public EsperController(Rule sub) {
		this();
		this.rule = sub;
	}

	@Override
	public PatternFilter getEventPattern() {
		return this.patternFilter;
	}
	
	/**
	 * Registers an event with a name at the engine. Nestable maps are allowed.
	 * 
	 * @param eventName
	 *            Name of the event used in the patterns (mostly the inputName).
	 * @param properties
	 *            Properties of the event. Each event is send as an HashMap.
	 *            These Properties must contain an entry for each key in the
	 *            HashMap containing the data type of the HashMap value.
	 */
	@Override
	public synchronized void registerEventWithProperties(String eventName,
			Map<String, Object> properties) {

		if (!this.config.getEventTypesNestableMapEvents()
				.containsKey(eventName)) {
			this.config.addEventType(eventName, properties);
		}
	}


	@Override
	public void initialize(PatternFilter originalFilter) throws Exception {

		if (logger.isDebugEnabled())
			logger.debug("initializing esper controller");

		this.patternFilter = originalFilter;

		patterns = new HashMap<String, EventPattern>();
		for (EventPattern ep : originalFilter.getPatterns()) {
			patterns.put(ep.getID(), ep);
		}


		for (String key : patterns.keySet()) {
			EventPattern val = patterns.get(key);

			if (val.getInputName() != null){
				registerEventWithProperties(val.getInputName(), val.getInputProperties());
			}
			
			if (val.createsNewInternalEvent()) {
				registerEventWithProperties(val.getNewEventName(), val.getOutputProperties());
			}
		}

		// register custom functions
		this.registerCustomFunctions();

		// build listeners
		for (EventPattern eventPattern : patterns.values()) {
			buildListenersForPattern(eventPattern);
		}

		// log the statements
		this.logStatements();

		// initialize esper
		this.epService = EPServiceProviderManager.getProvider(
				"ses:id:" + this.hashCode(), this.config);

		// initialize repetitive count listeners
		this.initializeCountingListeners();

		// initialize listeners
		this.initializeListeners();


		// send initial counting event
		this.sendInitialCountingEvent();
		if (logger.isDebugEnabled())
			logger.debug("esper controller is ready");
	}

	/**
	 * registers the namespace of the custom functions
	 */
	private void registerCustomFunctions() {
		// standard namespace
		this.config.addImport(CUSTOM_ESPER_FUNCTIONS_NAMESPACE);
	}

	/**
	 * logs all created statements
	 */
	private void logStatements() {
		if (logger.isInfoEnabled()) {
			StringBuilder sb = new StringBuilder();
			sb.append("Statements:");

			for (String key : this.listeners.keySet()) {
				sb.append("\n\t" + this.listeners.get(key).getEventPattern().getID()
						+": "+ key);
			}

			for (String key : this.countingListeners.keySet()) {
				sb.append("\n\t" + key);
			}

			sb.append("\n--");
			logger.info(sb.toString());
		}

	}


	/**
	 * registers debug listeners for every statement
	 */
	@SuppressWarnings("unused")
	private void buildDebugListeners() {
		// a debug listener for every statement
		EPStatement statement;

		for (String key : this.epStatements.keySet()) {
			statement = this.epStatements.get(key);

			statement.addListener(new DebugListener());
		}
	}

	/**
	 * sends an initial counting event to all counting listeners
	 */
	private void sendInitialCountingEvent() {
		CountingListener cListener;
		for (String key : this.countingListeners.keySet()) {
			cListener = this.countingListeners.get(key);

			// publish start event for counting
			Date now = new Date();
			MapEposEvent event = new MapEposEvent(now.getTime(), now.getTime());
			event.put(MapEposEvent.VALUE_KEY, 1);

			this.sendEvent(cListener.getInputEventName(), event);
		}
	}

	/**
	 * Sends an event to the esper runtime.
	 * 
	 * @param name
	 *            The name of the event.
	 * @param event
	 *            The event itself.
	 */
	@Override
	public synchronized void sendEvent(String name, MapEposEvent event) {

		// this.logger.info("event is " + ((event != null) ? "not " : "") +
		// "null!");

		if (logger.isDebugEnabled()) {
			StringBuilder sb = new StringBuilder();
			// sb.append("\n");
			sb.append("posting new event (" + System.currentTimeMillis()
					+ "; hash=" + event.hashCode() + "):");
			sb.append("\n\tname:  " + name);
			// sb.append("\n" + event.toString());
			// sb.append("\n\tevent: " + event.getClass().getName());
			// sb.append("current time: " + new Date().getTime());
			// for (String key : event.keySet()) {
			// sb.append("\n\t" + key + ": \t" + event.get(key));
			// }
			// sb.append("\n");
			EsperController.logger.debug(sb.toString());
		}

		this.epService.getEPRuntime().sendEvent(event, name);
	}

	/**
	 * builds and registers the listeners for a single pattern
	 * 
	 * @param pattern
	 *            the pattern
	 */
	private void buildListenersForPattern(EventPattern pattern) {
		if (logger.isDebugEnabled())
			logger.debug("building listener for pattern " + pattern.getID());
		/*
		 * other pattern only needs one per statement
		 */
		//TODO do we need a CountingListener for the first statement of a pattern?
			
		this.buildListener(pattern);
	}

	/**
	 * builds the listener for a single statement
	 * 
	 * @param statement
	 *            the statement
	 */
	private void buildListener(EventPattern statement) {
		StatementListener listener;
		// create new listener

		/*
		 * here we need a workaround. esper is not capable of sending first/last
		 * events of a batch-view - it instead sends all events in that view.
		 * check the EML if first or last event is selected and then register
		 * LastEventStatementListener or FirstEventStatementListener
		 */
//		if (statement.getView() != null
//				&& statement.getView().getViewName()
//						.equals(Constants.VIEW_SELECT_LAST)) {
//			listener = new LastEventStatementListener(statement, this,
//					this.subMgr);
//		} else {
		listener = new StatementListener(statement, this, this.rule);
//		}

		// add listener to map
		this.listeners.put(statement.createStringRepresentation(), listener);
	}

	/**
	 * initializes the listeners (registers them at the statements)
	 * 
	 * @throws Exception
	 */
	private synchronized void initializeListeners() throws Exception {
		// for every statement in the map
		EPStatement epStatement;
		for (String statement : this.listeners.keySet()) {

			/*
			 * register statements at engine. Try-Catch needed for better
			 * SoapFaults for users -> a statement can fail if the property was
			 * not registered in the DataTypesMap
			 */
			epStatement = null;
			try {
				epStatement = this.epService.getEPAdministrator().createEPL(
						statement);
			} catch (EPStatementException e) {
				EsperController.logger.warn(e.getMessage());

				StringBuilder sb = new StringBuilder();
				for (StackTraceElement ste : e.getStackTrace()) {
					sb.append("\n" + ste.toString());
				}
				EsperController.logger.warn(sb.toString());

				if (e.getMessage().contains("Implicit conversion")) {
					throw new Exception(
							"Registration of statement failed. Looks like your observerd property was"
									+ " not registered by any publisher.\r\n"
									+ "If you used \"value\" in your Guard, please use \"doubleValue\" or \"stringValue\" instead.\r\n"
									+ "Standard data types:\r\n"
									+ "sensorID = String\r\n"
									+ "stringValue = String\r\n"
									+ "doubleValue = double\r\n"
									+ "startTime = long\r\n"
									+ "endTime = long\r\n"
									+ "observedProperty = String\r\n"
									+ "foiID = String");
				}
				// else throw initial exception
				throw new Exception(
						"Error in esper statement, possible EML error: '"
								+ e.getMessage() + "'", e);
			}

			// register listener at esper statement
			if (epStatement != null) {
				epStatement.addListener(this.listeners.get(statement));
				// store epStatements
				this.epStatements.put(statement, epStatement);
			}

		}
	}

	/**
	 * initializes the counting listeners
	 */
	private synchronized void initializeCountingListeners() {
		// for every statement in the map
		EPStatement epStatement;
		CountingListener cListener;
		for (String statement : this.countingListeners.keySet()) {
			cListener = this.countingListeners.get(statement);

			// register statement at engine
			epStatement = this.epService.getEPAdministrator().createEPL(
					statement);

			// register listener at esper statement
			epStatement.addListener(cListener);

			// store epStatements
			this.epStatements.put(statement, epStatement);
		}
	}

	/**
	 * Searches for the data type of a property.
	 * 
	 * @param fullPropertyName
	 *            the full EML name of the property
	 * 
	 * @return a java.lang.Class or a Map containing Classes and/or further Maps
	 */
	@Override
	public Object getDatatype(String fullPropertyName) {
		// split into event and property name part
		String eventName;
		String propertyName;
		int lastSlash = fullPropertyName.lastIndexOf("/");

		propertyName = fullPropertyName.substring(lastSlash + 1);

		int lastButOneSlash = fullPropertyName.substring(0, lastSlash)
				.lastIndexOf("/");

		if (lastButOneSlash <= 0) {
			// full name looks like "event/value"
			eventName = fullPropertyName.substring(0, lastSlash);
		} else {
			// full name looks like "event/nestedEvent/value", we need
			// nestedEvent
			eventName = fullPropertyName.substring(lastButOneSlash + 1,
					lastSlash);
		}

		// check all inputs first
		// for (InputDescription descr : this.inputDescriptions) {
		// if (descr.getName().equals(eventName)) {
		// return DataTypeNameToClassConverter.convert(descr.getDataType());
		// }
		// }

		// then check property Restrictions
//		for (EventPattern pat : this.patterns) {
//			if (pat.getRelatedInputPatterns() == null || pat.getRelatedInputPatterns().isEmpty()) {
//				PatternSimple pats = (PatternSimple) pat;
//				for (PropRestriction propRes : pats.getPropertyRestrictions()) {
//					if (propRes.getName().equals(propertyName)) {
//						if (propRes.getValue().equals(
//								"\"" + MapEposEvent.DOUBLE_VALUE_KEY + "\"")) {
//							return Number.class;
//						}
//					}
//				}
//			}
//		}

		// then check all patterns
		for (EventPattern pat : this.patterns.values()) {
			if (pat.getNewEventName().equals(eventName)) {
				// this select function defines the data type
				return pat.getInputProperties().get(propertyName);
			}
		}
		return null;
	}

	/**
	 * get a map containing all data types of an event
	 * 
	 * @param eventName
	 *            name of the event (only the event name)
	 * 
	 * @return a map containing all data types of an event or the class of the
	 *         data type if the event is an input event
	 */
	@Override
	public Object getEventDatatype(String eventName) {
		// check input data types
		if (this.inputEventDataTypes.containsKey(eventName)) {
			// event is process input
			return this.inputEventDataTypes.get(eventName);
		}

		// check pattern outputs
		// logger.info("check pattern outputs (for event name '" + eventName +
		// "'), no. of patterns: " + this.parser.getPatterns().size());
		for (EventPattern pat : this.patterns.values()) {
			if (pat.getNewEventName().equals(eventName)) {
				// this select function defines the data type
				return pat.getInputProperties();
			}
		}

		// nothing found
		EsperController.logger
				.warn("No data type description found for event '" + eventName
						+ "'.");
		return null;
	}

	// /**
	// *
	// * @param name name of the output
	// * @return the {@link OutputDescription} oft an output or
	// <code>null</code> if there is no output for the
	// * name
	// */
	// public OutputDescription getOutputDescription(String name) {
	// // for (OutputDescription descr : this.outputDescriptions) {
	// // if (descr.getName().equals(name)) {
	// // //output description found
	// // return descr;
	// // }
	// // }
	// logger.severe("No description for output '" + name + "' found.");
	// return null;
	// }

	/**
	 * Performs the output of a value.
	 * 
	 * @param outputName
	 *            the name of the output
	 * @param value
	 *            the value to send
	 */
	public void doOutput(String outputName, Object value) {
		// this.process.doOutput(outputName, value);
	}

	// /**
	// * @return the epService
	// */
	// public EPServiceProvider getEpService() {
	// return this.epService;
	// }

	// /**
	// * test main
	// *
	// * @param args
	// */
	// public static void main(String[] args) {
	// /*
	// * Initialize the Logger (Config file is located in folder 'xml' with name
	// 'log4j.xml')
	// */
	// org.apache.log4j.xml.DOMConfigurator.configureAndWatch("xml" +
	// java.io.File.separator + "log4j.xml",
	// 60 * 1000);
	//
	// EsperController c = new EsperController(null);
	// c.test();
	// }

	/**
	 * Returns the newEventName of a given pattern
	 * 
	 * @param patternID
	 *            id of the pattern
	 * @param selectFunctionNumber
	 *            number of the select function which results are counted
	 * 
	 * @return the newEventName of the pattern
	 */
	@SuppressWarnings("all")
	public String getNewEventName(String patternID, int selectFunctionNumber) {
		// get all patterns from parser
		return this.patterns.get(patternID).getNewEventName();
	}

	/**
	 * 
	 * @param statement
	 *            the esper statement
	 * 
	 * @return the registered statement object for the statement or null if not
	 *         present
	 */
	public EPStatement getEPStatement(String statement) {
		if (this.epStatements.containsKey(statement)) {
			return this.epStatements.get(statement);
		}
		return null;
	}

	/**
	 * Removes all statements and listeners from the esper engine,
	 */
	public void removeFromEngine() {
		for (EPStatement epst : this.epStatements.values()) {
			if (logger.isDebugEnabled())
				logger.debug("Removing statement: \n\t" + epst.getText());
			epst.removeAllListeners();
			epst.destroy();
		}

		/* destroy this complete engine - its independent */
		this.epService.destroy();
	}

}
