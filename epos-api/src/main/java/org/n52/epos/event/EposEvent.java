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

}
