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
package org.n52.epos.transform.sos;

import org.n52.epos.event.EposEvent;
import org.n52.epos.transform.EposTransformer;
import org.n52.epos.transform.TransformationException;
import org.n52.sos.event.SosEvent;
import org.n52.sos.event.events.ObservationInsertion;
import org.n52.sos.event.events.SensorInsertion;

/**
 * built-in {@link SosEvent} to {@link EposEvent} transformer.
 * 
 * @author matthes rieke
 *
 */
public class SosEventToEposEventTransformer implements EposTransformer {

	@Override
	public EposEvent transform(Object input) throws TransformationException {
		if (input instanceof ObservationInsertion) {
			return transformFrom((ObservationInsertion) input);
		}
		else if (input instanceof SensorInsertion) {
			return transformFrom((SensorInsertion) input);
		}
		
		throw new IllegalStateException("Should never reach here!");
	}

	private EposEvent transformFrom(ObservationInsertion input) {
		return new EposEvent();
	}
	
	private EposEvent transformFrom(SensorInsertion input) {
		return new EposEvent();
	}

	@Override
	public boolean supportsInput(Class<?> input) {
		if (input == null)
			return false;
		
		if (input.isAssignableFrom(ObservationInsertion.class) ||
				input.isAssignableFrom(SensorInsertion.class)) {
			return true;
		}
		
		return false;
	}

}
