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
package org.n52.epos.transform;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;

/**
 * {@link ServiceLoader}-enabled interface for providing
 * implementations of {@link MessageTransformer} repositories
 * 
 * @author matthes rieke
 *
 * @param <O> output type of the repository implementation
 */
public interface TransformationRepsitory<O> {

	/**
	 * Transform an object to the desired output type.
	 * 
	 * @param input the input object
	 * @return the input object transformed to type O
	 * @throws TransformationException if the transformation failed
	 */
	public O transform(Object input) throws TransformationException;

	/**
	 * @param input the input class
	 * @return true if and only if the input object can be transformed to the desired
	 * output type O
	 */
	public boolean supportsInput(Object input);

	/**
	 * @return a set of supported output classes
	 */
	public Set<Class<?>> getSupportedOutputs();

	
	/**
	 * The Instance class providing access to the implementing
	 * instances of {@link TransformationRepsitory}.
	 * 
	 * @author matthes rieke
	 *
	 */
	public static class Instance {

		private static List<TransformationRepsitory<?>> repos;

		static {
			@SuppressWarnings("rawtypes")
			ServiceLoader<TransformationRepsitory> loader = ServiceLoader
					.load(TransformationRepsitory.class);

			repos = new ArrayList<TransformationRepsitory<?>>();
			for (TransformationRepsitory<?> transformationRepsitory : loader) {
				repos.add(transformationRepsitory);
			}
		}

		private static List<TransformationRepsitory<?>> getRepositories(
				Class<?> out) {
			List<TransformationRepsitory<?>> result = new ArrayList<TransformationRepsitory<?>>();

			Set<Class<?>> outputClasses;
			for (TransformationRepsitory<?> t : repos) {
				outputClasses = t.getSupportedOutputs();
				if (outputClasses != null && outputClasses.contains(out)) {
					result.add(t);
				}
			}

			return result;
		}

		/**
		 * @param input the input object
		 * @param outputClass the desired output class type
		 * @return the transformed object as a type of outputClass
		 * @throws Exception if no matching transformer is available
		 */
		public static Object transform(Object input, Class<?> outputClass)
				throws TransformationException {
			List<TransformationRepsitory<?>> repos = getRepositories(outputClass);
			for (TransformationRepsitory<?> t : repos) {
				if (t.supportsInput(input)) {
					return t.transform(input);
				}

			}

			throw new TransformationException(
					"No transformer with Input '" + input.getClass().getName()
							+ "' and Output '" + outputClass.getName()
							+ "' available.");
		}

	}

}
