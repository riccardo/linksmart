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
import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.karaf.options.KarafDistributionOption;

import org.w3c.dom.Element;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.wso2.balana.attr.AttributeValue;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import static org.w3c.dom.Node.ATTRIBUTE_NODE;
import static org.w3c.dom.Node.CDATA_SECTION_NODE;
import static org.w3c.dom.Node.COMMENT_NODE;
import static org.w3c.dom.Node.DOCUMENT_TYPE_NODE;
import static org.w3c.dom.Node.ELEMENT_NODE;
import static org.w3c.dom.Node.ENTITY_NODE;
import static org.w3c.dom.Node.ENTITY_REFERENCE_NODE;
import static org.w3c.dom.Node.NOTATION_NODE;
import static org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE;
import static org.w3c.dom.Node.TEXT_NODE;

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
            		ITConfiguration.regressionDefaults(true),
            		features(ITConfiguration.getFeaturesRepositoryURL(),"policy-pdp-it"),
 //                   mavenBundle("eu.linksmart.component", "pdp-fragment", "2.2.1-SNAPSHOT"),
 //                   mavenBundle("mvn:eu.linksmart.features/supernode/2.2.1-SNAPSHOT"),
            };
        }


    @Test
    public void testPolicyPDP() {
    	try {
        	System.out.println("starting policy-pdp test");
        	Assert.assertEquals("eu.linksmart.policy.pdp.impl.PdpApplication", pdp.getClass().getName());

            MY_REGISTRATION = new Registration(VIRTUAL_ADDRESS, new Part[]{ATTR_1});
            OTHER_REGISTRATION = new Registration(VIRTUAL_ADDRESS2, new Part[]{ATTR_2});

            //create requests
//            Set<Attribute> attrs = new HashSet<>();
//            Attribute attr = new Attribute(
 //                   URI.create(LinkSmartXacmlConstants.RESOURCE_RESOURCE_ID.getUrn()),
//                    null,
//                    new DateTimeAttribute(),
//                    new StringAttribute(MY_REGISTRATION.getVirtualAddress().toString()),
//                    true,
//                    XACMLConstants.XACML_VERSION_3_0);
//            attrs.add(attr);
//            Attributes attrResource = new Attributes(URI.create(XACMLConstants.RESOURCE_CATEGORY), attrs);
//            Set<Attributes> attributes = new HashSet<>();
//            attributes.add(attrResource);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();


                AttributeValue av = new StringAttribute( "CalculatorForBeginners");
                Set<Attribute> attrs = new HashSet<Attribute>();
                attrs.add(new Attribute(
//                        URI.create(LinkSmartXacmlConstants.ACTION_ACTION_ID.getUrn()),
                        URI.create(LinkSmartXacmlConstants.RESOURCE_RESOURCE_ID.getUrn()),
//                        URI.create(LinkSmartXacmlConstants.SUBJECT_SUBJECT_ID.getUrn()),
                        null,
                        new DateTimeAttribute(),
                        av,
                        true,
                        XACMLConstants.XACML_VERSION_3_0));
//                Attributes actionAttrs = new Attributes(URI.create(XACMLConstants.ACTION_CATEGORY), attrs);
                Attributes actionAttrs = new Attributes(URI.create(XACMLConstants.RESOURCE_CATEGORY), attrs);
//                Attributes actionAttrs = new Attributes(URI.create(XACMLConstants.SUBJECT_CATEGORY), attrs);
                Set<Attributes> attributes = new HashSet<>();
                attributes.add(actionAttrs);

            RequestCtx req = new RequestCtx(attributes, null);
            req.encode(baos);
            String reqXml = baos.toString();
            System.out.println( "request: "+reqXml);

            String response = pdp.evaluate(reqXml);

            System.out.println("response: " + response);

            if(response != null) {
                try {
                    //parse string into xml
                    Document xmlDoc = DocumentBuilderFactory
                            .newInstance()
                            .newDocumentBuilder()
                            .parse(new ByteArrayInputStream(response.getBytes()));
                    Element node = xmlDoc.getDocumentElement();
//                    listNodes(node,"");
                    String tagName = node.getTagName();
                    Assert.assertEquals("No response returned!", tagName, "Response");

                    System.out.println("policy-pdp test successfully completed");

                } catch (Exception e) {
                    Assert.fail("Cannot parse the response received from PDP");
                }
            }

        } catch(Exception e) {
        	e.printStackTrace();
        	fail(e.getMessage());
        }
    }

    static String nodeType(short type) {
        switch (type) {
            case ELEMENT_NODE:
                return "Element";
            case DOCUMENT_TYPE_NODE:
                return "Document type";
            case ENTITY_NODE:
                return "Entity";
            case ENTITY_REFERENCE_NODE:
                return "Entity reference";
            case NOTATION_NODE:
                return "Notation";
            case TEXT_NODE:
                return "Text";
            case COMMENT_NODE:
                return "Comment";
            case CDATA_SECTION_NODE:
                return "CDATA Section";
            case ATTRIBUTE_NODE:
                return "Attribute";
            case PROCESSING_INSTRUCTION_NODE:
                return "Attribute";
        }
        return "Unidentified";
    }

    static void listNodes(Node node, String indent) {
        String nodeName = node.getNodeName();
        System.out.println(indent+" Node: "+nodeName);
        short type =node.getNodeType();
        System.out.println(indent+" Node Type: " + nodeType(type));
        if(type == TEXT_NODE){
            System.out.println(indent+" Content is: "+((Text)node).getWholeText());
        } else if(node.hasAttributes()) {
            System.out.println(indent+" Element Attributes are:");
            NamedNodeMap attrs = node.getAttributes();
            for(int i = 0 ; i<attrs.getLength() ; i++) {
                Attr attribute = (Attr)attrs.item(i);
                System.out.println(indent+ " " + attribute.getName()+" = "+attribute.getValue());
            }
        }

        NodeList list = node.getChildNodes();
        if(list.getLength() > 0) {
            System.out.println(indent+" Child Nodes of "+nodeName+" are:");
            for(int i = 0 ; i<list.getLength() ; i++) {
                listNodes(list.item(i),indent+"  ");
            }
        }
    }


}
