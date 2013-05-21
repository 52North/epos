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
package org.n52.epos.pattern.eml.filter.temporal;

import javax.xml.namespace.QName;

import net.opengis.fes.x20.BinaryTemporalOpType;
import net.opengis.fes.x20.TemporalOpsType;

import org.apache.xmlbeans.XmlObject;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.n52.epos.pattern.eml.filter.IFilterElement;
import org.n52.oxf.xmlbeans.tools.XmlUtil;
import org.w3c.dom.Node;

/**
 * 
 * @author Thomas Everding
 * 
 */
public abstract class ATemporalFilter implements IFilterElement {

	/**
	 * Factory to build new comparison filters.
	 */
	public static final TemporalFilterFactory FACTORY = new TemporalFilterFactory();

	private static final String FES_NAMESPACE = "http://www.opengis.net/fes/2.0";
	private static final String GML_NAMESPACE = "http://www.opengis.net/gml/3.2";

	/**
	 * qualified name of FES ValueReference
	 */
	protected static final QName VALUE_REFERENCE_QNAME = new QName(
			FES_NAMESPACE, "ValueReference");

	/**
	 * qualified name of GML identifier
	 */
	protected static final QName GML_IDENTIFIER = new QName(GML_NAMESPACE,
			"identifier");

	/**
	 * qualified name of GML validTime
	 */
	protected static final QName GML_VALID_TIME = new QName(GML_NAMESPACE,
			"validTime");

	/**
	 * qualified name of GML TimePeriod
	 */
	protected static final QName GML_TIME_PERIOD = new QName(GML_NAMESPACE,
			"TimePeriod");

	/**
	 * qualified name of GML TimeInstant
	 */
	protected static final QName GML_TIME_INSTANT = new QName(GML_NAMESPACE,
			"TimeInstant");

	/**
	 * qualified name of GML timePosition
	 */
	protected static final QName GML_TIME_POSITION = new QName(GML_NAMESPACE,
			"timePosition");

	/**
	 * qualified name of GML beginPosition
	 */
	protected static final QName GML_BEGIN_POSITION = new QName(GML_NAMESPACE,
			"beginPosition");

	/**
	 * qualified name of GML endPosition
	 */
	protected static final QName GML_END_POSITION = new QName(GML_NAMESPACE,
			"endPosition");

	/**
	 * xbeans {@link TemporalOpsType}
	 */
	protected TemporalOpsType temporalOp;

	/**
	 * 
	 * Constructor
	 * 
	 * @param temporalOp
	 *            XML representation of a temporal filter
	 */
	public ATemporalFilter(TemporalOpsType temporalOp) {
		this.temporalOp = temporalOp;
	}

	/**
	 * Parses the gml:validTime from a BinaryTemporalOpType
	 * 
	 * @param binaryOp
	 *            {@link BinaryTemporalOpType}
	 * @return jodatime {@link Interval}
	 */
	protected Interval parseGMLTimePeriodFromBinaryTemporalOp(
			BinaryTemporalOpType binaryOp) {
		XmlObject period = binaryOp.selectChildren(GML_TIME_PERIOD)[0];

		String beginPos = "";
		XmlObject hasIndeterminate = period.selectChildren(GML_BEGIN_POSITION)[0]
				.selectAttribute(new QName("", "indeterminatePosition"));
		if (hasIndeterminate == null) {
			beginPos = stripText(period.selectChildren(GML_BEGIN_POSITION));
		} else {
			beginPos = stripText(hasIndeterminate);
		}

		String endPos = "";
		hasIndeterminate = period.selectChildren(GML_END_POSITION)[0]
				.selectAttribute(new QName("", "indeterminatePosition"));
		if (hasIndeterminate == null) {
			endPos = stripText(period.selectChildren(GML_END_POSITION));
		} else {
			endPos = stripText(hasIndeterminate);
		}

		/*
		 * construct time in millis. for "unknown" set the MAX_VALUE
		 */
		long beginPosMs, endPosMs = 0L;
		if (beginPos.equals("unknown")) {
			beginPosMs = Long.MAX_VALUE;
		} else {
			beginPosMs = new DateTime(beginPos).getMillis();
		}

		if (endPos.equals("unknown")) {
			endPosMs = Long.MAX_VALUE;
		} else {
			endPosMs = new DateTime(endPos).getMillis();
		}
		return new Interval(beginPosMs, endPosMs);
	}

	/**
	 * @param valRef
	 *            xbeans value reference
	 * @return jodatime {@link Interval}
	 * @throws FESParseException
	 *             if unexpected error occurs
	 */
	protected Interval getTimeFromValueReference(XmlObject valRef)
			throws Exception {
		String valRefString = XmlUtil.toString(
				valRef.getDomNode().getFirstChild()).trim();

		if (valRefString.endsWith("validTime")) {
			return parseValidTime();
		}
		throw new Exception(
				"Only gml:validTime supported at the current developement state");
	}

	private Interval parseValidTime() {
		XmlObject[] valRef = this.temporalOp
				.selectChildren(VALUE_REFERENCE_QNAME);

		if (valRef.length == 2) {
			// another valRef, referenced time
			// TODO how to parse the time with a ValueReference?
		}

		else {
			XmlObject[] period = this.temporalOp
					.selectChildren(GML_TIME_PERIOD);
			if (period != null && period.length > 0) {

				String beginPos = "";
				XmlObject hasIndeterminate = period[0]
						.selectChildren(GML_BEGIN_POSITION)[0]
						.selectAttribute(new QName("", "indeterminatePosition"));
				if (hasIndeterminate == null) {
					beginPos = stripText(period[0]
							.selectChildren(GML_BEGIN_POSITION));
				} else {
					beginPos = stripText(hasIndeterminate);
				}

				String endPos = "";
				hasIndeterminate = period[0].selectChildren(GML_END_POSITION)[0]
						.selectAttribute(new QName("", "indeterminatePosition"));
				if (hasIndeterminate == null) {
					endPos = stripText(period[0]
							.selectChildren(GML_END_POSITION));
				} else {
					endPos = stripText(hasIndeterminate);
				}

				/*
				 * construct time in millis. for "unknown" set the MAX_VALUE
				 */
				long beginPosMs, endPosMs = 0L;
				if (beginPos.equals("unknown")) {
					beginPosMs = Long.MAX_VALUE;
				} else {
					beginPosMs = new DateTime(beginPos).getMillis();
				}

				if (endPos.equals("unknown")) {
					endPosMs = Long.MAX_VALUE;
				} else {
					endPosMs = new DateTime(endPos).getMillis();
				}
				return new Interval(beginPosMs, endPosMs);

			}
			XmlObject[] instant = this.temporalOp
					.selectChildren(GML_TIME_INSTANT);
			if (instant != null && instant.length > 0) {
				String timepos = stripText(instant[0]
						.selectChildren(GML_TIME_POSITION));

				long beginPosMs = new DateTime(timepos).getMillis();
				return new Interval(beginPosMs, beginPosMs);
			}
		}
		return null;
	}

	/**
	 * Strips out the text of an xml-element and returns as a String.
	 */
	private String stripText(XmlObject[] elems) {
		if (elems != null && elems.length > 0) {
			return stripText(elems[0]);
		}
		return null;
	}

	private String stripText(XmlObject elem) {
		if (elem != null) {
			Node child = elem.getDomNode().getFirstChild();
			if (child != null) {
				return XmlUtil.toString(child).trim();
			}
		}
		return null;
	}

}
