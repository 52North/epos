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
package org.n52.epos.pattern.eml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.n52.epos.filter.pattern.EventPattern;
import org.n52.epos.filter.pattern.OutputGenerator;
import org.n52.epos.pattern.CustomStatementEvent;
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
	private Map<String, Object> inputProperties = new HashMap<String, Object>();
	private Map<String, Object> outputProperties = new HashMap<String, Object>();
	private OutputGenerator generator;

	public EMLEventPattern(APattern pattern, Statement statement,
			Map<String, Object> inputProps, Map<String, Object> outputProps,
			List<String> internalStreamNames) {
		this.statement = statement;
		if (pattern instanceof PatternComplex) {
			initFromComplex((PatternComplex) pattern, internalStreamNames);
		}
		else if (pattern instanceof PatternSimple) {
			initFromSimple((PatternSimple) pattern, internalStreamNames);
		}
		
		this.id = pattern.getPatternID();

		if (inputProps != null)
			this.inputProperties.putAll(inputProps);
		
		if (outputProps != null)
			this.outputProperties.putAll(outputProps);
		
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
	public Map<String, Object> getInputProperties() {
		return this.inputProperties;
	}
	
	@Override
	public Map<String, Object> getOutputProperties() {
		return this.outputProperties;
	}
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName() +": "+ this.statement.getStatement();
	}

	@Override
	public boolean createCausality() {
		return this.statement.getSelectFunction().getCreateCausality();
	}


	@Override
	public boolean hasCustomStatementEvents() {
		return this.statement.hasCustomStatementEvents();
	}


	@Override
	public List<CustomStatementEvent> getCustomStatementEvents() {
		return this.statement.getCustomStatementEvents();
	}


	@Override
	public OutputGenerator getOutputGenerator() {
		return this.generator;
	}

	public void setOutputGenerator(OutputGenerator generator) {
		this.generator = generator;
	}
	

}
