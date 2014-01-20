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
