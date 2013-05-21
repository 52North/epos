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

package org.n52.ses.eml.v001.filterlogic.esper;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.espertech.esper.client.*;
import com.espertech.esper.client.EventBean;

/**
 * Statement aware update listener to debug the plugin
 * 
 * @author Thomas Everding
 *
 */
public class DebugListener implements StatementAwareUpdateListener{
	
	private static final Logger logger = LoggerFactory
			.getLogger(DebugListener.class);

	@Override
	public void update(EventBean[] newEvents, EventBean[] oldEvents, EPStatement epStatement, EPServiceProvider serviceProvider) {
		DebugListener.logger.debug("");
		DebugListener.logger.debug("-------------------------------");
		DebugListener.logger.debug("Update received for statement:");
		DebugListener.logger.debug("\tname: " + epStatement.getName());
		DebugListener.logger.debug("\ttext: " + epStatement.getText());
		
		if (newEvents != null) {
			DebugListener.logger.debug("new events:");
			DebugListener.logger.debug("\tsize: " + newEvents.length);
			
			for (int i = 0; i < newEvents.length; i++) {
				DebugListener.logger.debug("\tnumber " + i);
				DebugListener.logger.debug("\t\tbean:  " + newEvents[i]);
				
				try{
					Object obj = newEvents[i].get("value");
					DebugListener.logger.debug("\t\tvalue: " + obj);
				}
				catch (Throwable t) {/*empty*/}
			}
		}
		else {
			DebugListener.logger.debug("new events are null");
		}
		
		if (oldEvents != null) {
			DebugListener.logger.debug("old events:");
			DebugListener.logger.debug("\tsize: " + oldEvents.length);
			
			for (int i = 0; i < oldEvents.length; i++) {
				DebugListener.logger.debug("\tnumber " + i);
				DebugListener.logger.debug("\t\tbean: " + oldEvents[i]);
			}
		}
		else {
			DebugListener.logger.debug("old events are null");
		}
		
		DebugListener.logger.debug("service provider: ");
		DebugListener.logger.debug("\t" + serviceProvider);
		
		DebugListener.logger.debug("-------------------------------");
	}
	
}
