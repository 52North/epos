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
package org.n52.epos.pattern.eml.util;

import java.util.Map;
import java.util.Vector;

import javax.xml.namespace.QName;

import net.opengis.em.x020.DerivedEventDocument;
import net.opengis.em.x020.DerivedEventType;
import net.opengis.em.x020.EventDocument;
import net.opengis.em.x020.EventEventRelationshipType;
import net.opengis.em.x020.EventType;
import net.opengis.em.x020.EventType.EventTime;
import net.opengis.em.x020.NamedValueType;
import net.opengis.eml.x001.EMLDocument;
import net.opengis.eml.x001.EMLDocument.EML;
import net.opengis.gml.FeaturePropertyType;
import net.opengis.gml.TimeInstantDocument;
import net.opengis.gml.TimeInstantType;
import net.opengis.gml.TimePeriodDocument;
import net.opengis.gml.TimePeriodType;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.joda.time.DateTime;
import org.n52.epos.event.MapEposEvent;
import org.n52.epos.filter.pattern.OutputGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

/**
 * @author Matthes Rieke <m.rieke@uni-muenster.de>
 *
 * Output generator using the OGC Event Model
 *
 */
public class EventModelGenerator implements OutputGenerator {

	private static final Logger logger = LoggerFactory
			.getLogger(EventModelGenerator.class);
	
	private static final String CAUSE_STRING = "http://www.opengis.net/em/roles/0.2/cause";

//	private Logger logger = Logger.getLogger(EventModelGenerator.class.getName());
	private MapEposEvent eventMap;

	private EventDocument resultEventDoc;
	private EventType resultEventType;

	private DerivedEventDocument resultDerivedEventDoc;
	private DerivedEventType resultDerivedEventType;
	
	private boolean firstRecursion = true;

	private EML eml;

	/**
	 * @param eml MapEvent to generate the model from
	 */
	public EventModelGenerator(EML eml) {
		this.eml = eml;
	}
	

	/**
	 * @return XmlObject holding the {@link EventType}
	 */
	public XmlObject generateEventDocument() {
		return this.generateEventDocument(null);
	}



	private EventType generateFromEventType(MapEposEvent recursiveEvent, EventType preEvent) {
		
		/*
		 * If the value of the event is of
		 * type MapEvent in the FIRST call
		 * of this method then on the event
		 * stored under value shall be exported.
		 * 
		 * This happens if SelectEvent is used.
		 */
		if (this.firstRecursion) {
			this.firstRecursion = false;
			
			if (recursiveEvent.get(MapEposEvent.VALUE_KEY) instanceof MapEposEvent) {
				return this.generateFromEventType((MapEposEvent) recursiveEvent.get(MapEposEvent.VALUE_KEY), preEvent);
			}
		}
		
		for (String key : recursiveEvent.keySet()) {
			if (key.equals(MapEposEvent.CAUSALITY_KEY) || key.equals(MapEposEvent.THIS_KEY) ||
					key.equals(MapEposEvent.END_KEY) || key.equals(MapEposEvent.STRING_VALUE_KEY) || 
					key.equals(MapEposEvent.DOUBLE_VALUE_KEY) || key.equals(MapEposEvent.PUBLICATION_KEY)) {
				/*
				 * Ignore following keys:
				 * 
				 * causality (is handled later)
				 * end time (is handled within start time)
				 * stringValue and doubleValue (contain the same as value)
				 * this (would just repeat the whole event)
				 */
//				this.logger.info("ignoring key: " + key);
				continue;
			}
			else if (key.equals(MapEposEvent.START_KEY)) {
				/*
				 * time
				 */
//				this.logger.info("adding time");

				EventTime time;
				//instant
				if (recursiveEvent.get(key) == 
					recursiveEvent.get(MapEposEvent.END_KEY)) {
					TimeInstantType timeInstant = TimeInstantType.Factory.newInstance();
					timeInstant.addNewTimePosition().setStringValue(
							new DateTime(recursiveEvent.get(key)).toString());

					/*
					 * workaround for xsi:type attribute
					 */
					time = preEvent.addNewEventTime();
					time.setTimePrimitive(timeInstant);
					XmlCursor cursor = time.getTimePrimitive().newCursor();
					cursor.setName(TimeInstantDocument.type.getDocumentElementName());
					cursor.removeAttribute(new QName("http://www.w3.org/2001/XMLSchema-instance",
							"type"));
				}
				//period
				else {
					TimePeriodType timePeriod = TimePeriodType.Factory.newInstance();
					timePeriod.addNewBeginPosition().setStringValue(
							new DateTime(recursiveEvent.get(key)).toString());
					timePeriod.addNewEndPosition().setStringValue(
							new DateTime(recursiveEvent.get(MapEposEvent.END_KEY)).toString());

					/*
					 * workaround for xsi:type attribute
					 */
					time = preEvent.addNewEventTime();
					time.setTimePrimitive(timePeriod);
					XmlCursor cursor = time.getTimePrimitive().newCursor();
					cursor.setName(TimePeriodDocument.type.getDocumentElementName());
					cursor.removeAttribute(new QName("http://www.w3.org/2001/XMLSchema-instance",
							"type"));
				}
			}
			else if (recursiveEvent.get(key) instanceof Map<?, ?> &&
					((Map<?, ?>)recursiveEvent.get(key)).containsKey(MapEposEvent.ORIGNIAL_OBJECT_KEY)) {
				/*
				 * value of key is a MapEvent containing 
				 * an original message -> just use original 
				 * message
				 */
//				this.logger.info("the event contains a map with an original message at key '" + key + "'. Using only the original message.");
				Object origMess = ((Map<?, ?>)recursiveEvent.get(key)).get(MapEposEvent.ORIGNIAL_OBJECT_KEY);
				
				try {
					XmlObject xo = null;
					if (origMess instanceof XmlObject) {
						logger.debug("original object is an XmlObject");
						xo = (XmlObject) origMess;
					}
					else if (origMess instanceof Node) {
						logger.debug("original object is a DOM Node");
						xo = XmlObject.Factory.parse((Node) origMess);
					}
					
					if (xo != null) {
						NamedValueType namedVal = preEvent.addNewAttribute().addNewNamedValue();
						namedVal.addNewName().setStringValue(key);
						namedVal.addNewValue().set(xo);
					}
				}
				catch (Exception e) {
					//log exception
					logger.warn(e.getMessage(), e);
					
					//forward exception
					throw new RuntimeException(e);
				}
			}
			else {
				/*
				 * other values
				 */
				NamedValueType namedVal = preEvent.addNewAttribute().addNewNamedValue();
				String value = recursiveEvent.get(key).toString();
				
				try {
					Text text = namedVal.getDomNode().getOwnerDocument().createTextNode(value);
					
					XmlObject xo = XmlObject.Factory.parse(text);
					
					namedVal.addNewName().setStringValue(key);
					namedVal.addNewValue().set(xo);
				}
				catch (Exception e) {
					logger.warn(e.getMessage(), e);
					
					//forward exception
					throw new RuntimeException(e);
				}
			}
		}

		//get the cause. should be available cause no Original Message was set.
		Object cause = recursiveEvent.get(MapEposEvent.CAUSALITY_KEY);

		if (cause != null && cause instanceof Vector<?>) {
			Vector<?> causalities = (Vector<?>) cause;

			if (!(preEvent instanceof DerivedEventType)) {
				EventModelGenerator.logger.warn("No DerviedEvent. continue without adding causality.");
			}
			else {
				DerivedEventType derEvent = (DerivedEventType) preEvent;
				for (Object object : causalities) {
					if (object instanceof MapEposEvent) {

						EventEventRelationshipType eventRelation = 
							derEvent.addNewMember().addNewEventEventRelationship();
						eventRelation.addNewRole().setStringValue(CAUSE_STRING);

						/*
						 * add the original message as  the target of
						 * the EventEventRelationship
						 */
						if (((MapEposEvent) object).containsKey(MapEposEvent.ORIGNIAL_OBJECT_KEY)) {
							//TODO: seems kind of dirty, as this class should not know NotficiationMessage, but INotificationMessage
							//XXX WTF, dude?!
							Object notify = ((MapEposEvent) object).get(MapEposEvent.ORIGNIAL_OBJECT_KEY);
							
							logger.debug("Found original object: {}", notify);
							
							if (notify instanceof XmlObject) {
								eventRelation.addNewTarget().set((XmlObject) notify);
							}
							else if (notify instanceof Node) {
								try {
									eventRelation.addNewTarget().set(XmlObject.Factory.parse((Node) notify));
								} catch (XmlException e) {
									logger.warn("Could not generate XmlObject from node '{}'", notify);
								}
							}

						}
						/*
						 * recursive call because we do not have a
						 * original message
						 */
						else {
							EventType causingEvent = generateFromEventType((MapEposEvent) object,
									DerivedEventType.Factory.newInstance());
							
							FeaturePropertyType target = eventRelation.addNewTarget();
							target.addNewFeature().set(causingEvent);
							
							/*
							 * workaround for xsi:type (abstract elements)
							 */
							XmlCursor cursor = target.newCursor();
							cursor.toFirstChild();
							cursor.setName(new QName("http://www.opengis.net/em/0.2.0", "DerivedEvent"));
							cursor.removeAttribute(new QName("http://www.w3.org/2001/XMLSchema-instance",
									"type"));
						}

					}
				}
			}
		}

		return preEvent;
	}

	/**
	 * @param eml if eml available put it in the procedure
	 * @return XmlObject holding the {@link EventType}
	 */
	public XmlObject generateEventDocument(XmlObject eml) {

		if (this.resultEventType != null) {
			generateFromEventType(this.eventMap, this.resultEventType);
			return this.resultEventDoc;
		}
		generateFromEventType(this.eventMap, this.resultDerivedEventType);
		if (eml  != null) {
			this.resultDerivedEventType.setProcedure(eml);
		}
		else {
			this.resultDerivedEventType.addNewProcedure();
		}
		return this.resultDerivedEventDoc;
	}


	@Override
	public Object generateOutput(MapEposEvent resultEvent) {
		this.eventMap = resultEvent;
		
		if (this.eventMap.containsKey(MapEposEvent.ORIGNIAL_OBJECT_KEY)) {
			this.resultEventDoc = EventDocument.Factory.newInstance();
			this.resultEventType = this.resultEventDoc.addNewEvent();
		}
		else {
			this.resultDerivedEventDoc = DerivedEventDocument.Factory.newInstance();
			this.resultDerivedEventType = this.resultDerivedEventDoc.addNewDerivedEvent();
		}
		
		EMLDocument emlDoc = EMLDocument.Factory.newInstance();
		emlDoc.setEML(eml);
		
		return generateEventDocument(emlDoc);
	}



}
