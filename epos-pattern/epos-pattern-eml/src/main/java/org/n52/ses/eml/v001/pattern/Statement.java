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

package org.n52.ses.eml.v001.pattern;

import org.n52.ses.eml.v001.filterlogic.esper.EsperController;

/**
 * contains a statement string (for esper) and possibly a SelFunction
 * 
 * @author Thomas Everding
 *
 */
public class Statement {
	
	private String statement;
	
	private SelFunction selectFunction;

	private DataView view;
	

	/**
	 * @return the statement
	 */
	public String getStatement() {
		return this.statement;
	}

	/**
	 * @param statement the statement to set
	 */
	public void setStatement(String statement) {
		this.statement = statement;
	}

	/**
	 * @return the selectFunction
	 */
	public SelFunction getSelectFunction() {
		return this.selectFunction;
	}

	/**
	 * @param selectFunction the selectFunction to set
	 */
	public void setSelectFunction(SelFunction selectFunction) {
		this.selectFunction = selectFunction;
	}

	/**
	 * Needed for last/first event workaround (see {@link EsperController}s
	 * buildListener private method.
	 * @param view the View of the statements pattern
	 */
	public void setView(DataView view) {
		this.view = view;
	}

	/**
	 * Needed for last/first event workaround (see {@link EsperController}s
	 * buildListener private method.
	 * @return the View of the statements pattern
	 */
	public DataView getView() {
		return this.view;
	}
	
	
}
