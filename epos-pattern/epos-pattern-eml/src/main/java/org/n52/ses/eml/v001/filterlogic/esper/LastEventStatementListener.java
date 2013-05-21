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
package org.n52.ses.eml.v001.filterlogic.esper;

import org.n52.epos.rules.Rule;
import org.n52.ses.eml.v001.pattern.Statement;

import com.espertech.esper.client.EventBean;

/**
 * This class extends the StatementListener. It only processes
 * the last event of an incoming event-array. This functionality
 * is close to impossible to create within esper using EPL.
 * Only constructor and the {@link StatementListener#update(EventBean[], EventBean[])}
 * method are needed.
 * 
 * @author Matthes Rieke <m.rieke@uni-muenster.de>
 *
 */
public class LastEventStatementListener extends StatementListener {

	
	/**
	 * see {@link StatementListener#StatementListener(Statement, EsperController, SubscriptionManager)}
	 */
	public LastEventStatementListener(Statement statement,
			EsperController controller, Rule sub) {
		super(statement, controller, sub);
	}

	@Override
	public void update(EventBean[] newEvents, EventBean[] oldEvents) {
		if (newEvents != null && newEvents.length > 0) {
			this.handleMatch(newEvents[newEvents.length - 1]);
		}
	}

	
	
}
