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
package org.n52.epos.pattern.eml.pattern;

import org.n52.epos.pattern.eml.EMLPatternFilter;



/**
 * Representation of a reference to a pattern output.
 * 
 * @author Thomas Everding
 *
 */
public class PatternOutputReference {
	
	private int selectFunctionNumber;
	
	private String patternID;
	
	private String newEventName = "";

	private EMLPatternFilter controller;
	
	
	/**
	 * 
	 * Constructor
	 *
	 * @param selectFuncgtionNumber the select function number of the output
	 * @param patternID the ID of the pattern
	 * @param controller the logic controller to resolve the reference
	 */
	public PatternOutputReference(int selectFuncgtionNumber, String patternID, EMLPatternFilter controller) {
		this.selectFunctionNumber = selectFuncgtionNumber;
		this.patternID = patternID;
		this.controller = controller;
	}


	
	/**
	 * @return the selectFunctionNumber
	 */
	public int getSelectFunctionNumber() {
		return this.selectFunctionNumber;
	}


	
	/**
	 * @return the patternID
	 */
	public String getPatternID() {
		return this.patternID;
	}


	
	/**
	 * @return the newEventName as resolved by the controller. may return an empty string if the reference cannot (yet) be resolved
	 */
	public String getNewEventName() {
		if (this.newEventName.equals("") || this.newEventName.equals("null")) {
			//resolve reference
			this.newEventName = this.controller.resolveNewEventName(this.patternID, this.selectFunctionNumber);
			
			//check result
			if (this.newEventName.equals("null")) {
				return "";
			}
		}
		
		return this.newEventName;
	}

}
