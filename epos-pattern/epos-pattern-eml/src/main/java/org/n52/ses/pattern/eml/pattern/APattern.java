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
/**
 * Part of the diploma thesis of Thomas Everding.
 * @author Thomas Everding
 */

package org.n52.ses.pattern.eml.pattern;

import java.util.HashSet;
import java.util.Vector;

import org.n52.ses.pattern.eml.filterlogic.EMLParser;


/**
 * superclass of all patterns
 * 
 * @author Thomas Everding
 *
 */
public abstract class APattern{
	
	/**
	 * pattern id
	 */
	protected String patternID;
	
	/**
	 * description for this pattern
	 */
	protected String description = "";
	
	/**
	 * the collection of {@link SelFunction}s
	 */
	protected Vector<SelFunction> selectFunctions = new Vector<SelFunction>();
	
	/**
	 * the used property names
	 */
	protected HashSet<Object> propertyNames = new HashSet<Object>();

	/**
	 * @return the patternID
	 */
	public String getPatternID() {
		return this.patternID;
	}

	/**
	 * sets the patternID
	 * 
	 * @param patternID the patternID to set
	 */
	public void setPatternID(String patternID) {
		this.patternID = patternID;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * sets the pattern description
	 * 
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the select functions
	 */
	public Vector<SelFunction> getSelectFunctions() {
		return this.selectFunctions;
	}
	
	
	/**
	 * adds a select function
	 * 
	 * @param selectFunction the new select function
	 */
	public void addSelectFunction(SelFunction selectFunction) {
		this.selectFunctions.add(selectFunction);
	}
	
	
	/**
	 * creates an esper EPL statement
	 * 
	 * @return this pattern in esper EPL, one statement for every select clause
	 */
	public abstract Statement[] createEsperStatements();

	
	/**
	 * creates an esper EPL statement
	 * 
	 * @param parser the parser to build the statements
	 * 
	 * @return this pattern in esper EPL, one statement for every select clause
	 */
	public abstract Statement[] createEsperStatements(EMLParser parser);
	
	
	/**
	 * 
	 * @return all found property names of this pattern
	 */
	public HashSet<Object> getPropertyNames() {
		return this.propertyNames;
	}
	
}
