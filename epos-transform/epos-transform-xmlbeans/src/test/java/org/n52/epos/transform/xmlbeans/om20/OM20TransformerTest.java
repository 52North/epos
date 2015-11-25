/*
 * Copyright (C) 2015 52north.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
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
