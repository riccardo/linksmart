package eu.linksmart.policy.pdp.impl;

import eu.linksmart.it.utils.ITConfiguration;
import eu.linksmart.network.Registration;
import eu.linksmart.network.VirtualAddress;
import eu.linksmart.policy.LinkSmartXacmlConstants;
import eu.linksmart.policy.pdp.PolicyDecisionPoint;
import eu.linksmart.utils.Part;
import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.karaf.options.KarafDistributionOption;
import org.w3c.dom.Element;
import org.wso2.balana.XACMLConstants;
import org.wso2.balana.attr.DateTimeAttribute;
import org.wso2.balana.attr.StringAttribute;
import org.wso2.balana.ctx.Attribute;
import org.wso2.balana.ctx.xacml3.RequestCtx;
import org.wso2.balana.xacml3.Attributes;

import javax.inject.Inject;
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.fail;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.*;

@RunWith(PaxExam.class)
public class  PdpApplicationIT  {

    private static final Part ATTR_1 = new Part("One", "Eins");
    private static final Part ATTR_2 = new Part("Two", "Zwei");
    private static final VirtualAddress VIRTUAL_ADDRESS = new VirtualAddress("0.0.0.0");
    private static final VirtualAddress VIRTUAL_ADDRESS2 = new VirtualAddress("1.1.1.1");
    private static Registration MY_REGISTRATION;
    private static Registration OTHER_REGISTRATION;

    @Inject
        private PolicyDecisionPoint pdp;

        @Configuration
        public Option[] config() {
            return new Option[] {
            		ITConfiguration.regressionDefaults(),
            		features(ITConfiguration.getFeaturesRepositoryURL(),"policy-pdp-it"),
            };
        }

    @Test
    public void testPolicyPDP() {
    	try {
        	System.out.println("starting policy-pdp test");
        	Assert.assertEquals("eu.linksmart.policy.pdp.impl.PdpApplication", pdp.getClass().getName());

            MY_REGISTRATION = new Registration(VIRTUAL_ADDRESS, new Part[]{ATTR_1});
            OTHER_REGISTRATION = new Registration(VIRTUAL_ADDRESS2, new Part[]{ATTR_2});

            /**
             * Tests whether information about the resource is retrieved correctly.
             */

            //create requests
            Set<Attribute> attrs = new HashSet<>();
            Attribute attr = new Attribute(
                    URI.create(LinkSmartXacmlConstants.RESOURCE_RESOURCE_ID.getUrn()),
                    null,
                    new DateTimeAttribute(),
                    new StringAttribute(MY_REGISTRATION.getVirtualAddress().toString()),
                    true,
                    XACMLConstants.XACML_VERSION_3_0);
            attrs.add(attr);
            Attributes attrResource = new Attributes(URI.create(XACMLConstants.RESOURCE_CATEGORY), attrs);
            Set<Attributes> attributes = new HashSet<>();
            attributes.add(attrResource);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            RequestCtx req = new RequestCtx(attributes, null);
            req.encode(baos);
            String reqXml = baos.toString();
            //System.out.println( "request: "+reqXml);

            String response = pdp.evaluate(reqXml);
            //System.out.println("response: " + response);

            if(response != null) {
                try {
                    //parse string into xml
                    Element node =  DocumentBuilderFactory
                            .newInstance()
                            .newDocumentBuilder()
                            .parse(new ByteArrayInputStream(response.getBytes()))
                            .getDocumentElement();
                    String tagName = node.getTagName();
                    Assert.assertEquals("No response returned!", tagName, "Response");
                    //convert xml into attribute
                    //Attribute attrResponse = Attribute.getInstance(node, XACMLConstants.XACML_VERSION_3_0);
                    //Assert.assertEquals("Retrieved attribute is not the same as expected", ATTR_1.getValue(), ((StringAttribute)attrResponse.getValue()).getValue());
                } catch (Exception e) {
                    Assert.fail("Cannot parse attribute received from PIP");
                }
            }

            /**
             * Tests whether information about the subject is retrieved correctly.
             */

            //create requests
            attrs = new HashSet<Attribute>();
            attr = new Attribute(
                    URI.create(LinkSmartXacmlConstants.SUBJECT_SUBJECT_ID.getUrn()),
                    null,
                    new DateTimeAttribute(),
                    new StringAttribute(OTHER_REGISTRATION.getVirtualAddress().toString()),
                    true,
                    XACMLConstants.XACML_VERSION_3_0);
            attrs.add(attr);
            Attributes attrSubject = new Attributes(URI.create(XACMLConstants.SUBJECT_CATEGORY), attrs);
            attributes = new HashSet<Attributes>();
            attributes.add(attrSubject);

            baos = new ByteArrayOutputStream();
            req = new RequestCtx(attributes, null);
            req.encode(baos);
            reqXml = baos.toString();

            //System.out.println( "request: "+reqXml);

            response = pdp.evaluate(reqXml);
            //System.out.println("response: " + response);

            System.out.println("policy-pdp test successfully completed");
        } catch(Exception e) {
        	e.printStackTrace();
        	fail(e.getMessage());
        }
    }
}
