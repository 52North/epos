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
package org.n52.epos.filter.pattern;

import java.util.List;

import org.n52.epos.filter.PassiveFilter;

/**
 * A {@link PatternFilter} implementation is a specialized
 * {@link PassiveFilter}, containing a set of patterns
 * which incoming events shall be evaluated against.
 * 
 * @author matthes rieke
 *
 */
public interface PatternFilter extends PassiveFilter {

	/**
	 * @return the list of {@link EventPattern}s.
	 */
	public List<EventPattern> getPatterns();
	
	/**
	 * @return create a serialized version of this pattern
	 */
	public CharSequence serialize();

	/**
	 * @return the input stream name that acts as the common
	 * external input for this pattern
	 */
	public String getInputStreamName();
	
}
