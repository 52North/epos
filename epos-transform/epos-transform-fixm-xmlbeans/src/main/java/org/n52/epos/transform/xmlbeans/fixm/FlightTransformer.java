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


import aero.fixm.base.x30.SignificantPointType;
import aero.fixm.flight.x30.AircraftPositionType;
import aero.fixm.flight.x30.EnRouteType;
import aero.fixm.flight.x30.FlightDocument;
import aero.fixm.flight.x30.FlightType;
import aero.fixm.foundation.x30.GeographicLocationType;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import java.util.List;


import org.apache.xmlbeans.XmlObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.IOException;
import java.io.InputStream;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.n52.epos.event.EposEvent;
import org.n52.epos.event.MapEposEvent;
import org.n52.epos.transform.TransformationException;


/**
 * Parser for notifications encoded in FIXM.
 *
 */
public class FlightTransformer extends AbstractFIXMTransformer {
    
    private static final Logger LOG = LoggerFactory.getLogger(FlightTransformer.class);
    private final GeometryFactory geometryFactory;

    public FlightTransformer() {
        this.geometryFactory = new GeometryFactory();
    }
    
    public EposEvent decode(XmlObject xo) throws IOException {
        if (xo instanceof FlightDocument) {
            FlightDocument fd = (FlightDocument) xo;
            Position curr = parseCurrentPosition(fd);
            String gufi = parseGufi(fd);
            String identification = parseIdentification(fd);
            long now = System.currentTimeMillis();
            
            MapEposEvent event = new MapEposEvent(now, now);
            event.setValue("gufi", gufi);
            event.setValue("identification", identification);
            Point geom = this.geometryFactory.createPoint(new Coordinate(curr.getLongitude(), curr.getLatitude()));
            event.setValue(MapEposEvent.GEOMETRY_KEY, geom);
            
            return event;
        }
        
        return null;
    }
    
    public EposEvent decode(InputStream inputStream) throws IOException {
        try {
            XmlObject xo = XmlObject.Factory.parse(inputStream);
            return decode(xo);
        } catch (XmlException ex) {
            throw new IOException(ex);
        }
    }
    
    private Position parseCurrentPosition(FlightDocument xo) {
        if (xo.getFlight().isSetEnRoute()) {
            EnRouteType enroute = xo.getFlight().getEnRoute();
            if (enroute.isSetPosition()) {
                AircraftPositionType pos = enroute.getPosition();
                if (pos.isSetPosition()) {
                    SignificantPointType pos2 = pos.getPosition();
                    return extractPosition(pos2);
                }
            }
        }
        return null;
    }
    
    private Position extractPosition(XmlObject pos2) throws NumberFormatException {
        XmlCursor cur = pos2.newCursor();
        if (cur.toFirstChild()) {
            XmlObject locObj = cur.getObject();
            if (locObj instanceof GeographicLocationType) {
                GeographicLocationType lpt = (GeographicLocationType) locObj;
                List posList = lpt.getPos();
                if (posList.size() == 2) {
                    return new Position(Double.parseDouble(posList.get(0).toString()),
                            Double.parseDouble(posList.get(1).toString()));
                }
            }
        }
        return null;
    }
    
    private String parseGufi(FlightDocument fd) {
        if (fd.getFlight().isSetGufi()) {
            return fd.getFlight().getGufi().getStringValue();
        }
        return null;
    }
    
    private String parseIdentification(FlightDocument fd) {
        if (fd.getFlight().isSetFlightIdentification()) {
            return fd.getFlight().getFlightIdentification().getAircraftIdentification();
        }
        return null;
    }
    
    
    @Override
    protected EposEvent transformXmlBeans(XmlObject xo) throws TransformationException {
        FlightDocument input;
        if (xo instanceof FlightType) {
            input = FlightDocument.Factory.newInstance();
            input.setFlight((FlightType) xo);
        }
        else {
            input = (FlightDocument) xo;
        }
        
        try {
            EposEvent event = decode(input);
            return event;
        } catch (IOException ex) {
            LOG.warn(ex.getMessage(), ex);
            throw new TransformationException(ex);
        }
    }
    
    @Override
    protected boolean supportsXmlBeansInput(XmlObject input) {
        return input instanceof FlightDocument || input instanceof FlightType;
    }
    
    @Override
    protected QName getSupportedQName() {
        return FlightDocument.type.getDocumentElementName();
    }
    
    
}
