package eu.linksmart.policy.pdp.impl;

import eu.linksmart.it.utils.ITConfiguration;
import eu.linksmart.policy.pdp.PolicyDecisionPoint;
import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.karaf.options.KarafDistributionOption;

import javax.inject.Inject;

import static org.junit.Assert.fail;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.*;

@RunWith(PaxExam.class)
public class  PdpApplicationIT  {

        @Inject
        private PolicyDecisionPoint pdp;

        @Configuration
        public Option[] config() {
            return new Option[] {
            		ITConfiguration.regressionDefaults(),
                    KarafDistributionOption.editConfigurationFilePut("etc/org.apache.karaf.shell.cfg", "sshPort", "8117"),
                    KarafDistributionOption.editConfigurationFilePut("etc/org.apache.karaf.management.cfg", "rmiRegistryPort", "1133"),
                    KarafDistributionOption.editConfigurationFilePut("etc/org.apache.karaf.management.cfg", "rmiServerPort", "44479"),
                    KarafDistributionOption.editConfigurationFilePut("etc/org.ops4j.pax.web.cfg", "org.osgi.service.http.port", "8099"),
            		features(ITConfiguration.getFeaturesRepositoryURL(),"policy-pdp-it"),  
            };
        }

    @Test
    public void testPolicyPDP() {
    	try {
        	System.out.println("starting policy-pdp test");
        	Assert.assertEquals("eu.linksmart.policy.pdp.impl.PdpApplication", pdp.getClass().getName());
			System.out.println("policy-pdp test successfully completed");
        } catch(Exception e) {
        	e.printStackTrace();
        	fail(e.getMessage());
        }
    }
}
