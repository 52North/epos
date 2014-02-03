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
package org.n52.epos.engine.filter.it;

import java.util.HashMap;
import java.util.Map;

import org.n52.epos.engine.filter.XPathConfiguration;
import org.n52.epos.engine.filter.XPathFilter;
import org.n52.epos.filter.EposFilter;
import org.n52.epos.filter.FilterInstantiationException;
import org.n52.epos.filter.FilterInstantiationRepository;

public class FilterFactory {

	
	public static XPathFilter createXPathFilter() throws FilterInstantiationException {
		Map<String, String> prefixes = new HashMap<String, String>();
		prefixes.put("om", "http://www.opengis.net/om/2.0");
		prefixes.put("xlink", "http://www.w3.org/1999/xlink");
		prefixes.put("gml", "http://www.opengis.net/gml/3.2");
		XPathConfiguration config = new XPathConfiguration("//om:observedProperty[@xlink:href='http://www.52north.org/test/observableProperty/1']",
				prefixes);
		EposFilter filter = FilterInstantiationRepository.Instance.instantiate(config);
		if (filter instanceof XPathFilter) return (XPathFilter) filter;
		return null;
	}
	
	
}
