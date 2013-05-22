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

package org.n52.epos.engine.esper.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * ThreadPool for the execution of {@link Runnable}s.
 * Implemented as Singleton. 
 * 
 * @author Thomas Everding
 *
 */
public class ThreadPool {
	
	private static ThreadPool instance = null;
	
	private ExecutorService executor;
	
	/**
	 * 
	 * Private Constructor
	 *
	 */
	private ThreadPool() {
		this.executor = Executors.newSingleThreadExecutor(new NamedThreadFactory("EML-UpdateHandlerPool"));
	}
	
	
	/**
	 * 
	 * @return the only instance of this class
	 */
	public static synchronized ThreadPool getInstance() {
		if (instance == null) {
			instance = new ThreadPool();
		}
		
		return instance;
	}
	
	
	/**
	 * Executes a class implementing {@link Runnable}.
	 * Does not block.
	 * 
	 * @param runnable the runnable
	 */
	public synchronized void execute(Runnable runnable) {
		this.executor.submit(runnable);
	}
}
