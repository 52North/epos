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
