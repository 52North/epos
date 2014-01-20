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


	/**
	 * An implementation shall remove the given rule.
	 * It shall not be taken into consideration after this
	 * method call.
	 * 
	 * @param rule the rule to be removed
	 */
	public void removeRule(Rule rule);
	
	/**
	 * an implementation shall free all resources to avoid memory leaks.
	 */
	public void shutdown();

}
