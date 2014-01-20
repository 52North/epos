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
