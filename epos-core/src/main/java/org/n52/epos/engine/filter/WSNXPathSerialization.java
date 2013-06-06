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
package org.n52.epos.engine.filter;

import java.util.Map;

import org.n52.epos.filter.EposFilter;
import org.n52.epos.filter.FilterSerialization;

public class WSNXPathSerialization implements FilterSerialization {

	@Override
	public CharSequence serializeFilter(EposFilter filter) {
		if (filter instanceof XPathFilter) {
			StringBuilder sb = new StringBuilder();
			sb.append("<wsnt:MessageContent Dialect=\"http://www.w3.org/TR/1999/REC-xpath-19991116\"");
			sb.append("	xmlns:wsnt=\"http://docs.oasis-open.org/wsn/b-2\"");
			Map<String, String> namespaces = ((XPathFilter) filter).getNamespaces();
			for (String key : namespaces.keySet()) {
				if (key.trim().equals("wsnt")) continue;
				sb.append(" xmlns:");
				sb.append(key);
				sb.append("=\"");
				sb.append(namespaces.get(key));
				sb.append("\"");
			}
			sb.append(">");
			sb.append(((XPathFilter) filter).getRawExpression());
			sb.append("</wsnt:MessageContent>");
			
			return sb;
		}
		return null;
	}

}
