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
					catch (Exception t) {
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
