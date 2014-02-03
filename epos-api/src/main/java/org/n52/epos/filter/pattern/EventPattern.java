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

	public OutputGenerator getOutputGenerator();
}
