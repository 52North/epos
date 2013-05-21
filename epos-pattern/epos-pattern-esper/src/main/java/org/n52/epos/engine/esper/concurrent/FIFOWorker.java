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

import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.n52.epos.event.MapEposEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the default concurrent implementation of the SES.
 * It internally stores received {@link QueuedMapEventCollection}s
 * in a {@link ConcurrentLinkedQueue}. 
 * 
 * @author matthes rieke <m.rieke@52north.org>
 *
 */
public class FIFOWorker implements IConcurrentNotificationHandler {

	private static final Logger logger = LoggerFactory
			.getLogger(FIFOWorker.class);

	private boolean running = true;
	private ConcurrentLinkedQueue<QueuedMapEventCollection> queue;
	private IPollListener listener;
	private Object queueWaiter = new Object();
	private long timeout = 5000;

	private int notProcessedFailureCount;

	private Thread thread;

	private boolean useIntelligentTimeout;

	private ITimeoutEstimation estimation;

	protected boolean stopped;

	public FIFOWorker() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		this.queue = new ConcurrentLinkedQueue<QueuedMapEventCollection>();
		
		ServiceLoader<ITimeoutEstimation> loader = ServiceLoader.load(ITimeoutEstimation.class);
		for (ITimeoutEstimation iTimeoutEstimation : loader) {
			this.estimation = iTimeoutEstimation;
			break;
		}
		
		//TODO use config
		this.estimation.setMinimumTimeout(500);
		this.estimation.setMaximumTimeout(5000);
	}

	/**
	 * @param listener the callback which process the message
	 * @param timeout the timeout the worker should wait until discarding a non-processed event
	 * @throws IllegalAccessException due to reflections
	 * @throws InstantiationException due to reflections
	 * @throws ClassNotFoundException due to reflections
	 */
	public FIFOWorker(IPollListener listener, int timeout) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		this();
		this.listener = listener;
		setTimeout(timeout);
	}


	private void callPollListeners(MapEposEvent alert) {
		if (this.listener != null) this.listener.onElementPolled(alert);
	}

	@Override
	public QueuedMapEventCollection insertPendingEventCollection(
			QueuedMapEventCollection result) {
		synchronized (this.queueWaiter) {
			this.queue.offer(result);
			this.queueWaiter.notifyAll();
		}
		return result;
	}

	@Override
	public void notifyOnDataAvailability(QueuedMapEventCollection event) {
	}

	@Override
	public void startWorking() {
		this.running = true;
		this.thread = new Thread(new Runnable() {

			@Override
			public void run() {
				while (FIFOWorker.this.running) {

					QueuedMapEventCollection alerts;
					synchronized (FIFOWorker.this.queueWaiter) {
						while (FIFOWorker.this.queue.isEmpty()) {
							/*
							 * is this a system shutdown?
							 */
							if (!FIFOWorker.this.running) return;

							try {
								FIFOWorker.this.queueWaiter.wait();
							} catch (InterruptedException e) {
								logger.warn(e.getMessage(), e);
							}
						}

					}

					while (!FIFOWorker.this.queue.isEmpty()) {
						if (FIFOWorker.this.stopped) return;
						
						/*
						 * get message from queue
						 */
						alerts = queue.poll();
						if (logger.isDebugEnabled())
							logger.debug("####### current queue size: "+FIFOWorker.this.queue.size());

						/*
						 * queue was empty
						 */
						if (alerts == null) continue;

						/*
						 * check if we got the next valid element,
						 * otherwise wait.
						 */
						if (!alerts.getFuture().isDone()) {
							try {
								/*
								 * wait the timeout time and check again one time,
								 * else discard
								 */
								long t = FIFOWorker.this.getCurrenTimeout();

								if (logger.isDebugEnabled()) {
									logger.debug("current estimated timeout: "+t);
								}
								try {
									if (alerts.getFuture().get(t, TimeUnit.MILLISECONDS) != null) {
										/*
										 * we have waited the timeout period,
										 * processing did not take place.
										 * discard this one and go for the next.
										 */
										alerts.getFuture().cancel(true);
										FIFOWorker.this.notProcessedFailureCount++;
										continue;
									}

								} catch (ExecutionException e) {
									logger.warn(e.getMessage());
									FIFOWorker.this.notProcessedFailureCount++;
									continue;
								} catch (TimeoutException e) {
									logger.warn(e.getMessage());
									FIFOWorker.this.notProcessedFailureCount++;
									continue;
								}

								/*
								 * update the timeout
								 */
								if (FIFOWorker.this.useIntelligentTimeout) {
									alerts.setElapsedTime();
									estimation.updateTimeout(alerts.getElapsedTime(), true);
								}

							} catch (InterruptedException e) {
								logger.warn(e.getMessage(), e);
							}
						}

						if (!alerts.isFilled()) {
							/*
							 * it was cancelled, ignore it
							 */
							FIFOWorker.this.notProcessedFailureCount++;
							continue;
						}

						/*
						 * update the timeout
						 */
						if (FIFOWorker.this.useIntelligentTimeout) {
							estimation.updateTimeout(alerts.getElapsedTime());
						}

						/*
						 * we polled a processed element, yay!
						 * forward it to our listener
						 */
						if (alerts.getCollection() != null) {
							for (MapEposEvent alert : alerts.getCollection()) {
								if (logger.isInfoEnabled()) {
									logger.info(alert.toString());
								}

								callPollListeners(alert);
							}	
						}
					}
				}
			}
		});
		this.thread.start();
	}

	protected long getCurrenTimeout() {
		if (this.useIntelligentTimeout) {
			return this.estimation.getCurrenTimeout();
		}
		return this.timeout;
	}

	@Override
	public void stopWorking() {
		this.stopped = true;
		this.running = false;
		synchronized (this.queueWaiter) {
			this.queueWaiter.notifyAll();
		}
	}

	@Override
	public int getNotProcessedFailureCount() {
		return this.notProcessedFailureCount;
	}

	@Override
	public void resetFailures() {
		this.notProcessedFailureCount = 0;
	}

	@Override
	public void joinUntilEmpty() {

		this.running = false;
		synchronized (this.queueWaiter) {
			this.queueWaiter.notifyAll();
		}
		try {
			this.thread.join();
		} catch (InterruptedException e) {
			logger.warn(e.getMessage(), e);
		}
		startWorking();

	}

	@Override
	public void setTimeout(long l) {
		this.timeout = l;
		if (this.estimation != null) {
			this.estimation.setMaximumTimeout((int) timeout);
		}
	}

	@Override
	public void setPollListener(IPollListener pl) {
		this.listener = pl;
	}

	@Override
	public void setUseIntelligentTimeout(boolean b) {
		this.useIntelligentTimeout = b;
	}

}