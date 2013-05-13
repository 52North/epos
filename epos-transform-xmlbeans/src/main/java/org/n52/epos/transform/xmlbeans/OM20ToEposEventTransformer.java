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
package org.n52.epos.transform.xmlbeans;

import net.opengis.gml.x32.AbstractTimeObjectType;
import net.opengis.gml.x32.FeaturePropertyType;
import net.opengis.gml.x32.ReferenceType;
import net.opengis.gml.x32.TimeInstantType;
import net.opengis.gml.x32.TimePeriodType;
import net.opengis.gml.x32.TimePositionType;
import net.opengis.om.x20.OMObservationDocument;
import net.opengis.om.x20.OMObservationType;
import net.opengis.om.x20.OMProcessPropertyType;
import net.opengis.om.x20.TimeObjectPropertyType;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.values.XmlAnyTypeImpl;
import org.joda.time.DateTime;
import org.n52.epos.event.EposEvent;
import org.n52.epos.transform.EposTransformer;
import org.n52.epos.transform.TransformationException;
import org.n52.oxf.xmlbeans.tools.XmlUtil;

/**
 * built-in {@link SosEvent} to {@link EposEvent} transformer.
 * 
 * @author matthes rieke
 *
 */
public class OM20ToEposEventTransformer implements EposTransformer {

	@Override
	public EposEvent transform(Object input) throws TransformationException {
		if (input instanceof OMObservationDocument) {
			return transformFrom((OMObservationDocument) input);
		}
		else if (input instanceof OMObservationType) {
			return transformFrom((OMObservationType) input);
		}
		
		throw new IllegalStateException("Should never reach here!");
	}

	private EposEvent transformFrom(OMObservationDocument input) {
		return transformFrom(input.getOMObservation());
	}
	
	private EposEvent transformFrom(OMObservationType input) {
		return parseObservation(input);
	}

	@Override
	public boolean supportsInput(Object input) {
		if (input == null)
			return false;
		
		if (input instanceof OMObservationDocument ||
				input instanceof OMObservationType) {
			return true;
		}
		
		return false;
	}


	private EposEvent parseObservation(OMObservationType obs) {
		EposEvent result = parsePhenomenonTime(obs.getPhenomenonTime());
		
		parseProcedure(obs.getProcedure(), result);
		
		parseObservedProperty(obs.getObservedProperty(), result);
		
		parseFeatureOfInterest(obs.getFeatureOfInterest(), result);
		
		parseResult(obs.getResult(), result);
		
		return result;
	}

	private void parseResult(XmlObject object, EposEvent result) {
		if (object instanceof XmlAnyTypeImpl) {
			String value = XmlUtil.stripText(object);
			Double asDouble = parseAsDouble(value);
			if (asDouble != null) {
				result.put(EposEvent.DOUBLE_VALUE_KEY, asDouble.doubleValue());
				if (result.get(EposEvent.OBSERVED_PROPERTY_KEY) != null) {
					result.put(result.get(EposEvent.OBSERVED_PROPERTY_KEY).toString(), asDouble.doubleValue());
				}
			}
			else {
				if (result.get(EposEvent.OBSERVED_PROPERTY_KEY) != null) {
					result.put(result.get(EposEvent.OBSERVED_PROPERTY_KEY).toString(), value);
				}
			}
			
			result.put(EposEvent.STRING_VALUE_KEY, value);
			
		}
	}

	private Double parseAsDouble(String value) {
		try {
			return Double.parseDouble(value);
		} catch (NumberFormatException e) {
		}
		return null;
	}

	private void parseFeatureOfInterest(FeaturePropertyType featureOfInterest,
			EposEvent result) {
		if (featureOfInterest.isSetHref()) {
			result.put(EposEvent.FEATURE_TYPE_KEY, featureOfInterest.getHref());
		}
	}

	private void parseProcedure(OMProcessPropertyType procedure, EposEvent result) {
		if (procedure.isSetHref()) {
			result.put(EposEvent.SENSORID_KEY, procedure.getHref());
			result.put("procedure", procedure.getHref());
		}
	}

	private void parseObservedProperty(ReferenceType observedProperty,
			EposEvent result) {
		if (observedProperty.isSetHref()) {
			result.put(EposEvent.OBSERVED_PROPERTY_KEY, observedProperty.getHref());
		}
	}

	private EposEvent parsePhenomenonTime(TimeObjectPropertyType phenomenonTime) {
		AbstractTimeObjectType timeObject = phenomenonTime.getAbstractTimeObject();
		
		if (timeObject instanceof TimeInstantType) {
			TimePositionType pos = ((TimeInstantType) timeObject).getTimePosition();
			DateTime dateTime = new DateTime(pos.getStringValue());
			return new EposEvent(dateTime.getMillis(), dateTime.getMillis());
		}
		else if (timeObject instanceof TimePeriodType) {
			TimePositionType begin = ((TimePeriodType) timeObject).getBeginPosition();
			TimePositionType end = ((TimePeriodType) timeObject).getEndPosition();
			DateTime beginDate = new DateTime(begin.getStringValue());
			DateTime endDate = new DateTime(end.getStringValue());
			return new EposEvent(beginDate.getMillis(), endDate.getMillis());
		}
		
		return null;
	}

	
}
