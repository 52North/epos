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

package org.n52.epos.transform.xmlbeans.fixm;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import java.io.IOException;
import java.io.InputStream;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.n52.epos.event.EposEvent;
import org.n52.epos.event.MapEposEvent;

/**
 *
 * @author <a href="mailto:m.rieke@52north.org">Matthes Rieke</a>
 */
public class FlightTransformerTest {

    @Test
    public void testDecoding() throws IOException {
        FlightTransformer decoder = new FlightTransformer();

        EposEvent flight = decoder.decode(readFlight());

        Assert.assertThat(flight.getValue("gufi"), CoreMatchers.is("8c7995c5-1a65-430c-96d8-a8347b9ed2a3"));
        Assert.assertThat(flight.getValue("identification"), CoreMatchers.is("MNG200D"));
        Point geom = (Point) (Geometry) flight.getValue(MapEposEvent.GEOMETRY_KEY);
        Assert.assertThat(geom.getY(), CoreMatchers.is(35.15));
        Assert.assertThat(geom.getX(), CoreMatchers.is(-119.38));
    }

    private InputStream readFlight() {
        return getClass().getResourceAsStream("test-flight.xml");
    }

}
