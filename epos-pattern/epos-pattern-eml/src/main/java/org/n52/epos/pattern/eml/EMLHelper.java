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

import javax.xml.namespace.QName;

import net.opengis.fes.x20.BinaryComparisonOpType;
import net.opengis.fes.x20.BinaryLogicOpType;
import net.opengis.fes.x20.ComparisonOpsType;
import net.opengis.fes.x20.FilterType;
import net.opengis.fes.x20.LiteralType;
import net.opengis.fes.x20.LogicOpsType;
import net.opengis.fes.x20.PropertyIsBetweenType;
import net.opengis.fes.x20.PropertyIsLikeType;
import net.opengis.fes.x20.PropertyIsNullType;
import net.opengis.fes.x20.UnaryLogicOpType;
import net.opengis.swe.x101.QuantityDocument;
import net.opengis.swe.x101.UomPropertyType;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.n52.oxf.conversion.unit.NumberWithUOM;
import org.n52.oxf.conversion.unit.ucum.UCUMTools;
import org.n52.oxf.xmlbeans.tools.XmlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EMLHelper {
	
	private static final Logger logger = LoggerFactory.getLogger(EMLHelper.class);

	private static final QName AND_QNAME = new QName("http://www.opengis.net/fes/2.0", "And");
	
	private static final QName OR_QNAME = new QName("http://www.opengis.net/fes/2.0", "Or");
	
	private static final QName NOT_QNAME = new QName("http://www.opengis.net/fes/2.0", "Not");

	private static final QName VALUE_REFERENCE_QNAME =
		new QName("http://www.opengis.net/fes/2.0", "ValueReference");
	
	/**
	 * helpermethod for replacePhenomenonStringsAndConvertUnits().
	 * @throws Exception 
	 */
	public static void replaceForFilter(FilterType filter) throws Exception {
		if (filter.isSetLogicOps()) {
			replaceForLogicOp(filter.getLogicOps());
		}
		
		if (filter.isSetComparisonOps()) {
			ComparisonOpsType cOps = filter.getComparisonOps();
			replaceForComparisonOp(cOps);	
		}
		
	}
	
	/**
	 * helpermethod for replacePhenomenonStringsAndConvertUnits().
	 * @throws Exception 
	 */
	public static void replaceForLogicOp(LogicOpsType logicOps) throws Exception {
		QName loQName = logicOps.newCursor().getName();
		
		/*
		 * check Not
		 */
		if (NOT_QNAME.equals(loQName)) {
			//create new NotFilter
			UnaryLogicOpType unaryOp = (UnaryLogicOpType) logicOps;
			if (unaryOp.isSetComparisonOps()) {
				replaceForComparisonOp(unaryOp.getComparisonOps());
			}
			if (unaryOp.isSetLogicOps()) {
				//rekursion
				replaceForLogicOp(unaryOp.getLogicOps());
			}
		}
		
		/*
		 * binary operators
		 */
		if (AND_QNAME.equals(loQName) || OR_QNAME.equals(loQName)) {
			BinaryLogicOpType binaryOp = (BinaryLogicOpType) logicOps;
			
			ComparisonOpsType[] array = binaryOp.getComparisonOpsArray();
			for (ComparisonOpsType cOps : array) {
				replaceForComparisonOp(cOps);
			}
			
			LogicOpsType[] array2 = binaryOp.getLogicOpsArray();
			for (LogicOpsType logicOp : array2) {
				replaceForLogicOp(logicOp);
			}
		}
				
	}
	
	/**
	 * helpermethod for replacePhenomenonStringsAndConvertUnits().
	 * @throws Exception 
	 */
	public static void replaceForComparisonOp(ComparisonOpsType cOps) throws Exception {
		if (cOps instanceof PropertyIsBetweenType) {
			PropertyIsBetweenType pibt = (PropertyIsBetweenType) cOps;
			
			XmlObject[] exps = new XmlObject[3];
			exps[0] = pibt.getExpression();
			exps[1] = pibt.getLowerBoundary().getExpression();
			exps[2] = pibt.getUpperBoundary().getExpression();
			
			for (XmlObject et : exps) {
				replaceForExpression(et);
			}
			
		} else if (cOps instanceof BinaryComparisonOpType) {

			BinaryComparisonOpType bcot = (BinaryComparisonOpType) cOps;
			XmlObject[] exps = bcot.getExpressionArray();
			for (XmlObject et : exps) {
				replaceForExpression(et);
			}
		} else if (cOps instanceof PropertyIsNullType) {
			PropertyIsNullType pint = (PropertyIsNullType) cOps;
			XmlObject pnt = pint.getExpression();
			replaceForExpression(pnt);

		} else if (cOps instanceof PropertyIsLikeType) {
			PropertyIsLikeType pilt = (PropertyIsLikeType) cOps;

			XmlObject[] lt = pilt.getExpressionArray();
			
			if (lt != null && lt.length > 1) {
				replaceForExpression(lt[0]);
				replaceForExpression(lt[1]);	
			}
			
		}
	}

	/**
	 * helpermethod for replacePhenomenonStringsAndConvertUnits().
	 * @throws Exception 
	 */
	public static void replaceForExpression(XmlObject et) throws Exception {
		QName etQn = et.newCursor().getName();
		if (et instanceof LiteralType) {
			LiteralType lt = (LiteralType) et;

			XmlObject xmlContent = XmlObject.Factory.parse(lt.toString());
			if (xmlContent instanceof QuantityDocument) {
				QuantityDocument sweQ = (QuantityDocument) xmlContent;
				if (sweQ.getQuantity() != null) {
					if (!sweQ.getQuantity().isSetValue()) {
						throw new Exception("There was" +
								" no Value specified in the swe:Quantity element");
					}
					Double value = sweQ.getQuantity().getValue();
					
					UomPropertyType uom = sweQ.getQuantity().getUom();
					String uomCode = "";
					if (uom != null) {
						uomCode = uom.getCode();
					}
					else {
						//no ucum-code, just return without converting
						return;
					}
					
					try {
						NumberWithUOM result = UCUMTools.convert(uomCode, value);
						sweQ.getQuantity().setValue(result.getValue());
						sweQ.getQuantity().getUom().setCode(result.getUom());
						et.set(sweQ);		
					}
					catch (Throwable t) {
						logger.warn("Could not convert uom '" + uom.xmlText(new XmlOptions().setSaveOuter()) +
								"' to base unit, reason: " + t.getMessage());
						if (t instanceof RuntimeException) throw (RuntimeException) t;
						throw new RuntimeException(t);
					}

				}
			}
		
		} 

		else if (VALUE_REFERENCE_QNAME.equals(etQn)) {
			String urn = XmlUtil.stripText(et);
			urn = urn.replaceAll(":", "__").replaceAll("\\.", "_");
			XmlUtil.setTextContent(et, urn);
		}

	}

	
}
