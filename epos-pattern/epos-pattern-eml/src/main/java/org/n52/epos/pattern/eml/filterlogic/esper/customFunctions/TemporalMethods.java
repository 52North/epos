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
package org.n52.epos.pattern.eml.filterlogic.esper.customFunctions;

import org.n52.epos.event.MapEposEvent;





/**
 * provides special temporal methods
 * 
 * @author Thomas Everding
 *
 */
public class TemporalMethods {
	
	/**
	 * String that separates two long values in an interval string
	 */
	public static final String INTERVAL_SEPARATOR = "until";
	
	
	/**
	 * This method tests if a time lies within an interval
	 * (borders included). It implements the FES2.0 AnyInteracts
	 * operator.
	 * @param eventObj the event whose time is checked for interactions
	 * @param timeProperty the property name of the time 
	 * @param intersectionInterval the time interval on which the interaction is tested, format: long|long
	 * 
	 * @return true if the testTime lies (partly) in the interval
	 */
	public static boolean anyInteracts (Object eventObj, Object timeProperty, Object intersectionInterval) {
		MapEposEvent event = (MapEposEvent) eventObj;
		
		String test = event.get(timeProperty).toString();
		
		if (test.contains(INTERVAL_SEPARATOR)) {
			//interval vs. interval
			String[] startEnd = test.split(INTERVAL_SEPARATOR);
			long testLS = Long.parseLong(startEnd[0]);
			long testLE = Long.parseLong(startEnd[1]);
			
			startEnd = intersectionInterval.toString().split(INTERVAL_SEPARATOR);
			long intervalS = Long.parseLong(startEnd[0]);
			long intervalE = Long.parseLong(startEnd[1]);
			
			return intersectsIntervals(testLS, testLE, intervalS, intervalE);
		}
		//instant vs. interval
		long testL = Long.parseLong(test);
		
		String[] startEnd = intersectionInterval.toString().split(INTERVAL_SEPARATOR);
		long intervalS = Long.parseLong(startEnd[0]);
		long intervalE = Long.parseLong(startEnd[1]);
		
		return intersectsInstant(testL, intervalS, intervalE);
	}

	private static boolean intersectsInstant(long test, long intervalS, long intervalE) {
		if (test >= intervalS && test <= intervalE) {
			//test lies within the interval
			return true;
		}
		return false;
	}

	private static boolean intersectsIntervals(long testS, long testE, long intervalS, long intervalE) {
		if (testE <= intervalS) {
			//test interval ended too early
			return false;
		}
		if (testS >= intervalE) {
			//test interval starts too late
			return false;
		}
		
		//test interval intersect the other interval
		return true;
	}
}
