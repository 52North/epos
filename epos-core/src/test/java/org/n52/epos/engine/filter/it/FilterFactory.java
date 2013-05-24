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
package org.n52.epos.engine.filter.it;

import java.util.HashMap;
import java.util.Map;

import javax.xml.xpath.XPathExpressionException;

import org.n52.epos.engine.filter.XPathFilter;

public class FilterFactory {

	
	public static XPathFilter createXPathFilter() throws XPathExpressionException {
		Map<String, String> prefixes = new HashMap<String, String>();
		prefixes.put("om", "http://www.opengis.net/om/2.0");
		prefixes.put("xlink", "http://www.w3.org/1999/xlink");
		prefixes.put("gml", "http://www.opengis.net/gml/3.2");
		return new XPathFilter("//om:observedProperty[@xlink:href='http://www.52north.org/test/observableProperty/1']",
				prefixes);
	}
	
	
}
