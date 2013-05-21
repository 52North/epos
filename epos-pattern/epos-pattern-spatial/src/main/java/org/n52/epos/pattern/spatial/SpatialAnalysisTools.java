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
package org.n52.epos.pattern.spatial;


import java.util.ServiceLoader;

import com.vividsolutions.jts.geom.Geometry;

public class SpatialAnalysisTools {
	

	private static ICreateBuffer bufferAnalysis;

	public static Geometry buffer(Geometry geom, double distance, String ucumUom, String crs) {
		synchronized (SpatialAnalysisTools.class) {
			if (bufferAnalysis == null) {
				bufferAnalysis = (ICreateBuffer) initializeImplementation(ICreateBuffer.class);
			}	
		}
		
		return bufferAnalysis.buffer(geom, distance, ucumUom, crs);
	}

	private static ICreateBuffer initializeImplementation(Class<ICreateBuffer> clazz) {
		ServiceLoader<ICreateBuffer> loader = ServiceLoader.load(clazz);
		for (ICreateBuffer iCreateBuffer : loader) {
			return iCreateBuffer;
		}
		
		throw new IllegalStateException("No implementation of "+ clazz + " available.");
	}
	
}
