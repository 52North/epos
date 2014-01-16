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
package org.n52.epos.filter.pattern;

import java.util.List;
import java.util.Map;

import org.n52.epos.event.EposEvent;
import org.n52.epos.pattern.CustomStatementEvent;
import org.n52.epos.rules.Rule;

/**
 * Representation of an event-consuming pattern
 * (e.g. similar to an esper statement).
 * 
 * @author matthes rieke
 *
 */
public interface EventPattern {

	/**
	 * @return true, if the causing events shall be transported
	 * into new events (created if {@link #createsNewInternalEvent()}
	 * as {@link #getNewEventName()}.
	 */
	public boolean createCausality();
	
	/**
	 * if true, the underlying engine
	 * shall call {@link Rule#filter(org.n52.epos.event.EposEvent)} or
	 * {@link Rule#filter(org.n52.epos.event.EposEvent, Object)} when this pattern
	 * matches.
	 * @return true if it creates output to be transferred outside of the engine
	 */
	public boolean createsFinalOutput();
	
	/**
	 * if true, the underlying engine shall create a new event
	 * using {@link #getNewEventName()} and taking {@link #getOutputProperties()}
	 * into account.
	 * @return true if it creates a new event for engine-internal use
	 */
	public boolean createsNewInternalEvent();
	
	/**
	 * @return the string representation which the underlying engine
	 * shall use to instantiate its internal representation
	 */
	public String createStringRepresentation();

	/**
	 * @return the id of this pattern
	 */
	public String getID();

	/**
	 * @return the name of the external input this pattern
	 * uses or null if it does require an external input
	 */
	public String getInputName();

	/**
	 * @return the input properties this pattern may define
	 * rules for.
	 */
	public Map<String, Object> getInputProperties();

	/**
	 * @return the name of the event which this pattern is
	 * publishing to, or null if {@link #createsNewInternalEvent()} is false.
	 */
	public String getNewEventName();

	/**
	 * @return the event properties which are used for the events
	 * created as {@link #getNewEventName()}.
	 */
	public Map<String, Object> getOutputProperties();

	/**
	 * @return a list of {@link EventPattern#getID()} references
	 * which this pattern uses as inputs
	 */
	public List<String> getRelatedInputPatterns();
	
	/**
	 * if true, the underlying engine shall call
	 * {@link Rule#filter(org.n52.epos.event.EposEvent, Object)} when
	 * this pattern matches with {@link EposEvent#getOriginalObject()} as
	 * the second method-call argument.
	 * 
	 * @return if the original event shall be used as the output
	 * outside the underlying engine (see {@link #createsFinalOutput()}.
	 */
	public boolean usesOriginalEventAsOutput();

	public boolean hasCustomStatementEvents();

	public List<CustomStatementEvent> getCustomStatementEvents();
}
