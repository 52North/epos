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

import java.math.BigInteger;

import net.opengis.eml.x001.AbstractPatternType.SelectFunctions;
import net.opengis.eml.x001.EMLDocument;
import net.opengis.eml.x001.EMLDocument.EML;
import net.opengis.eml.x001.EMLDocument.EML.SimplePatterns;
import net.opengis.eml.x001.GuardType;
import net.opengis.eml.x001.SelectFunctionType;
import net.opengis.eml.x001.SelectFunctionType.SelectEvent;
import net.opengis.eml.x001.SimplePatternType;
import net.opengis.eml.x001.ViewType;
import net.opengis.eml.x001.ViewType.LengthView;
import net.opengis.fes.x20.FilterDocument;
import net.opengis.fes.x20.FilterType;

import org.n52.epos.filter.EposFilter;
import org.n52.epos.filter.FilterInstantiationRepository;

public class FES20FilterInstantiation implements FilterInstantiationRepository {

	@Override
	public EposFilter instantiateFrom(Object input) throws Exception {
		FilterDocument filterDoc = (FilterDocument) input;
		return new EMLPatternFilter(createEmlWrapper(filterDoc.getFilter()));
	}

	@Override
	public Class<?> getSupportedInput() {
		return FilterDocument.class;
	}
	
	/**
	 * Generates an EML document holding one
	 * simple pattern with the given FilterType.
	 * 
	 * @param fesFilter an OGC filter encoding 2.0 filter
	 * 
	 * @return an EML document with a simple pattern using the filter as guard
	 */
	private static EMLDocument createEmlWrapper(FilterType fesFilter) {
		EMLDocument doc = EMLDocument.Factory.newInstance();
		EML eml = doc.addNewEML();
		
		eml.addNewComplexPatterns();
		eml.addNewRepetitivePatterns();
		eml.addNewTimerPatterns();
		
		SimplePatterns simple = eml.addNewSimplePatterns();
		
		SimplePatternType pattern = simple.addNewSimplePattern();
		
		pattern.setPatternID("defaultSimplePattern");
		pattern.setInputName("input");
		
		SelectFunctions funcs = pattern.addNewSelectFunctions();
		SelectFunctionType func = funcs.addNewSelectFunction();
		
		func.setNewEventName("");
		func.setOutputName("output");
		SelectEvent selEv = func.addNewSelectEvent();
		selEv.setEventName("sensorStream");
		
		ViewType view = pattern.addNewView();
		LengthView length = view.addNewLengthView();
		length.setEventCount(new BigInteger("1"));
		
		GuardType guard = pattern.addNewGuard();
		guard.setFilter(fesFilter);
		
		pattern.addNewPropertyRestrictions();
		
		return doc;
	}

}
