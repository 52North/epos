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

import org.apache.xmlbeans.XmlObject;
import org.joda.time.Interval;
import org.n52.epos.event.MapEposEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.opengis.fes.x20.TemporalOpsType;

/**
 * Temporal operator that checks the temporal 'met by# condition. 
 *
 */
public class MetByFilter extends ATemporalFilter {

	private static final Logger logger = LoggerFactory
			.getLogger(MetByFilter.class);
	
	/**
	 * 
	 * Constructor
	 *
	 * @param temporalOps FES temporal operator
	 */
	public MetByFilter(TemporalOpsType temporalOps) {
		super(temporalOps);
	}

	@Override
	public String createExpressionString(boolean complexPatternGuard) {
		StringBuilder sb = new StringBuilder();
		
		XmlObject[] valRef = this.temporalOp.selectChildren(VALUE_REFERENCE_QNAME);
		
		Interval time = null;
		if (valRef != null) {
			try {
				time = getTimeFromValueReference(valRef[0]);
			} catch (Exception e) {
				//TODO log exception and throw
				logger.warn(e.getMessage(), e);
			}
		}

		if (time != null) {
			sb.append(MapEposEvent.START_KEY +" = "+ time.getEndMillis());
		}
		return sb.toString();
	}

	@Override
	public void setUsedProperty(String nodeValue) {
		/*empty*/
	}

}
