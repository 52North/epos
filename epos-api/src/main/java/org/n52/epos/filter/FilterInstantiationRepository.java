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
package org.n52.epos.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author matthes rieke
 *
 * @param <I> the input type (=encoding) of the rule
 */
public interface FilterInstantiationRepository {

	public EposFilter instantiateFrom(Object input) throws Exception;


	/**
	 * @return a set of supported input types
	 */
	public Class<?> getSupportedInput();
	
	/**
	 * The Instance class providing access to the implementing
	 * instances of {@link FilterInstantiationRepository}.
	 * 
	 * @author matthes rieke
	 *
	 */
	public static class Instance {
		
		private static final Logger logger = LoggerFactory.getLogger(Instance.class);
		private static List<FilterInstantiationRepository> repos;

		static {
			ServiceLoader<FilterInstantiationRepository> loader = ServiceLoader
					.load(FilterInstantiationRepository.class);

			repos = new ArrayList<FilterInstantiationRepository>();
			for (FilterInstantiationRepository filterRepo : loader) {
				repos.add(filterRepo);
			}
		}

		private static List<FilterInstantiationRepository> getRepositories(
				Class<?> inputClass) {
			List<FilterInstantiationRepository> result = new ArrayList<FilterInstantiationRepository>();

			Class<?> supportedInput;
			for (FilterInstantiationRepository t : repos) {
				supportedInput = t.getSupportedInput();
				if (supportedInput != null && supportedInput.isAssignableFrom(inputClass)) {
					logger.debug("Found implementation: {}", t.getClass().getCanonicalName());
					result.add(t);
				}
			}

			return result;
		}

		/**
		 * @param input the input object
		 * @return the {@link EposFilter} instance
		 * @throws Exception if no matching transformer is available
		 */
		public static EposFilter instantiate(Object input)
				throws FilterInstantiationException {
			List<FilterInstantiationRepository> repos = getRepositories(input.getClass());
			for (FilterInstantiationRepository t : repos) {
				try {
					return t.instantiateFrom(input);
				} catch (Exception e) {
					logger.warn(e.getMessage(), e);
					logger.warn("Skipping FilterInstnationRepository: "+ t.getClass().getName());
				}
			}

			throw new FilterInstantiationException(
					"No FilterInstantiationRepository with supported Input '" + input.getClass().getName()
							+ "' available.");
		}

	}
}
