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
package org.n52.ses.eml.v001.filter.temporal;

import javax.xml.namespace.QName;

import org.n52.ses.eml.v001.filter.IFilterElement;

import net.opengis.fes.x20.TemporalOpsType;

/**
 * Factory that builds the temporal filter objects.
 *
 */
public class TemporalFilterFactory {
	
	private static final String FES_NAMESPACE = "http://www.opengis.net/fes/2.0";
	
	private static final QName BEFORE_QNAME = new QName(FES_NAMESPACE, "Before");
	private static final QName AFTER_QNAME = new QName(FES_NAMESPACE, "After");
	private static final QName MEETS_QNAME = new QName(FES_NAMESPACE, "Meets");
	private static final QName MET_BY_QNAME = new QName(FES_NAMESPACE, "MetBy");
	private static final QName ANY_INTERACTS_QNAME = new QName(FES_NAMESPACE, "AnyInteracts");

	
	/**
	 * Builds the temporal filter objects
	 * 
	 * @param temporalOps FES temporal operator
	 * 
	 * @return object representing the temporal operator
	 */
	//TODO: property names not necessary (compare to ALogicalFilter.FACTORY)?
	public IFilterElement buildTemporalFilter(TemporalOpsType temporalOps) {
		QName tOpName = temporalOps.newCursor().getName();
		
		if (tOpName.equals(BEFORE_QNAME)) {
			return new BeforeFilter(temporalOps);
		}
		
		if (tOpName.equals(AFTER_QNAME)) {
			return new AfterFilter(temporalOps);
		}
		
		if (tOpName.equals(MEETS_QNAME)) {
			return new MeetsFilter(temporalOps);
		}
		
		if (tOpName.equals(MET_BY_QNAME)) {
			return new MetByFilter(temporalOps);
		}
		if (tOpName.equals(ANY_INTERACTS_QNAME)) {
			return new AnyInteractsFilter(temporalOps);
		}
		
		return null;
	}

}
