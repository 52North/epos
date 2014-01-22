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
