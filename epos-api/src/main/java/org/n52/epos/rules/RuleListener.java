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
package org.n52.epos.rules;

import org.n52.epos.event.EposEvent;

public interface RuleListener {

	/**
	 * This method is called whenever an {@link EposEvent}
	 * matched the Rule to which this listener is attached.
	 * 
	 * @param event the matching event
	 */
	public void onMatchingEvent(EposEvent event);

	/**
	 * This method is called whenever an {@link EposEvent}
	 * matched the Rule to which this listener is attached.
	 * Same as {@link #onMatchingEvent(EposEvent)} but an
	 * implementation should forward the desiredOutputToConsumer
	 * to the final consumer.
	 * 
	 * @param event the matching event
	 * @param desiredOutputToConsumer this output shall be forwarded
	 * to the final consumer
	 */
	public void onMatchingEvent(EposEvent event, Object desiredOutputToConsumer);

	public Object getEndpointReference();
	
}
