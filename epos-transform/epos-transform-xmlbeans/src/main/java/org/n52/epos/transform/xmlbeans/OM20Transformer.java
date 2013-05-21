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
import org.n52.epos.event.MapEposEvent;
import org.n52.epos.transform.EposTransformer;
import org.n52.epos.transform.TransformationException;
import org.n52.oxf.xmlbeans.tools.XmlUtil;

/**
 * built-in {@link SosEvent} to {@link MapEposEvent} transformer.
 * 
 * @author matthes rieke
 *
 */
public class OM20Transformer implements EposTransformer {

	@Override
	public EposEvent transform(Object input) throws TransformationException {
		MapEposEvent result = null;
		
		if (input instanceof OMObservationDocument) {
			result = transformFrom((OMObservationDocument) input);
		}
		else if (input instanceof OMObservationType) {
			result = transformFrom((OMObservationType) input);
		}
		
		if (result == null) {
			throw new IllegalStateException("Should never reach here!");
		}
		
		result.put(MapEposEvent.ORIGNIAL_OBJECT_KEY, input);
		return result;
	}

	private MapEposEvent transformFrom(OMObservationDocument input) {
		return transformFrom(input.getOMObservation());
	}
	
	private MapEposEvent transformFrom(OMObservationType input) {
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


	private MapEposEvent parseObservation(OMObservationType obs) {
		MapEposEvent result = parsePhenomenonTime(obs.getPhenomenonTime());
		
		parseProcedure(obs.getProcedure(), result);
		
		parseObservedProperty(obs.getObservedProperty(), result);
		
		parseFeatureOfInterest(obs.getFeatureOfInterest(), result);
		
		parseResult(obs.getResult(), result);
		
		return result;
	}

	private void parseResult(XmlObject object, MapEposEvent result) {
		if (object instanceof XmlAnyTypeImpl) {
			String value = XmlUtil.stripText(object);
			Double asDouble = parseAsDouble(value);
			if (asDouble != null) {
				result.put(MapEposEvent.DOUBLE_VALUE_KEY, asDouble.doubleValue());
				if (result.get(MapEposEvent.OBSERVED_PROPERTY_KEY) != null) {
					result.put(result.get(MapEposEvent.OBSERVED_PROPERTY_KEY).toString(), asDouble.doubleValue());
				}
			}
			else {
				if (result.get(MapEposEvent.OBSERVED_PROPERTY_KEY) != null) {
					result.put(result.get(MapEposEvent.OBSERVED_PROPERTY_KEY).toString(), value);
				}
			}
			
			result.put(MapEposEvent.STRING_VALUE_KEY, value);
			
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
			MapEposEvent result) {
		if (featureOfInterest.isSetHref()) {
			result.put(MapEposEvent.FEATURE_TYPE_KEY, featureOfInterest.getHref());
		}
	}

	private void parseProcedure(OMProcessPropertyType procedure, MapEposEvent result) {
		if (procedure.isSetHref()) {
			result.put(MapEposEvent.SENSORID_KEY, procedure.getHref());
			result.put("procedure", procedure.getHref());
		}
	}

	private void parseObservedProperty(ReferenceType observedProperty,
			MapEposEvent result) {
		if (observedProperty.isSetHref()) {
			result.put(MapEposEvent.OBSERVED_PROPERTY_KEY, observedProperty.getHref());
		}
	}

	private MapEposEvent parsePhenomenonTime(TimeObjectPropertyType phenomenonTime) {
		AbstractTimeObjectType timeObject = phenomenonTime.getAbstractTimeObject();
		
		if (timeObject instanceof TimeInstantType) {
			TimePositionType pos = ((TimeInstantType) timeObject).getTimePosition();
			DateTime dateTime = new DateTime(pos.getStringValue());
			return new MapEposEvent(dateTime.getMillis(), dateTime.getMillis());
		}
		else if (timeObject instanceof TimePeriodType) {
			TimePositionType begin = ((TimePeriodType) timeObject).getBeginPosition();
			TimePositionType end = ((TimePeriodType) timeObject).getEndPosition();
			DateTime beginDate = new DateTime(begin.getStringValue());
			DateTime endDate = new DateTime(end.getStringValue());
			return new MapEposEvent(beginDate.getMillis(), endDate.getMillis());
		}
		
		return null;
	}

	
}
