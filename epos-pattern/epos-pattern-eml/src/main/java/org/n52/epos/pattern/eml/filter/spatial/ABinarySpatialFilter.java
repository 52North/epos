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
package org.n52.epos.pattern.eml.filter.spatial;


import net.opengis.fes.x20.BinarySpatialOpType;

import org.apache.xmlbeans.XmlObject;
import org.n52.epos.event.MapEposEvent;
import org.n52.epos.pattern.eml.filter.expression.LiteralExpression;
import org.n52.epos.pattern.functions.MethodNames;
import org.n52.oxf.conversion.gml32.xmlbeans.jts.GMLGeometryFactory;
import org.n52.oxf.xmlbeans.tools.XmlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;


/**
 * 
 * @author Matthes Rieke <m.rieke@uni-muenster.de>
 *
 */
public abstract class ABinarySpatialFilter extends ASpatialFilter {

	/**
	 * the operator of this instance 
	 */
	protected BinarySpatialOpType bsOperator;

	/**
	 * the global logger
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(ABinarySpatialFilter.class);

	/**
	 * 
	 * Constructor
	 *
	 * @param bsOp FES binary spatial operator
	 */
	public ABinarySpatialFilter(BinarySpatialOpType bsOp) {
		this.bsOperator = bsOp;
	}

	/**
	 * @param methodName the java method name of the spatial filter
	 * @return the expression string
	 */
	protected String createExpressionForBinaryFilter(String methodName) {
		Geometry geom = null;

		XmlObject[] literals = this.bsOperator.selectChildren(LiteralExpression.FES_2_0_LITERAL_NAME);

		if (literals.length > 1) {
			logger.warn("Multiple fes:Literal in expression. using the first.");
		}
		if (literals.length >= 1) {

			XmlObject[] children = XmlUtil.selectPath("./*", literals[0]);

			if (children.length > 1) {
				logger.warn("Multiple children in fes:Literal. using the first.");
			}
			if (children.length >= 1) {
				try {
					geom = GMLGeometryFactory.parseGeometry(children[0]);
				} catch (ParseException e) {
					logger.warn(e.getMessage(), e);
				}
			}

		}

		if (geom == null) {
			logger.warn("Only gml:Envelope supported at the current developement state.");
			return null;
		}
		
		//TODO reihenfolge
		
		StringBuilder sb = new StringBuilder();

		sb.append(MethodNames.SPATIAL_METHODS_PREFIX);
		sb.append(methodName+ "(");
		//create WKT from corners
		//TODO actually resolve the property name from the ValueReference?!
		sb.append(MapEposEvent.GEOMETRY_KEY );
		sb.append(", ");
		sb.append(MethodNames.SPATIAL_METHODS_PREFIX);
		sb.append("fromWKT(\""+ geom.toText() +"\"))");

		return sb.toString();
	}

}
