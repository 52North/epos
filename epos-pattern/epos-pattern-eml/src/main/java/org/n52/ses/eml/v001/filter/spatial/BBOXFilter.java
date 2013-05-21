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
package org.n52.ses.eml.v001.filter.spatial;

import org.apache.xmlbeans.XmlException;
import org.n52.epos.event.MapEposEvent;
import org.n52.oxf.conversion.gml32.xmlbeans.jts.GMLGeometryFactory;
import org.n52.ses.eml.v001.filterlogic.esper.customFunctions.MethodNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.opengis.fes.x20.BBOXType;
import net.opengis.gml.x32.EnvelopeType;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;


/**
 * 
 * @author Matthes Rieke <m.rieke@uni-muenster.de>
 *
 */
public class BBOXFilter extends ASpatialFilter {

	private BBOXType bboxOperator;
	
	private static final String GML_NAMESPACE = "http://www.opengis.net/gml/3.2";

	private static final String	ENVELOPE_NAME = "Envelope";
	
	private static final Logger logger = LoggerFactory
			.getLogger(BBOXFilter.class);

	/**
	 * 
	 * Constructor
	 *
	 * @param bboxOp FES bounding box
	 */
	public BBOXFilter(BBOXType bboxOp) {
		this.bboxOperator = bboxOp;
	}

	@Override
	public String createExpressionString(boolean complexPatternGuard) {
		Geometry geom = null;
		try {
			//get Envelope
			Node bbNode = this.bboxOperator.getDomNode();
			NodeList nodes = bbNode.getChildNodes();
			Node envNode = null;
			
			for (int i = 0; i < nodes.getLength(); i++) {
				if (!nodes.item(i).getNamespaceURI().equals(GML_NAMESPACE)) {
					continue;
				}
				if (!nodes.item(i).getLocalName().equals(ENVELOPE_NAME)) {
					continue;
				}
				//envelope found
				envNode = nodes.item(i);
			}
			
			//parse Envelope
			EnvelopeType envelope = EnvelopeType.Factory.parse(envNode);
			geom = GMLGeometryFactory.parseGeometry(envelope);
		} catch (ParseException e) {
			logger.warn(e.getMessage(), e);
		}
		catch (XmlException e) {
			logger.warn("could not parse the envelope: " + e.getMessage());
		}
		
		if (geom == null) {
			//error while creating geometry
			return "";
		}
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(MethodNames.SPATIAL_METHODS_PREFIX);
		sb.append("bbox(");
		sb.append(MethodNames.SPATIAL_METHODS_PREFIX);
		//create WKT from corners
		sb.append("fromWKT(\""+ geom.toText() +"\")");
		sb.append(", ");
		sb.append(MapEposEvent.GEOMETRY_KEY +")");
		
		return sb.toString();
	}

	@Override
	public void setUsedProperty(String nodeValue) {
		/*empty*/
	}

}
