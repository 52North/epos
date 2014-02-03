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
/**
 * Part of the diploma thesis of Thomas Everding.
 * @author Thomas Everding
 */

package org.n52.epos.pattern.eml.filter.expression;

import javax.xml.namespace.QName;

import net.opengis.fes.x20.LiteralType;
import net.opengis.swe.x101.UomPropertyType;
import net.opengis.swe.x101.CategoryDocument.Category;
import net.opengis.swe.x101.CountDocument.Count;
import net.opengis.swe.x101.QuantityDocument.Quantity;
import net.opengis.swe.x101.TextDocument.Text;
import net.opengis.swe.x101.TimeDocument.Time;

import org.apache.xmlbeans.QNameSet;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.n52.oxf.conversion.unit.UOMTools;
import org.n52.oxf.conversion.unit.ucum.UCUMTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents an expression for a literal value 
 * 
 * @author Thomas Everding
 *
 */
public class LiteralExpression extends AFilterExpression{

	/*
	 * Logger instance for this class
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(LiteralExpression.class);

	private LiteralType literal;
	
	/**
	 * Literal element of FES 2.0
	 */
	public static final QName FES_2_0_LITERAL_NAME = new QName("http://www.opengis.net/fes/2.0", "Literal");
	

	/**
	 * 
	 * Constructor
	 *
	 * @param literal the literal value
	 */
	public LiteralExpression(LiteralType literal) {
		this.literal = literal;
	}

	@Override
	public String createExpressionString(boolean complexPatternGuard) {

		//uom conversion or does it work already??
		//there is something implemented in EsperFilterEngine. 
		//This would then only work for esper...
		//Right -> Conversion should be done here, at the end of the chain
		//uom conversion is done for SWECommon AnyScalar types (in private method)

		//TODO different behavior necessary for complex patterns?
		//		if (complexPatternGuard) {
		//			//expression for complex patterns
		//			//TODO: error here?
		//			StringBuilder log = new StringBuilder();
		//			log.append("literal in complex pattern guard found");
		//			log.append("\nliteral: " + literalString);
		//			logger.info(log.toString());
		//			return this.literalString;
		//		}

//		try {
//		/*
//		 * what should this block do? at first try to parse the text 
//		 * inside of an XML element without storing the result.
//		 * The if no exception occurred, another thing, namely
//		 * the literal string (-> the XML text) shall be returned...
//		 */
//			TextDocument.Factory.parse(literal.xmlText());
//			return "'" + literalString.trim() + "'";
//		}
//		catch (XmlException e) {
//			logger.log(Level.INFO, "No TextDocument found. continue... Exception was: "+ e);
//		}


		//try to parse as SWE Common data type
		String result = parseAnyScalarTypes(this.literal);

		if (result != null) {
			//SWE Common type found in the literal element
			return result;
		}
		
		//else no SWE common data type found
		//just use the string representation
		return this.literal.newCursor().getTextValue().trim();
	}

	private String parseAnyScalarTypes(XmlObject anyScalarType) {
		if (anyScalarType == null) return null;

		/*
		 * select children that are of AnyScalar group
		 */
		String sweNS = "http://www.opengis.net/swe/1.0.1";
		QName[] qnArray = new QName[] {
				new QName(sweNS, "Count"),
				new QName(sweNS, "Quantity"),
				new QName(sweNS, "Time"),
				new QName(sweNS, "Boolean"),
				new QName(sweNS, "Category"),
				new QName(sweNS, "Text")
		};

		QNameSet qns = QNameSet.forArray(qnArray);
		qnArray = null;
		XmlObject[] anyScalars = anyScalarType.selectChildren(qns);


		/*
		 * if found any, parse the results
		 */
		if (anyScalars.length == 1) {
			String resultString = null;

			XmlObject as = anyScalars[0];

			UomPropertyType uom = null;
			double doubleValue = Double.NaN;
			String stringValue = null;

			/*
			 * AnyNumerical
			 */
			if (as instanceof Count) {
				Count count = (Count) as;

				if (count.isSetValue()) {
					doubleValue = Double.parseDouble(""+ count.getValue());
				}
			}
			else if (as instanceof Quantity) {
				Quantity quant = (Quantity) as;

				if (quant.isSetUom()) {
					uom = quant.getUom();
				}

				if (quant.isSetValue()) {
					doubleValue = quant.getValue();
				}
			}
			else if (as instanceof Time) {
				//TODO parse
				throw new RuntimeException("time parsing not yet implemented in LiteralExpression.java");
			}

			/*
			 * Other AnyScalars
			 */
			else if (as instanceof net.opengis.swe.x101.BooleanDocument.Boolean) {
				net.opengis.swe.x101.BooleanDocument.Boolean bool = 
					(net.opengis.swe.x101.BooleanDocument.Boolean) as;

				if (bool.isSetValue()) {
					stringValue = ""+ bool.getValue();
				}
			}
			else if (as instanceof Category) {
				Category cat = (Category) as;

				if (cat.isSetValue()) {
					stringValue = cat.getValue();
				}
			}
			else if (as instanceof Text) {
				Text text = (Text) as;

				if (text.isSetValue()) {
					stringValue = text.getValue();
				}
			}

			//convert if a uom was found
			if (uom != null && doubleValue != Double.NaN) {
				try {
					//convert to SI units
					doubleValue= UOMTools.convertToBaseUnit(doubleValue, uom.getCode());
					
					if (UCUMTools.isCompatible(uom.getCode(), "s")) {
						//XXX This affects also Parsers -> VERY DIRTY as it is NOT transparent.
						//time values shall be represented as milliseconds -> mult by 1000
						doubleValue *= 1000;
					}
				}
				catch (Throwable t) {
					logger.warn("Could not convert uom '" + uom.xmlText(new XmlOptions().setSaveOuter()) +
							"' to base unit, reason: " + t.getMessage());
					if (t instanceof RuntimeException) throw (RuntimeException) t;
					throw new RuntimeException(t);
				}
			}

			if (stringValue != null) {
				resultString = "\""+ stringValue +"\"";
			}
			else {
				resultString = ""+ doubleValue;
			}

			return resultString;
		}
		//else
		return null;
	}
}
