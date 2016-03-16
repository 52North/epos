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
package org.n52.epos.event;

/**
 * The interface representing an internal event
 * object.
 * 
 * @author matthes rieke
 *
 */
public interface EposEvent {
	
	/**
	 * @param key the key to associate with the value
	 * @param value the value object to be associated with the key
	 */
	public void setValue(CharSequence key, Object value);

	/**
	 * @param key the associated key
	 * @return the value object associated with this key
	 */
	public Object getValue(CharSequence key);

	/**
	 * @return the original object of this event
	 */
	public Object getOriginalObject();

	/**
	 * @return the start time of this event. if it is a discrete event
	 * {@link #getStartTime()} == {@link #getEndTime()} shall be true.
	 */
	public long getStartTime();

	/**
	 * @return the end time of this event. if it is a discrete event
	 * {@link #getStartTime()} == {@link #getEndTime()} shall be true.
	 */
	public long getEndTime();

	/**
	 * adds a causal ancestor of this event
	 * 
	 * @param event the causal ancestor 
	 */
	public void addCausalAncestor(EposEvent event);

	
	/**
	 * @param input the original basis for this event
	 */
	public void setOriginalObject(Object input);
        
        String getContentType();

}
