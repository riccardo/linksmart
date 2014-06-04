package eu.linksmart.network.identity.pip.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.LogFactory;

import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Element;
import org.wso2.balana.XACMLConstants;
import org.wso2.balana.attr.DateTimeAttribute;
import org.wso2.balana.attr.StringAttribute;
import org.wso2.balana.ctx.Attribute;
import org.wso2.balana.xacml3.Attributes;
import org.wso2.balana.ctx.xacml3.RequestCtx;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import eu.linksmart.network.VirtualAddress;
import eu.linksmart.network.Registration;
import eu.linksmart.network.Message;
import eu.linksmart.network.NMResponse;
import eu.linksmart.network.identity.impl.IdentityManagerImpl;
import eu.linksmart.network.identity.pip.impl.IdentityMgrPolicyInformationPoint;
import eu.linksmart.network.networkmanager.core.NetworkManagerCore;
import eu.linksmart.policy.LinkSmartXacmlConstants;
import eu.linksmart.utils.Part;

public class IdentityMgrPolicyInformationPointTest {

	private static final Part ATTR_1 = new Part("One", "Eins");
	private static final Part ATTR_2 = new Part("Two", "Zwei");
	private static final VirtualAddress VIRTUAL_ADDRESS = new VirtualAddress("0.0.0.0");
	private static Registration MY_VIRTUAL_ADDRESS;
	private static Registration OTHER_VIRTUAL_ADDRESS;

	private IdentityManagerImpl identityMgr;
	private IdentityMgrPolicyInformationPoint identityPip;

	@Before
	public void setUp() throws RemoteException {
		//create IdMgr and store entities 
		this.identityMgr = new IdentityManagerImpl();
		MY_VIRTUAL_ADDRESS = this.identityMgr.createServiceByAttributes(new Part[]{ATTR_1});
		OTHER_VIRTUAL_ADDRESS = this.identityMgr.createServiceByAttributes(new Part[]{ATTR_2});
		this.identityPip = new IdentityMgrPolicyInformationPoint(identityMgr);

	}

	/**
	 * Tests whether information about the resource is retrieved correctly.
	 */
	@Test
	public void findAttributeTestResource() {
		//create requests
		Set<Attribute> attrs = new HashSet<Attribute>();
		Attribute attr = new Attribute(
				URI.create(LinkSmartXacmlConstants.RESOURCE_RESOURCE_ID.getUrn()), 
				null,
				new DateTimeAttribute(),
				new StringAttribute(MY_VIRTUAL_ADDRESS.getVirtualAddress().toString()),
				true,
				XACMLConstants.XACML_VERSION_3_0);
		attrs.add(attr);
		Attributes attrResource = new Attributes(URI.create(XACMLConstants.RESOURCE_CATEGORY), attrs);
		Set<Attributes> attributes = new HashSet<Attributes>();
		attributes.add(attrResource);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		RequestCtx req = new RequestCtx(attributes, null);
		req.encode(baos);
		String reqXml = baos.toString();

		String response = identityPip.findAttribute(
				URI.create("http://www.w3.org/2001/XMLSchema#string"),
				URI.create("linksmart:policy:resource:One"),
				null,
				URI.create(XACMLConstants.RESOURCE_CATEGORY),
				reqXml);

		if(response != null) {
			try {
				//parse string into xml
				Element node =  DocumentBuilderFactory
						.newInstance()
						.newDocumentBuilder()
						.parse(new ByteArrayInputStream(response.getBytes()))
						.getDocumentElement();
				//convert xml into attribute
				Attribute attrResp = Attribute.getInstance(node);
				assertEquals("Retrieved attribute is not the same as expected", ATTR_1.getValue(), ((StringAttribute)attrResp.getValue()).getValue());
			} catch (Exception e) {
				fail("Cannot parse attribute received from PIP");
			} 
		}
	}

	/**
	 * Tests whether information about the subject is retrieved correctly.
	 */
	@Test
	public void findAttributeTestSubject() {
		//create requests
		Set<Attribute> attrs = new HashSet<Attribute>();
		Attribute attr = new Attribute(
				URI.create(LinkSmartXacmlConstants.SUBJECT_SUBJECT_ID.getUrn()), 
				null,
				new DateTimeAttribute(),
				new StringAttribute(OTHER_VIRTUAL_ADDRESS.getVirtualAddress().toString()),
				true,
				XACMLConstants.XACML_VERSION_3_0);
		attrs.add(attr);
		Attributes attrSubject = new Attributes(URI.create(XACMLConstants.SUBJECT_CATEGORY), attrs);
		Set<Attributes> attributes = new HashSet<Attributes>();
		attributes.add(attrSubject);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();		
		RequestCtx req = new RequestCtx(attributes, null);
		req.encode(baos);
		String reqXml = baos.toString();

		String response = identityPip.findAttribute(
				URI.create("http://www.w3.org/2001/XMLSchema#string"),
				URI.create("linksmart:policy:subject:Two"),
				null,
				URI.create(XACMLConstants.SUBJECT_CATEGORY),
				reqXml);

		if(response != null) {
			try {
				//parse string into xml
				Element node =  DocumentBuilderFactory
						.newInstance()
						.newDocumentBuilder()
						.parse(new ByteArrayInputStream(response.getBytes()))
						.getDocumentElement();
				//convert xml into attribute
				Attribute attrResponse = Attribute.getInstance(node);
				assertEquals("Retrieved attribute is not the same as expected", ATTR_2.getValue(), ((StringAttribute)attrResponse.getValue()).getValue());
			} catch (Exception e) {
				fail("Cannot parse attribute received from PIP");
			} 
		}
	}
	
	/**
	 * Tests whether null is returned when no information is available.
	 */
	@Test
	public void findAttributeTestSubjectNone() {
		//create requests
		Set<Attribute> attrs = new HashSet<Attribute>();
		Attribute attr = new Attribute(
				URI.create(LinkSmartXacmlConstants.SUBJECT_SUBJECT_ID.getUrn()), 
				null,
				new DateTimeAttribute(),
				new StringAttribute(OTHER_VIRTUAL_ADDRESS.getVirtualAddress().toString()),
				true,
				XACMLConstants.XACML_VERSION_3_0);
		attrs.add(attr);
		Attributes attrSubject = new Attributes(URI.create(XACMLConstants.SUBJECT_CATEGORY), attrs);
		Set<Attributes> attributes = new HashSet<Attributes>();
		attributes.add(attrSubject);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();		
		RequestCtx req = new RequestCtx(attributes, null);
		req.encode(baos);
		String reqXml = baos.toString();

		try {
		String response = identityPip.findAttribute(
				URI.create("http://www.w3.org/2001/XMLSchema#string"),
				URI.create("linksmart:policy:subject:Three"),
				null,
				URI.create(XACMLConstants.SUBJECT_CATEGORY),
				reqXml);

		assertNull("Retrieval did not return null although expected!", response);
		} catch (Exception e) {
			fail("Exception while searching for undefined attribute!");
		}
	}
}
