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

/**
 * Class providing an estimation for future processing timeouts.
 * It stores ARRAY_SIZE processing periods and calculates on that basis
 * an IDW interpolated estimated timeout. This implementation could
 * in the worst case lead to loss of messages but provides adequate performance.
 * 
 * @author matthes rieke <m.rieke@52north.org> 
 *
 */
public abstract class PredictedTimeoutEstimation implements ITimeoutEstimation {

	protected static final int ARRAY_SIZE = 50;
	protected static final double IDW_POWER = 1;
	protected int[] timeouts = new int[ARRAY_SIZE];
	protected int currentPos = 0;
	protected int fixedMinimum;
	protected int initialTimeout;

	public PredictedTimeoutEstimation() {
	}

	@Override
	public void setMaximumTimeout(int timeout) {
		this.initialTimeout = timeout;
		for (int i = 0; i < this.timeouts.length; i++) {
			this.timeouts[i] = timeout;
		}
	}
	
	@Override
	public void setMinimumTimeout(int l) {
		this.fixedMinimum = l;		
	}

	@Override
	public void updateTimeout(long l) {
		updateTimeout(l, false);
	}

	@Override
	public void updateTimeout(long l, boolean onFailure) {
		if (onFailure) {
			/*
			 * weight failure deltas heavier than normal
			 */
			l = Math.min(this.initialTimeout, l);
		}
		this.timeouts[currentPos] = (int) l;
		this.currentPos = (this.currentPos+1) % ARRAY_SIZE;
	}

	@Override
	public abstract int getCurrenTimeout();


	public static void main(String[] args) {
		PredictedTimeoutEstimation t = new IDWTimeoutEstimation();
		t.setMinimumTimeout(500);
		t.setMaximumTimeout(5000);
		for (int i = 0; i < ARRAY_SIZE *1.5; i++) {
			t.updateTimeout(10*i);
		}
		System.out.println(t.getCurrenTimeout());
	}

	public static class IDWTimeoutEstimation extends PredictedTimeoutEstimation {

		
		@Override
		public int getCurrenTimeout() {
			int result = 0;
			/*
			 * IDW
			 */
			double sum = 0;
			for (int i = 0; i < this.timeouts.length; i++) {
				double distance = (i-currentPos);
				if (distance > 0) {
					distance = ARRAY_SIZE - distance;
				} else if (distance < 0 ){
					distance = Math.abs(distance);
				} else {
					distance = ARRAY_SIZE;
				}

				distance = 1.0 / Math.pow(distance, IDW_POWER);

				sum += distance;
			}

			for (int i = 0; i < this.timeouts.length; i++) {
				/*
				 * weight the values regarding distance to current position.
				 * normalized by ARRAY_SIZE
				 */
				double distance = (i-currentPos);
				if (distance > 0) {
					distance = ARRAY_SIZE - distance;
				} else if (distance < 0 ){
					distance = Math.abs(distance);
				} else {
					distance = ARRAY_SIZE;
				}

				distance = 1.0 / Math.pow(distance, IDW_POWER);

				double weighted = this.timeouts[i] * (distance / sum);
				//						System.out.println( ((i == currentPos) ? "CURRENT!!" : "") + distance+"; orig="+this.timeouts[i]+"; weighting: "+weighted);
				result += weighted;
			}
			return Math.max(fixedMinimum, result);
		}

	}
	
	
	public static class AverageTimeoutEstimation extends PredictedTimeoutEstimation {
		
		
		@Override
		public int getCurrenTimeout() {
			int result = 0;
			for (int i = 0; i < this.timeouts.length; i++) {
				result += this.timeouts[i];
			}
			return Math.max(fixedMinimum, result / this.timeouts.length);
		}
	}
}
