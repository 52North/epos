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

import java.util.Date;

import org.n52.epos.event.EposEvent;
import org.n52.epos.event.MapEposEvent;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class GenericXMLTransformer implements EposTransformer {

	@Override
	public EposEvent transform(Object input, String contentType) throws TransformationException {
		Date now = new Date();
		MapEposEvent result = new MapEposEvent(now.getTime(), now.getTime());
		result.setOriginalObject(input);
		return result;
	}

	@Override
	public boolean supportsInput(Object input, String contentType) {
		if (input instanceof Element || input instanceof Node ||
				input instanceof Document) {
			return true;
		}
		return false;
	}

	@Override
	public short getPriority() {
		return 19;
	}


}
