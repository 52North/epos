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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;

import org.joda.time.DateTime;
import org.n52.epos.event.EposEvent;
import org.n52.epos.event.MapEposEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class holds all ({@link ServiceLoader}-registered)
 * implementations of {@link EposTransformer}.
 * 
 * @author matthes rieke
 */
public class TransformationToEposEventRepository implements TransformationRepository<EposEvent> {
	
	private static final Logger logger = LoggerFactory.getLogger(TransformationToEposEventRepository.class);
	private Set<EposTransformer> transformers = new HashSet<EposTransformer>();
	private Comparator<? super MessageTransformer<EposEvent>> comparator = new PriorityComparator();
	
	public TransformationToEposEventRepository() {
		ServiceLoader<EposTransformer> loader = ServiceLoader.load(EposTransformer.class);
		
		for (EposTransformer eposTransformer : loader) {
			transformers.add(eposTransformer);
		}
	}
	
	@Override
	public EposEvent transform(Object input, String contentType) throws TransformationException {
		MessageTransformer<EposEvent> trans = findTransformers(input);
		
		logger.debug("Using transformer {}", trans.getClass().getName());
		
		EposEvent result = trans.transform(input, null);
		
		if (result == null) {
			logger.warn("The resolved transformer ({}) was not able to create an event. Trying to at least provide "
					+ "the original object", trans);
			
			DateTime now = new DateTime();
			result = new MapEposEvent(now.getMillis(), now.getMillis());
			result.setValue(MapEposEvent.ORIGNIAL_OBJECT_KEY, input);
		}
		else {
			if (result.getOriginalObject() == null) {
				
				/*
				 * add original object
				 */
				result.setOriginalObject(input);
			}
		}
		
		return result;
	}
	
	private MessageTransformer<EposEvent> findTransformers(Object input) throws TransformationException {
		List<MessageTransformer<EposEvent>> candidates = new ArrayList<MessageTransformer<EposEvent>>(transformers.size());
		for (MessageTransformer<EposEvent> t : transformers) {
			if (t.supportsInput(input, null)) {
				candidates.add(t);
			}
		}
		
		if (!candidates.isEmpty()) {
			if (candidates.size() > 1) {
				Collections.sort(candidates, comparator);
			}
			return candidates.get(0);
		}
		
		throw new TransformationException("Could not find Transformer for Input "+ input.getClass());
	}

	@Override
	public Set<Class<?>> getSupportedOutputs() {
		Set<Class<?>> result = new HashSet<>();
		result.add(EposEvent.class);
		return result;
	}

	@Override
	public boolean supportsInput(Object input, String contentType) {
		for (MessageTransformer<EposEvent> t : transformers) {
			if (t.supportsInput(input, null)) {
				return true;
			}
		}
		return false;
	}
	
	private static class PriorityComparator implements Comparator<MessageTransformer<EposEvent>> {

		@Override
		public int compare(MessageTransformer<EposEvent> o1,
				MessageTransformer<EposEvent> o2) {
			return (o1.getPriority() - o2.getPriority());
		}
		
	}

}
