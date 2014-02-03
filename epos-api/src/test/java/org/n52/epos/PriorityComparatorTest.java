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
package org.n52.epos;

import org.junit.Assert;
import org.junit.Test;
import org.n52.epos.event.EposEvent;
import org.n52.epos.transform.EposTransformer;
import org.n52.epos.transform.MessageTransformer;
import org.n52.epos.transform.TransformationException;
import org.n52.epos.transform.TransformationRepository;

public class PriorityComparatorTest {
	
	MessageTransformer<EposEvent> trans1 = new LocalTransformer1();
	MessageTransformer<EposEvent> trans2 = new LocalTransformer2();
	
	Object input = new RuntimeException();
	static Class<?> selectedTransformer;
	
	
	@Test
	public void testComparison() throws TransformationException {
		TransformationRepository.Instance.transform(input, EposEvent.class);
		
		Assert.assertTrue("Not the expected transformer: "+ selectedTransformer,
				selectedTransformer.equals(LocalTransformer2.class));
	}
	
	public static class LocalTransformer1 implements EposTransformer {

		@Override
		public EposEvent transform(Object input) throws TransformationException {
			PriorityComparatorTest.selectedTransformer = getClass();
			return null;
		}

		@Override
		public boolean supportsInput(Object input) {
			return input instanceof RuntimeException;
		}

		@Override
		public short getPriority() {
			return 1;
		}
		
	}
	
	public static class LocalTransformer2 extends LocalTransformer1 {
		
		@Override
		public short getPriority() {
			return 0;
		}
		
	}

}
