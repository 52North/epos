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
package org.n52.epos.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import org.n52.epos.engine.rules.RuleInstance;
import org.n52.epos.event.EposEvent;
import org.n52.epos.filter.PassiveFilter;
import org.n52.epos.pattern.NoPassiveFilterPresentException;
import org.n52.epos.pattern.PatternEngine;
import org.n52.epos.rules.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The central component of the EPOS library.
 * This class is a singleton, the instance can be
 * retrieved through {@link #getInstance()}.
 * 
 * @author matthes rieke
 *
 */
public class EposEngine {

	private static EposEngine instance;
	private static final Logger logger = LoggerFactory.getLogger(EposEngine.class);
	
	private List<Rule> rules = new ArrayList<Rule>();
	private PatternEngine patternEngine;
	
	private EposEngine() {
		ServiceLoader<PatternEngine> loader = ServiceLoader.load(PatternEngine.class);
		
		for (PatternEngine patternEngine : loader) {
			this.patternEngine = patternEngine;
		}
		
		if (this.patternEngine == null) {
			logger.error("No instance of {} available. Provide one through the ServiceLoader " +
					"mechanism. Rules with instances of {} will not match!", 
					PatternEngine.class.getCanonicalName(),
					PassiveFilter.class.getCanonicalName());
		}
	}
	
	public static synchronized EposEngine getInstance() {
		if (instance == null) {
			instance = new EposEngine();
		}
		return instance;
	}
	
	/**
	 * Evaluate an {@link EposEvent} against
	 * the registered {@link RuleInstance}s.
	 * 
	 * @param event the event object
	 */
	public void filterEvent(EposEvent event) {
		logger.debug("Received new Event: {}", event);
		boolean insertIntoPatternEngineRequired = false;
		
		synchronized (this) {
			for (Rule rule : this.rules) {
				if (!rule.hasPassiveFilter()) {
					rule.filter(event);
				} else {
					insertIntoPatternEngineRequired = true;
				}
			}	
		}
		
		if (insertIntoPatternEngineRequired && this.patternEngine != null) {
			logger.debug("Pusing event into PatternEngine instance.");
			this.patternEngine.insertEvent(event);
		}
	}


	/**
	 * Add a new rule to the engine.
	 * 
	 * @param newRule the new rule
	 */
	public synchronized void registerRule(Rule newRule) {
		this.rules.add(newRule);
		
		if (newRule.hasPassiveFilter() && this.patternEngine != null) {
			try {
				this.patternEngine.registerRule(newRule);
			} catch (NoPassiveFilterPresentException e) {
				logger.warn(e.getMessage(), e);
			}
		}
		
		logger.debug("Added new Rule: {}", newRule);
	}

	
	public synchronized void unregisterRule(Rule rule) {
		this.rules.remove(rule);
		
		if (rule.hasPassiveFilter() && this.patternEngine != null) {
			this.patternEngine.removeRule(rule);
		}
		
		logger.debug("Removed Rule: {}", rule);
	}
	
	/**
	 * release all resources
	 */
	public void shutdown() {
		if (this.patternEngine != null)
			this.patternEngine.shutdown();
	}

}
