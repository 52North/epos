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
/**
 * Part of the diploma thesis of Thomas Everding.
 * @author Thomas Everding
 */

package org.n52.ses.eml.v001.filter.logical;

import org.n52.ses.eml.v001.filter.IFilterElement;

/**
 * Representation of a logical filter operation
 * 
 * @author Thomas Everding
 * 
 */
public abstract class ALogicFilter implements IFilterElement {

	/**
	 * Factory to build new logic filters.
	 */
	public static final LogicFilterFactory FACTORY = new LogicFilterFactory();

	/**
	 * the used property of this filter element
	 */
	protected String usedProperty = null;

	@Override
	public void setUsedProperty(String nodeValue) {
		this.usedProperty = nodeValue;
	}
}
