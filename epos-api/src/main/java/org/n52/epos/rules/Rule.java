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
package org.n52.epos.rules;


import org.n52.epos.event.EposEvent;
import org.n52.epos.filter.ActiveFilter;
import org.n52.epos.filter.EposFilter;
import org.n52.epos.filter.PassiveFilter;
import org.n52.epos.pattern.PatternEngine;

public interface Rule {
	
	/**
	 * Adds another filter which shall be considered
	 * when {@link #filter(EposEvent)} is called.
	 * 
	 * @param filter an additional {@link ActiveFilter} instance
	 */
	public void addActiveFilter(ActiveFilter filter);
	
	/**
	 * Sets the {@link PassiveFilter}. A {@link PassiveFilterAlreadyPresentException}
	 * shall be thrown when a {@link PassiveFilter} has
	 * been set before.
	 * 
	 * @param filter
	 * @throws PassiveFilterAlreadyPresentException
	 */
	public void setPassiveFilter(PassiveFilter filter) throws PassiveFilterAlreadyPresentException;
	
	/**
	 * @return the registered {@link PassiveFilter} instance
	 * or null if non is registered.
	 */
	public PassiveFilter getPassiveFilter();
	
	/**
	 * @return true if a {@link PassiveFilter} is registered
	 * in an instance of the Rule.
	 */
	public boolean hasPassiveFilter();

	/**
	 * @return the listener which shall be called when
	 * all {@link EposFilter} match a certain event.
	 */
	public RuleListener getRuleListener();
	
	/**
	 * evaluate all registered {@link ActiveFilter}
	 * instances against the event. If all pass, the {@link RuleListener}
	 * attached to this Rule shall called.
	 * {@link PassiveFilter} instances shall not be taken into consideration,
	 * as they shall match only from inside a {@link PatternEngine}.
	 * 
	 * @param event the event to filter
	 */
	public void filter(EposEvent event);

	/**
	 * same as {@link #filter(EposEvent)}, but the {@link RuleListener}
	 * shall be invoked through {@link RuleListener#onMatchingEvent(EposEvent, Object)}.
	 * 
	 * @param event the event to filter
	 * @param desiredOutputToConsumer the desired output to provide
	 * to the consumer
	 */
	void filter(EposEvent event, Object desiredOutputToConsumer);
	
	
	
}
