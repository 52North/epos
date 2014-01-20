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
