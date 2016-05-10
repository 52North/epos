/**
 * Copyright (C) 2013-2014 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 as published
 * by the Free Software Foundation.
 *
 * If the program is linked with libraries which are licensed under one of
 * the following licenses, the combination of the program with the linked
 * library is not considered a "derivative work" of the program:
 *
 *     - Apache License, version 2.0
 *     - Apache Software License, version 1.0
 *     - GNU Lesser General Public License, version 3
 *     - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *     - Common Development and Distribution License (CDDL), version 1.0
 *
 * Therefore the distribution of the program linked with libraries licensed
 * under the aforementioned licenses, is permitted by the copyright holders
 * if the distribution is compliant with both the GNU General Public
 * icense version 2 and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
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
                                logger.info("New FilterInstantiationRepository: {}, supportedInput={}",
                                        filterRepo.getClass().getCanonicalName(), filterRepo.getSupportedInput());
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
		 * @throws FilterInstantiationException if no matching transformer is available
		 */
		public static EposFilter instantiate(Object input)
				throws FilterInstantiationException {
			List<FilterInstantiationRepository> repos = getRepositories(input.getClass());
			for (FilterInstantiationRepository t : repos) {
				try {
					return t.instantiateFrom(input);
				} catch (Exception e) {
					logger.warn(e.getMessage(), e);
					logger.warn("Skipping FilterInstantiationRepository: "+ t.getClass().getName());
				}
			}

			throw new FilterInstantiationException(
					"Could not instantiate Filter for Input '" + input.getClass().getName()
							+ "'");
		}

	}
}
