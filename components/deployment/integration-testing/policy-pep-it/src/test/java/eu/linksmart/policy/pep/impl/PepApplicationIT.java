package eu.linksmart.policy.pep.impl;

import eu.linksmart.it.utils.ITConfiguration;
import eu.linksmart.network.Registration;
import eu.linksmart.network.VirtualAddress;
import eu.linksmart.policy.LinkSmartXacmlConstants;
import eu.linksmart.policy.pep.PepService;
import eu.linksmart.policy.pep.PepResponse;

import eu.linksmart.utils.Part;
import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.karaf.options.KarafDistributionOption;
import org.wso2.balana.XACMLConstants;
import org.wso2.balana.attr.AttributeValue;
import org.wso2.balana.attr.DateTimeAttribute;
import org.wso2.balana.attr.StringAttribute;
import org.wso2.balana.ctx.Attribute;
import org.wso2.balana.ctx.xacml3.RequestCtx;
import org.wso2.balana.xacml3.Attributes;

import javax.inject.Inject;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.fail;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.*;

@RunWith(PaxExam.class)
public class  PepApplicationIT  {

    private static final Part ATTR_1 = new Part("One", "Eins");
    private static final Part ATTR_2 = new Part("Two", "Zwei");
    private static final VirtualAddress VIRTUAL_ADDRESS = new VirtualAddress("0.0.0.0");
    private static final VirtualAddress VIRTUAL_ADDRESS2 = new VirtualAddress("1.1.1.1");
    private static Registration MY_REGISTRATION;
    private static Registration OTHER_REGISTRATION;

    @Inject
        private PepService pep;

        @Configuration
        public Option[] config() {
            return new Option[] {
            		ITConfiguration.regressionDefaults(),
            		features(ITConfiguration.getFeaturesRepositoryURL(),"policy-pep-it"),
            };
        }

    @Test
    public void testCryptoManager() {
    	try {
        	System.out.println("starting policy-pep test");
        	Assert.assertEquals("eu.linksmart.policy.pep.impl.PepApplication", pep.getClass().getName());

            MY_REGISTRATION = new Registration(VIRTUAL_ADDRESS, new Part[]{ATTR_1});
            OTHER_REGISTRATION = new Registration(VIRTUAL_ADDRESS2, new Part[]{ATTR_2});

            String xmlReq = new String("test request");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();


            AttributeValue av = new StringAttribute( "read");
            Set<Attribute> attrs = new HashSet<Attribute>();
            attrs.add(new Attribute(
                    URI.create(LinkSmartXacmlConstants.ACTION_ACTION_ID.getUrn()),
                    null,
                    new DateTimeAttribute(),
                    av,
                    true,
                    XACMLConstants.XACML_VERSION_3_0));
            Attributes actionAttrs = new Attributes(URI.create(XACMLConstants.ACTION_CATEGORY), attrs);
            Set<Attributes> attributes = new HashSet<>();
            attributes.add(actionAttrs);

            RequestCtx req = new RequestCtx(attributes, null);
            req.encode(baos);
            String reqXml = baos.toString();
            System.out.println( "request: "+reqXml);

            PepResponse response = pep.requestAccessDecision(VIRTUAL_ADDRESS, VIRTUAL_ADDRESS2, "read", reqXml.getBytes());

            //public PepResponse requestAccessDecision(final VirtualAddress theSndVad, final VirtualAddress theRecVad, final String topic, final byte[] msg) {

            System.out.println("response: " + response.getStatus());

			System.out.println("policy-pep test successfully completed");
        } catch(Exception e) {
        	e.printStackTrace();
        	fail(e.getMessage());
        }
    }
}
