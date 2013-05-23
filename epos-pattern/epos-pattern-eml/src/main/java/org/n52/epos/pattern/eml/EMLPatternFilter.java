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
import java.util.Vector;

import net.opengis.eml.x001.EMLDocument;
import net.opengis.eml.x001.EMLDocument.EML;
import net.opengis.eml.x001.EventAttributeType;
import net.opengis.eml.x001.RepetitivePatternDocument;
import net.opengis.eml.x001.SimplePatternType;
import net.opengis.eml.x001.SimplePatternType.PropertyRestrictions;
import net.opengis.fes.x20.FilterType;

import org.apache.xmlbeans.XmlObject;
import org.n52.epos.event.DataTypesMap;
import org.n52.epos.event.EposEvent;
import org.n52.epos.event.MapEposEvent;
import org.n52.epos.filter.pattern.EventPattern;
import org.n52.epos.filter.pattern.PatternFilter;
import org.n52.epos.pattern.eml.pattern.APattern;
import org.n52.epos.pattern.eml.pattern.PatternComplex;
import org.n52.epos.pattern.eml.pattern.PatternRepetitive;
import org.n52.epos.pattern.eml.pattern.PatternSimple;
import org.n52.epos.pattern.eml.pattern.Statement;
import org.n52.oxf.xmlbeans.tools.XmlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import com.vividsolutions.jts.geom.Geometry;

public class EMLPatternFilter implements PatternFilter {

	private static final Logger logger = LoggerFactory.getLogger(EMLPatternFilter.class);
	private EML eml;
	private EMLParser parser;
	private Map<String, Map<String, Object>> propertiesByEventInput = new HashMap<String, Map<String,Object>>();
	private HashMap<String, APattern> patterns;
	private List<EventPattern> eventPatterns = new ArrayList<EventPattern>();
	private String externalInputStreamName;

	public EMLPatternFilter(EMLDocument emlDoc) throws Exception {
		this.eml = emlDoc.getEML();
		
		initialize();
	}

	private void initialize() throws Exception {
		replacePhenomenonStringsAndConvertUnits(eml);
		
		if (logger.isDebugEnabled())
			logger.debug("initializing EMLPatternFilter controller");
		this.parser = new EMLParser(this);
		this.parser.parseEML(eml);
		
		this.patterns = this.parser.getPatterns();
		
		/*
		 * Instantiate propertyNames for esper config
		 */
		
		Map<String, APattern> simplePatterns = new HashMap<String, APattern>();
		
		for (String key : patterns.keySet()) {
			APattern value = patterns.get(key);
			if (value instanceof PatternSimple) {
				simplePatterns.put(key, value);
			}
		}
		
		//register Map as event type with registered phenomenons/types
		HashMap<String, Object> eventProperties = new HashMap<String, Object>();
		eventProperties.put(MapEposEvent.START_KEY, Long.class);
		eventProperties.put(MapEposEvent.END_KEY, Long.class);
		eventProperties.put(MapEposEvent.STRING_VALUE_KEY, String.class);
		eventProperties.put(MapEposEvent.DOUBLE_VALUE_KEY, Double.class);
		eventProperties.put(MapEposEvent.CAUSALITY_KEY, Vector.class);
		eventProperties.put(MapEposEvent.GEOMETRY_KEY, Geometry.class);
		eventProperties.put(MapEposEvent.SENSORID_KEY, String.class);
		eventProperties.put(MapEposEvent.THIS_KEY, Map.class);

		/*
		 * Get data types for phenomenons.
		 */
		
		//TODO: check if string as a value does work (seems not...)
		DataTypesMap dtm = DataTypesMap.getInstance();
		
		/*
		 * the following loop is needed if a simple pattern 
		 * accesses an event property which is not a standard
		 * property known by the EML parser. If so this property 
		 * has to be added to the event that is registered with
		 * a data type. 
		 */
		for (String key : simplePatterns.keySet()) {
			APattern val = simplePatterns.get(key);
			for (Object key2 : val.getPropertyNames()) {
				eventProperties.put(key2.toString(), dtm.getDataType(key2.toString()));
			}
		}

		getPropertiesFromPatterns(eventProperties, patterns);
		
		for (String key : simplePatterns.keySet()) {
			PatternSimple val = (PatternSimple) simplePatterns.get(key);
			
			registerEventInputProperties(val.getInputName(), eventProperties);
		}
		
		
		//build listeners
		this.buildListeners(patterns);
		
		this.externalInputStreamName = resolveExternalInputStream();
	}
	
	private String resolveExternalInputStream() {
		List<String> candidates = new ArrayList<String>();
		for (EventPattern ep : this.eventPatterns) {
			if (ep.getInputName() != null) {
				if (!candidates.contains(ep.getInputName())) {
					candidates.add(ep.getInputName());
				}
			}
		}
		
		if (candidates.isEmpty()) {
			throw new IllegalStateException("Could not find an external input stream. This Filter will not work.");
		}
		
		if (candidates.size() > 1) {
			logger.warn("Multiple input streams found. Only streams with inputName = '{}' will receive events.",
					candidates.get(0));
		}
		
		return candidates.get(0);
	}

	private void registerEventInputProperties(String inputName,
			Map<String, Object> eventProperties2) {
		this.propertiesByEventInput.put(inputName, new HashMap<String, Object>(eventProperties2));
	}


	private void getPropertiesFromPatterns(Map<String, Object> properties,
			Map<String, APattern> patternMap) {
		
		APattern pat;
		String curr;
		DataTypesMap dtm = DataTypesMap.getInstance();
		for (String key : patternMap.keySet()) {
			pat = patternMap.get(key);
			for (Object s : pat.getPropertyNames()) {
				curr = s.toString();
				
				if (curr.contains("/")) {
					curr = curr.substring(curr.indexOf("/")+1, curr.length());	
				} else {
					curr = curr.substring(curr.indexOf(".")+1, curr.length());
				}
				
				if (!properties.containsKey(curr)) {
					properties.put(curr, dtm.getDataType(curr));
				}
			}
		}
		
	}
	
	/**
	 * builds and registers the listeners for all patterns
	 * 
	 * @param patterns the patterns
	 */
	private void buildListeners(HashMap<String, APattern> patterns) {
		HashMap<String, Object> completedPatterns = new HashMap<String, Object>();
		Vector<APattern> uncompletedPatterns = new Vector<APattern>();
		APattern patt;
		
		List<String> internalStreamNames = new ArrayList<String>();
		internalStreamNames.addAll(patterns.keySet());
		
		//check every pattern
		for (String key : patterns.keySet()) {
			patt = patterns.get(key);
			
			//first run: only simple and timer patterns
			if (patt instanceof PatternComplex || patt instanceof PatternRepetitive) {
				//these patterns need other patterns to be registered first
				uncompletedPatterns.add(patt);
				continue;
			}
			
			this.buildListenersForPattern(patt, internalStreamNames);
			completedPatterns.put(patt.getPatternID(), patt);
		}
		
		//second run: complex and repetitive patterns
		int i = -1;
		int maxTests = uncompletedPatterns.size() * 3;
		while (uncompletedPatterns.size() > 0) {
			uncompletedPatterns = this.buildComplexListeners(completedPatterns, uncompletedPatterns, internalStreamNames);
			
			//check for loop
			i++;
			if (i > maxTests) {
				logger.warn("One of the patterns can not be build or there is a loop in the patterns. This is not allowed.");
				break;
			}
		}
	}
	

	/**
	 * builds the listeners for patterns that use other patterns (complex and repetitive)
	 * 
	 * @param completedPatterns already completed patterns
	 * @param uncompletedPatterns patterns without listener
	 * @param internalStreamNames 
	 * @return the patterns witch could not be build
	 */
	private Vector<APattern> buildComplexListeners(HashMap<String, Object> completedPatterns,
			Vector<APattern> uncompletedPatterns, List<String> internalStreamNames) {
		
		Vector<APattern> stillUncomPatterns = new Vector<APattern>();
		PatternComplex cp;
		PatternRepetitive rp;
		for (APattern pat : uncompletedPatterns) {
			//check if all internal patterns are already completed
			if (pat instanceof PatternComplex) {
				//complex pattern
				cp = (PatternComplex) pat;
				
				if (completedPatterns.containsKey(cp.getFirstPatternID())
						&& completedPatterns.containsKey(cp.getSecondPatternID())) {
					//build pattern listeners
					this.buildListenersForPattern(pat, internalStreamNames);
					completedPatterns.put(pat.getPatternID(), pat);
				}
				else {
					//append to list, try later
					stillUncomPatterns.add(pat);
				}
			}
			else {
				//repetitive pattern
				rp = (PatternRepetitive) pat;
				
				if (completedPatterns.containsKey(rp.getPatternToRepeatID())) {
					//build pattern listeners
					this.buildListenersForPattern(pat, internalStreamNames);
					completedPatterns.put(pat.getPatternID(), pat);
				}
				else {
					//append to list, try later
					stillUncomPatterns.add(pat);
				}
			}
		}
		
		return stillUncomPatterns;
	}
	

	/**
	 * builds and registers the listeners for a single pattern
	 * 
	 * @param pattern the pattern
	 */
	private void buildListenersForPattern(APattern pattern, List<String> internalStreamNames) {
		
		if (logger.isDebugEnabled())
			logger.debug("building listener for pattern " + pattern.getPatternID());
		if (pattern instanceof PatternRepetitive) {
//			/*
//			 * repetitive pattern needs two statements per select function
//			 * 
//			 * first statement is the counting statement, the others are the selecting statements.
//			 */
//			Statement[] statements = pattern.createEsperStatements();
//			
//			//create listeners for the selecting statements
//			for (int i = 1; i < statements.length; i++) {
//				this.eventPatterns.add(createEventPattern(pattern, ))
//			}
			logger.warn(RepetitivePatternDocument.type.getDocumentElementName() + " currently not " +
					"supported.");
		}
		else {
			/*
			 * other pattern only needs one per statement
			 */
			Map<String, Object> properties = resolveInputPropertiesForPattern(pattern);
			for (Statement statement : pattern.createEsperStatements()) {
				EventPattern newPattern = createEventPattern(pattern, statement,
						properties, statement.getSelectFunction().getDataTypes(), internalStreamNames);
				this.eventPatterns.add(newPattern);
			}
		}
	}
	
	

	private Map<String, Object> resolveInputPropertiesForPattern(
			APattern pattern) {
		if (pattern instanceof PatternSimple) {
			return this.propertiesByEventInput.get(((PatternSimple) pattern).getInputName());
		}
		return null;
	}

	private EventPattern createEventPattern(APattern pattern,
			Statement statement, Map<String, Object> properties, Map<String, Object> outputs, List<String> internalStreamNames) {
		EMLEventPattern result = new EMLEventPattern(pattern, statement,
				properties, outputs, internalStreamNames);
		return result;
	}

	public void onTriggeredMatch(EposEvent event) {
		
	}

	public EML getEml() {
		return this.eml;
	}

	@Override
	public List<EventPattern> getPatterns() {
		return this.eventPatterns;
	}

	@Override
	public CharSequence serialize() {
		return this.eml.xmlText();
	}
	
	/**
	 * replaces phenomenon Strings containing ":" to "__" and
	 * converts any quantity units to its base units.
	 * @param converter the unit converter
	 * @throws Exception exceptions that occur
	 */
	public void replacePhenomenonStringsAndConvertUnits(EML emlXml) throws Exception {
		if (emlXml != null) {
			FilterType filter = null;
			SimplePatternType[] patterns = emlXml.getSimplePatterns().getSimplePatternArray();
			for (SimplePatternType spt : patterns) {
				if (spt.isSetGuard()) {
					filter = spt.getGuard().getFilter();
					
					EMLHelper.replaceForFilter(filter);
				}
				
				PropertyRestrictions propRes = spt.getPropertyRestrictions();
				if (propRes != null) {
					EventAttributeType[] arr = propRes.getPropertyRestrictionArray();
					
					for (EventAttributeType eat : arr) {
						XmlObject obj = XmlObject.Factory.newInstance();

						Element elem = (Element) eat.getValue().getDomNode();
						String tempText = XmlUtil.toString(elem.getFirstChild()).trim();
						
//						tempText = tempText.replaceAll(":", "__").replaceAll("\\.", "_");
						
						//TODO unit conversion not performed.
						
						obj.newCursor().setTextValue(tempText);
						eat.setValue(obj);
					}
				}
			}

		}

	}

	@Override
	public String getInputStreamName() {
		return this.externalInputStreamName;
	}

	public Object getEventDatatype(String eventName) {
		for (String registered : this.propertiesByEventInput.keySet()) {
			if (registered.equals(eventName)) {
				return this.propertiesByEventInput.get(registered);
			}
		}
		return null;
	}

	public Object getDatatype(String fullPropertyName) {
		// split into event and property name part
		String eventName;
		String propertyName;
		int lastSlash = fullPropertyName.lastIndexOf("/");

		propertyName = fullPropertyName.substring(lastSlash + 1);

		int lastButOneSlash = fullPropertyName.substring(0, lastSlash)
				.lastIndexOf("/");

		if (lastButOneSlash <= 0) {
			// full name looks like "event/value"
			eventName = fullPropertyName.substring(0, lastSlash);
		} else {
			// full name looks like "event/nestedEvent/value", we need
			// nestedEvent
			eventName = fullPropertyName.substring(lastButOneSlash + 1,
					lastSlash);
		}

		// check all inputs first
		// for (InputDescription descr : this.inputDescriptions) {
		// if (descr.getName().equals(eventName)) {
		// return DataTypeNameToClassConverter.convert(descr.getDataType());
		// }
		// }

		// then check property Restrictions
//		for (EventPattern pat : this.patterns) {
//			if (pat.getRelatedInputPatterns() == null || pat.getRelatedInputPatterns().isEmpty()) {
//				PatternSimple pats = (PatternSimple) pat;
//				for (PropRestriction propRes : pats.getPropertyRestrictions()) {
//					if (propRes.getName().equals(propertyName)) {
//						if (propRes.getValue().equals(
//								"\"" + MapEposEvent.DOUBLE_VALUE_KEY + "\"")) {
//							return Number.class;
//						}
//					}
//				}
//			}
//		}

		// then check all patterns
		for (EventPattern pat : getPatterns()) {
			if (pat.getNewEventName().equals(eventName)) {
				// this select function defines the data type
				return pat.getInputProperties().get(propertyName);
			}
		}
		return null;
	}

	public String resolveNewEventName(String firstPatternID,
			int firstSelectFunctionNumber) {
		APattern pattern = this.parser.getPatterns().get(firstPatternID);
		
		if (pattern == null) {
			return null;
		}
		
		return pattern.getSelectFunctions().get(firstSelectFunctionNumber).getNewEventName();
	}
	
}
