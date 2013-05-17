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
package org.n52.epos.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import org.n52.epos.engine.rules.RuleInstance;
import org.n52.epos.event.EposEvent;
import org.n52.epos.filter.PassiveFilter;
import org.n52.epos.pattern.PatternEngine;
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
	
	private List<RuleInstance> rules = new ArrayList<RuleInstance>();
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
		boolean insertIntoPatternEngineRequired = false;
		
		for (RuleInstance rule : this.rules) {
			if (!rule.hasPassiveFilter()) {
				rule.filter(event);
			} else {
				insertIntoPatternEngineRequired = true;
			}
		}
		
		if (insertIntoPatternEngineRequired && this.patternEngine != null) {
			this.patternEngine.insertEvent(event);
		}
	}
	
}
