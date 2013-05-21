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
package org.n52.epos.engine.esper.concurrent;

/**
 * Interface for a timeout estimation algorithm impl used
 * by instances of {@link IConcurrentNotificationHandler}.
 * 
 * @author matthes rieke <m.rieke@52north.org>
 *
 */
public interface ITimeoutEstimation {

	/**
	 * @param timeout the initial timeout
	 */
	void setMaximumTimeout(int timeout);

	/**
	 * @param l update the data with a new measured processing period
	 */
	void updateTimeout(long l);

	/**
	 * @param l update the data with a new measured processing period
	 * @param onFailure flag for processing failure, could be used to weight periods
	 */
	void updateTimeout(long l, boolean onFailure);

	/**
	 * @return the estimated timeout calculated by the underlying algorithm
	 */
	int getCurrenTimeout();

	/**
	 * @param l the minimum timeout
	 */
	void setMinimumTimeout(int l);

}
