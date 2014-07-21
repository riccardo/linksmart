package eu.linksmart.policy.pep.impl;

import eu.linksmart.it.utils.ITConfiguration;
import eu.linksmart.policy.pep.PepService;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;

import javax.inject.Inject;

import static org.junit.Assert.fail;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.*;

@RunWith(PaxExam.class)
public class  PepApplicationIT  {

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
			System.out.println("policy-pep test successfully completed");
        } catch(Exception e) {
        	e.printStackTrace();
        	fail(e.getMessage());
        }
    }
}
