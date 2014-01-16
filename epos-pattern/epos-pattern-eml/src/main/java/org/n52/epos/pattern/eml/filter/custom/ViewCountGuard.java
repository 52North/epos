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
package org.n52.epos.pattern.eml.filter.custom;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;

import javax.xml.namespace.QName;

import net.opengis.fes.x20.BinaryComparisonOpType;
import net.opengis.fes.x20.ComparisonOpsType;
import net.opengis.fes.x20.FilterType;
import net.opengis.fes.x20.LiteralType;
import net.opengis.swe.x101.CountDocument.Count;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.n52.epos.pattern.CustomStatementEvent;
import org.n52.epos.pattern.eml.Constants;
import org.n52.epos.pattern.eml.filter.IFilterElement;


public class ViewCountGuard extends CustomGuardFilter {
	
	private static final QName EQUAL_QNAME = new QName("http://www.opengis.net/fes/2.0", "PropertyIsEqualTo");
	private static List<CustomStatementEvent> customEvents = new ArrayList<CustomStatementEvent>();
	private FilterType guard;
	
	static {
		ServiceLoader<CustomStatementEvent> loader = ServiceLoader.load(CustomStatementEvent.class);
		
		for (CustomStatementEvent customStatementEvent : loader) {
			if (customStatementEvent.bindsToEvent(CustomStatementEvent.REMOVE_VIEW_COUNT_EVENT)) {
				customEvents.add(customStatementEvent);
			}
		}
	}

	public ViewCountGuard(FilterType guard) {
		this.guard = guard;
		
	}
	
	@Override
	public String createExpressionString(boolean complexPatternGuard) {
		ComparisonOpsType ops = this.guard.getComparisonOps();
		
		StringBuilder sb = new StringBuilder();
		sb.append(" count(*) = ");
		
		sb.append(findCount(convertToBinaryComparisonOp(ops).getExpressionArray()));
		
		return sb.toString();
	}
	
	protected static BinaryComparisonOpType convertToBinaryComparisonOp(ComparisonOpsType ops) {
		QName coQName = ops.newCursor().getName();
		
		if (EQUAL_QNAME.equals(coQName)) {
			BinaryComparisonOpType bcop = (BinaryComparisonOpType) ops;
			return bcop;
		}
		
		return null;
	}

	private String findCount(XmlObject[] expressionArray) {
		for (XmlObject xo : expressionArray) {
			if (xo instanceof LiteralType) {
				XmlCursor cur = xo.newCursor();
				cur.toFirstChild();
				if (cur.getObject() instanceof Count) {
					return Integer.toString(((Count) cur.getObject()).getValue().intValue());
				}
			}
		}
		return null;
	}

	@Override
	public void setUsedProperty(String nodeValue) {
	}

	@Override
	public String getEPLClauseOperator() {
		return Constants.EPL_HAVING;
	}

	@Override
	public List<CustomStatementEvent> getCustomStatementEvents() {
		return customEvents;
	}
	
	public static class Factory implements CustomGuardFactory {

		private static final String VIEW_COUNT = "VIEW_COUNT";

		@Override
		public boolean supports(FilterType filter, Set<Object> propertyNames) {
			if (!filter.isSetComparisonOps()) return false;
			
			BinaryComparisonOpType bcops = convertToBinaryComparisonOp(filter.getComparisonOps());
			if (bcops == null) return false;
			
			return findMagicValueReference(bcops.getExpressionArray()) != null;
		}
		
		private String findMagicValueReference(XmlObject[] expressionArray) {
			for (XmlObject xo : expressionArray) {
				if (xo instanceof XmlString) {
					String val = ((XmlString) xo).getStringValue().trim();
					if (val.equals(VIEW_COUNT)) return val;
				}
			}
			return null;
		}

		@Override
		public IFilterElement createInstance(FilterType filter,
				Set<Object> propertyNames) {
			return new ViewCountGuard(filter);
		}
		
	}


}
