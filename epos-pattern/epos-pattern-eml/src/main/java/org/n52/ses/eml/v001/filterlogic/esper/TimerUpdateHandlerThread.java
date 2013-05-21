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
///**
// * Part of the diploma thesis of Thomas Everding.
// * @author Thomas Everding
// */
//
//package de.ifgi.lehre.thesisEverding.eml.esper;
//
//import java.util.Date;
//import java.util.Vector;
//
//import com.espertech.esper.client.EventBean;
//
//import de.ifgi.lehre.thesisEverding.eml.event.MapEvent;
//import de.ifgi.lehre.thesisEverding.eml.pattern.Statement;
//
//
///**
// * Handles updates from a {@link TimerListener}.
// * 
// * @author Thomas Everding
// * 
// */
//public class TimerUpdateHandlerThread implements Runnable {
//	
//	private Statement statement;
//	
//	private EventBean bean;
//	
//	private EsperController controller;
//	
//	private String internalEventName;
//	
////	private boolean doOutput;
//	
////	private TimerListener listener;
//	
//	
//	/**
//	 * 
//	 * Constructor
//	 *
//	 * @param controller {@link EsperController} 
//	 * @param statement {@link Statement} of the listener
//	 * @param internalEventName internal name of the event
//	 * @param bean the event update
//	 */
//	public TimerUpdateHandlerThread(EsperController controller, Statement statement, String internalEventName, EventBean bean) {
//		this.bean = bean;
//		this.controller = controller;
//		this.statement = statement;
//		this.internalEventName = internalEventName;
//	}
//	
//	
//	@SuppressWarnings("unchecked")
//	@Override
//	public void run() {
//		//event received, publish new event for next timer pattern match
//		Date now = new Date();
//		MapEvent event = new MapEvent(now.getTime(), now.getTime());
//		event.put(MapEvent.VALUE_KEY, now.getTime());
//		
//		//create causality if wanted
//		if (this.statement.getSelectFunction().getCreateCausality()) {
//			MapEvent underlying = (MapEvent) bean.getUnderlying();
//			
//			Vector<MapEvent> underlyingCausality = (Vector<MapEvent>) underlying.get(MapEvent.CAUSALITY_KEY);
//			
//			//add causality of underlying event
//			for (MapEvent e : underlyingCausality) {
//				event.addCausalAncestor(e);
//			}
//			
//			//add underlying event to causality
//			event.addCausalAncestor(underlying);
//		}
//		
//		//publish to make next match possible
//		this.controller.sendEvent(this.internalEventName, event);
//		
////		if (doOutput) {
////			listener.doOutput(event);
////		}
//	}
//	
//}
