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
package org.n52.epos.engine.esper.concurrent;

import org.n52.epos.event.MapEposEvent;

/**
 * Interface for a concurrent Notification processor.
 * An implementation should inform at least one implementation
 * of {@link IPollListener} about availability of inserted
 * {@link QueuedMapEventCollection}. These get claimed before
 * the actual processing using the {@link #insertPendingEventCollection(QueuedMapEventCollection)}
 * method.
 * 
 * @author matthes rieke <m.rieke@52north.org>
 *
 */
public interface IConcurrentNotificationHandler {
	
	/**
	 * notify the worker that new data is available
	 * @param coll the collection which has been processed
	 */
	public void notifyOnDataAvailability(QueuedMapEventCollection coll);

	/**
	 * start the concurrent processing
	 */
	public void startWorking();

	/**
	 * stop the concurrent processing
	 */
	public void stopWorking();
	
	/**
	 * @param coll the collection of MapEvents to be filled
	 * @return an empty collection of {@link MapEvent}s. This will get filled after
	 * processing has finished. 
	 */
	public QueuedMapEventCollection insertPendingEventCollection(QueuedMapEventCollection coll);

	/**
	 * @return number of errors due to unprocessed elements
	 */
	public int getNotProcessedFailureCount();

	/**
	 * reset the failure counters
	 */
	public void resetFailures();

	/**
	 * Join the working thread until all elements got processed and forwarded
	 * to the {@link IPollListener} implementation.
	 */
	public void joinUntilEmpty();

	/**
	 * @param l the timeout the handler should wait for messages to be processed
	 */
	public void setTimeout(long l);
	
	/**
	 * @param pl the impl of {@link IPollListener}, getting called for each event item
	 */
	public void setPollListener(IPollListener pl);
	
	
	/**
	 * set this flag to enable training an benchmarking of timeout deltas
	 * by taking actual processing periods into account.
	 * @param b true?
	 */
	public void setUseIntelligentTimeout(boolean b);
	
	
	/**
	 * A listener interface which gets called when
	 * a MapEvent is processed.
	 * 
	 * @author matthes rieke <m.rieke@52north.org>
	 *
	 */
	public static interface IPollListener {
		
		/**
		 * @param alert the next available MapEvent 
		 */
		public void onElementPolled(MapEposEvent alert);
		
	}


}
