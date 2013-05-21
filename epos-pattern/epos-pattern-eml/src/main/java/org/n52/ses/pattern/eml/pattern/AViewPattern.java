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

package org.n52.ses.pattern.eml.pattern;


/**
 * superclass for all patterns with views
 * 
 * @author Thomas Everding
 *
 */
public abstract class AViewPattern extends APattern{
	
	/**
	 * the view (used for esper)
	 */
	protected DataView view;

	/**
	 * @return the view
	 */
	public DataView getView() {
		return this.view;
	}

	/**
	 * sets the view
	 * 
	 * @param view the view to set
	 */
	public void setView(DataView view) {
		this.view = view;
	}
}
