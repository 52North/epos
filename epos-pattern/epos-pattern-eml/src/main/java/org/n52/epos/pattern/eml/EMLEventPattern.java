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
package org.n52.epos.pattern.eml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.n52.epos.filter.pattern.EventPattern;
import org.n52.epos.pattern.eml.pattern.APattern;
import org.n52.epos.pattern.eml.pattern.PatternComplex;
import org.n52.epos.pattern.eml.pattern.PatternSimple;
import org.n52.epos.pattern.eml.pattern.Statement;

public class EMLEventPattern implements EventPattern {

	private Statement statement;
	private String id;
	private String inputName;
	private List<String> relatedInputPatterns = new ArrayList<String>();
	private boolean createFinalOutput;
	private String newEventName;
	private boolean createNewInternalEvent;
	private Map<String, Object> eventProperties = new HashMap<String, Object>();

	public EMLEventPattern(APattern pattern, Statement statement, List<String> internalStreamNames) {
		this.statement = statement;
		if (pattern instanceof PatternComplex) {
			initFromComplex((PatternComplex) pattern, internalStreamNames);
		}
		else if (pattern instanceof PatternSimple) {
			initFromSimple((PatternSimple) pattern, internalStreamNames);
		}
		
		this.id = pattern.getPatternID();

		this.eventProperties.putAll(this.statement.getSelectFunction().getDataTypes());
		
		resolveOutputStyle();
	}


	private void resolveOutputStyle() {
		if (this.statement.getSelectFunction().getNewEventName() != null &&
				!this.statement.getSelectFunction().getNewEventName().isEmpty()) {
			this.newEventName = this.statement.getSelectFunction().getNewEventName();
			this.createNewInternalEvent = true;
		}
		
		if (this.statement.getSelectFunction().getOutputName().equals("")) {
			
			//TODO (hack for static EML) fix output for StaticEMLDocument
			if (this.statement.getSelectFunction().getNewEventName().equals("")) {
				this.createFinalOutput = true;
			} else {
				this.createFinalOutput = false;
			}
		}
		else {
			this.createFinalOutput = true;
		}
	}


	private void initFromSimple(PatternSimple pattern, List<String> internalStreamNames) {
		this.inputName = pattern.getInputName();
		
		if (internalStreamNames.contains(this.inputName)) {
			this.relatedInputPatterns.add(this.inputName);
		}
		
	}

	private void initFromComplex(PatternComplex pattern, List<String> internalStreamNames) {
		
		if (internalStreamNames.contains(pattern.getFirstPatternID()) && 
				internalStreamNames.contains(pattern.getSecondPatternID())) {
			this.relatedInputPatterns.add(pattern.getFirstPatternID());
			this.relatedInputPatterns.add(pattern.getSecondPatternID());
		}
		else {
			throw new IllegalStateException("One or all referenced streams of this ComplexPattern '"+
					pattern.getPatternID() +"' are not resolvable!");
		}
		
	}

	@Override
	public List<String> getRelatedInputPatterns() {
		return this.relatedInputPatterns;
	}

	@Override
	public String createStringRepresentation() {
		return this.statement.getStatement();
	}

	@Override
	public boolean createsFinalOutput() {
		return this.createFinalOutput;
	}

	@Override
	public boolean createsNewInternalEvent() {
		return this.createNewInternalEvent;
	}

	@Override
	public String getNewEventName() {
		return this.newEventName;
	}

	@Override
	public boolean usesOriginalEventAsOutput() {
		return this.statement.getSelectFunction().allowsOriginalMessageAsResult();
	}

	@Override
	public String getID() {
		return this.id;
	}

	@Override
	public String getInputName() {
		return this.inputName;
	}

	@Override
	public Map<String, Object> getEventProperties() {
		return this.eventProperties;
	}

}
