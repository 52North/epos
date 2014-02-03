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
