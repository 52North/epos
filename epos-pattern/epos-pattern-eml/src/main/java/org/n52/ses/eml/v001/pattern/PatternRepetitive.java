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

import java.util.HashMap;

import org.n52.epos.pattern.eml.ILogicController;
import org.n52.ses.eml.v001.Constants;
import org.n52.ses.eml.v001.filterlogic.EMLParser;


/**
 * represents a repetitive pattern
 * 
 * @author Thomas Everding
 *
 */
public class PatternRepetitive extends APattern{ 
	
	private int repetitionCount;
	
	private String patternToRepeatID;
	
	private ILogicController controller;

	private int selectFunctionNumber;

	private String inputEventName;

	private EMLParser parser;
	
	/**
	 * @return the repetitionCount
	 */
	public int getRepetitionCount() {
		return this.repetitionCount;
	}

	/**
	 * @param repetitionCount the repetitionCount to set
	 */
	public void setRepetitionCount(int repetitionCount) {
		this.repetitionCount = repetitionCount;
	}

	/**
	 * @return the patternToRepeatID
	 */
	public String getPatternToRepeatID() {
		return this.patternToRepeatID;
	}

	/**
	 * @param patternToRepeatID the patternToRepeatID to set
	 */
	public void setPatternToRepeatID(String patternToRepeatID) {
		this.patternToRepeatID = patternToRepeatID;
	}

	@Override
	public Statement[] createEsperStatements() {
		//two statements needed per select function
		Statement[] result = new Statement[this.selectFunctions.size() +1];
		
		if (this.controller != null) {
			//get input event name
			this.inputEventName = this.controller.getNewEventName(this.patternToRepeatID, this.selectFunctionNumber);			
		}
		
		else if (this.parser != null) {
			this.inputEventName = getNewEventNameWithParser(/*this.patternToRepeatID, */this.selectFunctionNumber);
			
		}

		
		/*
		 * build statements
		 */
		String selectClause;
		String fromClause;
		SelFunction sel;
		Statement stat;
		
		//first statement: counting
		selectClause = Constants.EPL_SELECT
					   + " * ";
		fromClause = Constants.EPL_FROM
					 + " "
					 + this.inputEventName
					 + ".win:length_batch("
					 + this.repetitionCount
					 + ")";
		
		stat = new Statement();
		stat.setSelectFunction(null);
		stat.setStatement(selectClause + fromClause);
		
		//add first statement
		result[0] = stat;

		//further statements: selecting
		fromClause = Constants.EPL_FROM
		 + " "
		 + Constants.EPL_PATTERN
		 + " [every ("
		 + this.inputEventName
		 + Constants.REPETIVITE_COUNT_EVENT_SUFFIX
		 + " -> "
		 + this.inputEventName
		 + " = "
		 + this.inputEventName
		 + ")]";
		
		for (int i = 1; i < result.length; i ++) {
			sel = this.selectFunctions.get(i - 1);
			
			selectClause = Constants.EPL_SELECT
			   + " ";
			
//			if (sel.getFunctionName().equals(Constants.FUNC_SELECT_EVENT_NAME)) {
//				selectClause += inputEventName 
//								+ " as value ";
//			}
//			else {
			selectClause += sel.getSelectString(false);
//			}
			selectClause += " ";
			
			stat = new Statement();
			stat.setSelectFunction(sel);
			stat.setStatement(selectClause + fromClause);
			
			//add statement
			result[i] = stat;
		}
		
		return result;
	}
	
	
	/**
	 * Method for creating statements using just an EMLParser instead 
	 * of EsperController. (added by Matthes)
	 * 
	 * @param selFunctionNumber number of the select function
	 */
//	private String getNewEventNameWithParser(String secondPatternID2, int selectFunctionNumber) {
	private String getNewEventNameWithParser(int selFunctionNumber) {
		int sFN = selFunctionNumber;
		//get all patterns from parser
		HashMap<String, APattern> patterns = this.parser.getPatterns();
		
		//search for pattern
		if (!this.parser.getPatterns().containsKey(this.patternID)) {
			return null;
		}
		APattern pattern = patterns.get(this.patternID);
		
		//search for select function
		if (!(pattern.getSelectFunctions().size() > sFN)) {
			if (!(pattern.getSelectFunctions().size() >= 0)) {
				return null;
			}
			//set number to 0
			sFN = 0;
		}
		
		//return newEventName
		return pattern.getSelectFunctions().get(sFN).getNewEventName();
	}

	@Override
	public Statement[] createEsperStatements(EMLParser p) {
		this.parser = p;
		return createEsperStatements();
	}

	/**
	 * @param logicController the controller to set
	 */
	public void setController(ILogicController logicController) {
		this.controller = logicController;
	}

	
	/**
	 * sets the select function to use
	 * 
	 * @param selectFunctionNumber number of the select function
	 */
	public void setSelectFunctionToUse(int selectFunctionNumber) {
		this.selectFunctionNumber = selectFunctionNumber;
	}

	/**
	 * @return the inputEventName
	 */
	public String getInputEventName() {
		return this.inputEventName;
	}

	
	/**
	 * @return the selectFunctionNumber of the pattern to repeat
	 */
	public int getSelectFunctionNumber() {
		return this.selectFunctionNumber;
	}
}
