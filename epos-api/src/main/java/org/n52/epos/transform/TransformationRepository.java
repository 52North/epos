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
package org.n52.epos.transform;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link ServiceLoader}-enabled interface for providing
 * implementations of {@link MessageTransformer} repositories
 * 
 * @author matthes rieke
 *
 * @param <O> output type of the repository implementation
 */
public interface TransformationRepository<O> {

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
	 * instances of {@link TransformationRepository}.
	 * 
	 * @author matthes rieke
	 *
	 */
	public static class Instance {

		private static final Logger logger = LoggerFactory.getLogger(Instance.class);
		private static List<TransformationRepository<?>> repos;

		static {
			@SuppressWarnings("rawtypes")
			ServiceLoader<TransformationRepository> loader = ServiceLoader
					.load(TransformationRepository.class);

			repos = new ArrayList<TransformationRepository<?>>();
			for (TransformationRepository<?> transformationRepsitory : loader) {
				repos.add(transformationRepsitory);
			}
		}

		private static List<TransformationRepository<?>> getRepositories(
				Class<?> out) {
			List<TransformationRepository<?>> result = new ArrayList<TransformationRepository<?>>();

			Set<Class<?>> outputClasses;
			for (TransformationRepository<?> t : repos) {
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
		public static <T> T transform(Object input, Class<? extends T> outputClass)
				throws TransformationException {
			List<TransformationRepository<?>> repos = getRepositories(outputClass);
			for (TransformationRepository<?> t : repos) {
				if (t.supportsInput(input)) {
					logger.debug("Using {} TransformationRepository", t.getClass().getName());
					Object result = t.transform(input);
					return (T) result;
				}

			}

			throw new TransformationException(
					"No transformer with Input '" + input.getClass().getName()
							+ "' and Output '" + outputClass.getName()
							+ "' available.");
		}

	}

}
