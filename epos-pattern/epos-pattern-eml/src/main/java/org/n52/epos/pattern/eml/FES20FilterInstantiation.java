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
