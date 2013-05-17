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
package org.n52.epos.transform.xmlbeans;

import java.util.HashSet;
import java.util.ServiceLoader;
import java.util.Set;

import org.n52.epos.event.EposEvent;
import org.n52.epos.transform.EposTransformer;
import org.n52.epos.transform.MessageTransformer;
import org.n52.epos.transform.TransformationException;
import org.n52.epos.transform.TransformationRepository;

/**
 * This class holds all ({@link ServiceLoader}-registered)
 * implementations of {@link EposTransformer}.
 * 
 * @author matthes rieke
 */
public class TransformationToEposEventRepository implements TransformationRepository<EposEvent> {
	
	private Set<EposTransformer> transformers = new HashSet<EposTransformer>();
	
	public TransformationToEposEventRepository() {
		ServiceLoader<EposTransformer> loader = ServiceLoader.load(EposTransformer.class);
		
		for (EposTransformer eposTransformer : loader) {
			transformers.add(eposTransformer);
		}
	}
	
	@Override
	public EposEvent transform(Object input) throws TransformationException {
		MessageTransformer<EposEvent> trans = findTransformers(input);
		
		return trans.transform(input);
	}
	
	private MessageTransformer<EposEvent> findTransformers(Object input) throws TransformationException {
		for (MessageTransformer<EposEvent> t : transformers) {
			if (t.supportsInput(input)) {
				return t;
			}
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

}
