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
package org.n52.epos.engine.esper;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.n52.epos.engine.esper.concurrent.IConcurrentNotificationHandler;
import org.n52.epos.engine.esper.concurrent.NamedThreadFactory;
import org.n52.epos.event.EposEvent;
import org.n52.epos.event.MapEposEvent;
import org.n52.epos.filter.PassiveFilter;
import org.n52.epos.filter.pattern.ILogicController;
import org.n52.epos.filter.pattern.PatternFilter;
import org.n52.epos.pattern.PatternEngine;
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
	private IConcurrentNotificationHandler queueWorker;
	private ExecutorService messageProcessingPool;
	private boolean controlledConcurrentUse;

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

	}


	public void insertEvent(EposEvent message) {
		logger.info("Inserting Event: {}", message);
		if (message instanceof MapEposEvent) {
			for (Rule rule : this.esperControllers.keySet()) {
				ILogicController controller = this.esperControllers.get(rule);
				controller.sendEvent(controller.getEventPattern().getInputStreamName(),
						(MapEposEvent) message);
			}		
		}
	}


	public synchronized void registerRule(Rule rule) {

		if (!rule.hasPassiveFilter()) {
			throw new IllegalStateException("FilterEngine needs a PassiveFilter.");
		}

		PassiveFilter originalFilter = rule.getPassiveFilter();

		ILogicController controller = null;
		if (originalFilter instanceof PatternFilter) {
			/*
			 * parse the EML and get the patterns Also UNITCONVERSION is done
			 * here now as a first step
			 */
			try {
				Constructor<?> con = this.controllerClass
						.getConstructor(Rule.class);
				controller = (ILogicController) con.newInstance(rule);

				controller.initialize((PatternFilter) originalFilter);
				logger.info("Registering EML Controller for external input stream '"
						+ controller.getEventPattern().getInputStreamName() + "'");
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


	@Override
	public synchronized void removeRule(Rule rule) {
		ILogicController controller = this.esperControllers.get(rule);
		if (controller != null) {
			controller.removeFromEngine();
		}
		
		this.esperControllers.remove(rule);
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
