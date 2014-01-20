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
 * This program is free software; you can redistribute and/or modify it under
 * the terms of the GNU General Public License version 2 as published by the
 * Free Software Foundation.
 *
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied
 * WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program (see gnu-gpl v2.txt). If not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
 * visit the Free Software Foundation web page, http://www.fsf.org.
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
