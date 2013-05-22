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
package org.n52.epos.engine.esper;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.n52.epos.engine.esper.concurrent.IConcurrentNotificationHandler;
import org.n52.epos.event.EposEvent;
import org.n52.epos.event.MapEposEvent;
import org.n52.epos.filter.PassiveFilter;
import org.n52.epos.pattern.PatternEngine;
import org.n52.epos.pattern.eml.EMLPatternFilter;
import org.n52.epos.pattern.eml.ILogicController;
import org.n52.epos.pattern.eml.util.NamedThreadFactory;
import org.n52.epos.rules.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Matthes Rieke <m.rieke@uni-muenster.de>
 * 
 */
public class EsperFilterEngine implements PatternEngine {

	private Map<Rule, ILogicController> esperControllers;
	private static final Logger logger = LoggerFactory
			.getLogger(EsperFilterEngine.class);
	private Class<?> controllerClass;
//	private boolean insertionSuspended;
//	private Random random;
	private IConcurrentNotificationHandler queueWorker;
	private ExecutorService messageProcessingPool;
	private boolean controlledConcurrentUse;
//	private boolean performanceTesting = false;
//	private boolean testingSimulateLatency = true;
//	private boolean testingThrowRandomExceptions = false;

	/**
	 * 
	 * Constructor
	 * 
	 * @param converter
	 *            unit converter
	 * @param logger
	 *            logger
	 */
	public EsperFilterEngine() {
		if (logger.isInfoEnabled())
			logger.info("Init EsperFilterEngine...");

		this.messageProcessingPool = Executors.newFixedThreadPool(4,
				new NamedThreadFactory("FilterEngineProcessingPool"));

		ServiceLoader<ILogicController> loader = ServiceLoader
				.load(ILogicController.class);
		for (ILogicController ilc : loader) {
			this.controllerClass = ilc.getClass();
			break;
		}

		if (this.controllerClass == null) {
			throw new IllegalStateException(
					"Could not find an implementation for "
							+ ILogicController.class.getName());
		}

		// multiple runtime objects needed for pattern management
		this.esperControllers = new ConcurrentHashMap<Rule, ILogicController>();

		/*
		 * check if we have enrichment activated
		 */
		// String enrich = conf.getPropertyForKey(
		// ConfigurationRegistry.USE_ENRICHMENT).toString();
		// if (Boolean.parseBoolean(enrich.trim())) {
		// // DUMMY - USE REFLECTIONS HERE in future
		// this.enricher = new AIXMEnrichment();
		// }

		/*
		 * concurrent fifo worker implementation. first, check if we use
		 * concurrency monitoring
		 */
		// TODO use config
		this.controlledConcurrentUse = true;

		if (logger.isInfoEnabled())
			logger.info("Concurrent Message Processing? {}",
					this.controlledConcurrentUse);

		if (this.controlledConcurrentUse) {
			ServiceLoader<IConcurrentNotificationHandler> concurrentLoader = ServiceLoader
					.load(IConcurrentNotificationHandler.class);
			for (IConcurrentNotificationHandler icnh : concurrentLoader) {
					this.queueWorker = icnh;
					break;
			}

//			this.queueWorker.setPollListener(this);
			
			//TODO use config
			this.queueWorker.setTimeout(5000);
			this.queueWorker.setUseIntelligentTimeout(false);

			this.queueWorker.startWorking();
		}

//		if (this.performanceTesting) {
//			this.random = new Random();
//		}

	}


	public void insertEvent(EposEvent message) {
		if (message instanceof MapEposEvent) {
			for (Rule rule : this.esperControllers.keySet()) {
				ILogicController controller = this.esperControllers.get(rule);
				controller.sendEvent(controller.getInputStreamName(),
						(MapEposEvent) message);
			}		
		}
	}


	public void registerRule(Rule rule) {

		if (!rule.hasPassiveFilter()) {
			throw new IllegalStateException("FilterEngine needs a PassiveFilter.");
		}

		PassiveFilter originalFilter = rule.getPassiveFilter();

		ILogicController controller = null;
		if (originalFilter instanceof EMLPatternFilter) {
			/*
			 * parse the EML and get the patterns Also UNITCONVERSION is done
			 * here now as a first step
			 */
			try {
				Constructor<?> con = this.controllerClass
						.getConstructor(Rule.class);
				controller = (ILogicController) con.newInstance(rule);

				EMLPatternFilter emlFilter = (EMLPatternFilter) originalFilter;
				controller.initialize(emlFilter.getEml());
				logger.info("Registering EML Controller for external input stream '"
						+ controller.getInputStreamName() + "'");
				this.esperControllers.put(rule, controller);
			} catch (Exception e) {
				logger.warn(e.getMessage(), e);
				throw new RuntimeException(e);
			}
			
		}

//		else if (filter instanceof EPLFilterImpl) {
//			EPLFilterImpl epl = (EPLFilterImpl) filter;
//
//			controller = new EPLFilterController(ism, epl);
//			streamName = epl.getExternalInputName();
//		}

	}

	public void shutdown() {
		logger.info("Shutting down EsperFilterEngine...");

		if (this.controlledConcurrentUse) {
			this.queueWorker.stopWorking();
			this.queueWorker.notifyOnDataAvailability(null);
			this.messageProcessingPool.shutdownNow();
		}

		for (Rule espc : this.esperControllers.keySet()) {
			this.esperControllers.get(espc).removeFromEngine();
		}
	}



}
