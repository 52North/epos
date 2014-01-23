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
import org.n52.epos.transform.EposTransformer;
import org.n52.epos.transform.MessageTransformer;
import org.n52.epos.transform.TransformationException;
import org.n52.epos.transform.TransformationRepository;
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
	public EposEvent transform(Object input) throws TransformationException {
		MessageTransformer<EposEvent> trans = findTransformers(input);
		
		logger.debug("Using transformer {}", trans.getClass().getName());
		
		EposEvent result = trans.transform(input);
		
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
			if (t.supportsInput(input)) {
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
		Set<Class<?>> result = new HashSet<Class<?>>();
		result.add(EposEvent.class);
		return result;
	}

	@Override
	public boolean supportsInput(Object input) {
		for (MessageTransformer<EposEvent> t : transformers) {
			if (t.supportsInput(input)) {
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
