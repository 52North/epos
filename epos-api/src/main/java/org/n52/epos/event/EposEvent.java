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
