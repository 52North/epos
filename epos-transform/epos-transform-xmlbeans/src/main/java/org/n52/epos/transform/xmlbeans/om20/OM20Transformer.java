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
 * This program is free software; you can redistribute and/or modify it under
 * the terms of the GNU General Public License version 2 as published by the
 * Free Software Foundation.
 *
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied
 * WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program (see gnu-gpl v2.txt). If not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
 * visit the Free Software Foundation web page, http://www.fsf.org.
 */
package org.n52.epos.transform.xmlbeans.om20;

import java.util.ArrayList;
import java.util.ServiceLoader;

import javax.xml.namespace.QName;

import net.opengis.gml.x32.AbstractTimeObjectType;
import net.opengis.gml.x32.FeaturePropertyType;
import net.opengis.gml.x32.ReferenceType;
import net.opengis.gml.x32.TimeInstantType;
import net.opengis.gml.x32.TimePeriodType;
import net.opengis.gml.x32.TimePositionType;
import net.opengis.om.x20.NamedValuePropertyType;
import net.opengis.om.x20.OMObservationDocument;
import net.opengis.om.x20.OMObservationType;
import net.opengis.om.x20.OMProcessPropertyType;
import net.opengis.om.x20.TimeObjectPropertyType;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.values.XmlAnyTypeImpl;
import org.joda.time.DateTime;
import org.n52.epos.event.EposEvent;
import org.n52.epos.event.MapEposEvent;
import org.n52.epos.transform.TransformationException;
import org.n52.epos.transform.xmlbeans.AbstractXmlBeansTransformer;
import org.n52.oxf.xmlbeans.tools.XmlUtil;

/**
 * built-in {@link SosEvent} to {@link MapEposEvent} transformer.
 * 
 * @author matthes rieke
 *
 */
public class OM20Transformer extends AbstractXmlBeansTransformer {

	private static ArrayList<NamedParameterParser> namedParametersParsers;

	static {
		namedParametersParsers = new ArrayList<NamedParameterParser>();
		ServiceLoader<NamedParameterParser> loader = ServiceLoader.load(NamedParameterParser.class);
		for (NamedParameterParser npp : loader) {
			namedParametersParsers.add(npp);
		}
	}
	
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
	
	@Override
	protected boolean supportsXmlBeansInput(XmlObject input) {
		if (input == null)
			return false;
		
		if (input instanceof OMObservationDocument ||
				input instanceof OMObservationType) {
			return true;
		}
		
		return false;
	}

	@Override
	protected QName getSupportedQName() {
		return OMObservationDocument.type.getDocumentElementName();
	}


	private MapEposEvent parseObservation(OMObservationType obs) {
		MapEposEvent result = parsePhenomenonTime(obs.getPhenomenonTime());
		
		parseProcedure(obs.getProcedure(), result);
		
		parseParameters(obs.getParameterArray(), result);
		
		parseObservedProperty(obs.getObservedProperty(), result);
		
		parseFeatureOfInterest(obs.getFeatureOfInterest(), result);
		
		parseResult(obs.getResult(), result);
		
		return result;
	}

	private void parseParameters(NamedValuePropertyType[] parameterArray,
			MapEposEvent result) {
		for (NamedValuePropertyType namedValue : parameterArray) {
			for (NamedParameterParser npp : namedParametersParsers) {
				if (npp.supportsName(namedValue.getNamedValue().getName().getHref().trim())) {
					XmlObject value = namedValue.getNamedValue().getValue();
					XmlCursor cur = value.newCursor();
					if (cur.toFirstChild()) {
						npp.parseValue(cur.getObject(), result);
					}
				}
			}
		}
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
