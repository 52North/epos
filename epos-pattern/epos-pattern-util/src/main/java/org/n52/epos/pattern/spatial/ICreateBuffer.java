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


import com.vividsolutions.jts.geom.Geometry;

/**
 * 
 * @author Matthes Rieke <m.rieke@uni-muenster.de>
 *
 */
public interface ICreateBuffer {
	
	/**
	 * Creates a buffer of the given geometry using the crs to
	 * and the distance (with ucum-code) to do it the right way ;-)
	 * 
	 * @param geom The input geometry
	 * @param distance The distance
	 * @param ucumUom Unit of measurement in UCUM-Code
	 * @param crs The CoordinateSystem
	 * @return The buffered geometry
	 */
	public abstract Geometry buffer(Geometry geom, double distance, String ucumUom, String crs);

}
