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
package org.n52.epos.transform.xmlbeans.om20;

import java.io.IOException;
import net.opengis.om.x20.OMObservationDocument;
import org.apache.xmlbeans.XmlException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.n52.epos.event.EposEvent;
import org.n52.epos.transform.TransformationException;

/**
 *
 */
public class OM20TransformerTest {

    @Test
    public void testTransformation() throws TransformationException, IOException, XmlException {
        OM20Transformer transformer = new OM20Transformer();
        
        EposEvent result = transformer.transform(readXml());
        
        Assert.assertThat((Double) result.getValue("doubleValue"), CoreMatchers.is(29.0));
        Assert.assertThat(result.getValue("procedure").toString(), CoreMatchers.is("ws2500"));
        Assert.assertThat(result.getValue("observedProperty").toString(), CoreMatchers.is("AirTemperature"));
    }

    private OMObservationDocument readXml() throws IOException, XmlException {
        return OMObservationDocument.Factory.parse(getClass().getResourceAsStream("om.xml"));
    }
    
}
