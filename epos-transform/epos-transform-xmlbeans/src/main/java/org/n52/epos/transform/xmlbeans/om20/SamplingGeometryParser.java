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
package org.n52.epos.transform.xmlbeans.om20;

import org.apache.xmlbeans.XmlObject;
import org.n52.epos.event.MapEposEvent;
import org.n52.oxf.conversion.gml32.xmlbeans.jts.GMLGeometryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;

public class SamplingGeometryParser implements NamedParameterParser {

	private static final Logger logger = LoggerFactory.getLogger(SamplingGeometryParser.class);
	public static final String SAMPLING_GEOMETRY_NAME = "http://www.opengis.net/def/param‐name/OGC‐OM/2.0/samplingGeometry";
	
	@Override
	public boolean supportsName(String name) {
		return name.trim().equals(SAMPLING_GEOMETRY_NAME);
	}

	@Override
	public void parseValue(XmlObject value, MapEposEvent result) {
		Geometry geom;
		try {
			geom = GMLGeometryFactory.parseGeometry(value);
		} catch (ParseException e) {
			logger.warn(e.getMessage(), e);
			return;
		}
		if (geom != null) {
			result.put(MapEposEvent.GEOMETRY_KEY, geom);
		}
	}

}
