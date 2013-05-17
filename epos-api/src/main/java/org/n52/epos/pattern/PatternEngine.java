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
package org.n52.epos.pattern;

import org.n52.epos.event.EposEvent;
import org.n52.epos.filter.PassiveFilter;
import org.n52.epos.rules.Rule;

public interface PatternEngine {

	/**
	 * Inserts an {@link EposEvent} into the pattern
	 * matching mechanism. It shall pass out when the {@link PassiveFilter}
	 * of a {@link Rule} matches. The underlying
	 * pattern matching backend must ensure that
	 * {@link RuleInstance#filter(EposEvent)} is called in that case.
	 * 
	 * @param event the event object
	 */
	public void insertEvent(EposEvent event);
	
	/**
	 * Add a {@link Rule} into the engine which shall
	 * be evaluated on every call of {@link #insertEvent(EposEvent)}.
	 * 
	 * @param rule the rule
	 * @throws NoPassiveFilterPresentException if the rule does not contain
	 * an instance of {@link PassiveFilter} (as an engine only supports that
	 * kind if filters)
	 */
	public void registerRule(Rule rule) throws NoPassiveFilterPresentException;

}
