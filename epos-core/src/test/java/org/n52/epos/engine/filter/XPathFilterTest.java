package org.n52.epos.engine.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.xpath.XPathExpressionException;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.n52.epos.event.EposEvent;
import org.n52.epos.filter.ActiveFilter;
import org.mockito.Mockito;

public class XPathFilterTest {

	@Mock
	private EposEvent eventObject;
	
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void shouldPassXPathFilter() throws XmlException, IOException, XPathExpressionException {
		Mockito.when(eventObject.getOriginalObject()).thenReturn(readXmlObject());
		
		Map<String,String> map = new HashMap<String, String>();
		map.put("fes20", "http://www.opengis.net/fes/2.0");
		ActiveFilter filter = new XPathFilter("//fes20:Literal", map);
		
		Assert.assertTrue("Filter did not match!", filter.matches(eventObject));
	}
	
	@Test
	public void shouldNotPassXPathFilter() throws XmlException, IOException, XPathExpressionException {
		Mockito.when(eventObject.getOriginalObject()).thenReturn(readXmlObject());
		
		Map<String,String> map = new HashMap<String, String>();
		map.put("fes20", "http://www.opengis.net/fes/2.0");
		ActiveFilter filter = new XPathFilter("//fes20:Literals", map);
		
		Assert.assertTrue("Filter did match but should not have!", !filter.matches(eventObject));
	}

	private XmlObject readXmlObject() throws XmlException, IOException {
		return XmlObject.Factory.parse(getClass().getResource("xpathTestDocument.xml"));
	}
	
	
}
