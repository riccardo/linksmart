package eu.linksmart.network.identity.impl;

import static org.junit.Assert.*;

import javax.inject.Inject;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;

import eu.linksmart.it.utils.ITConfiguration;
import eu.linksmart.network.identity.IdentityManager;
import org.ops4j.pax.exam.karaf.options.KarafDistributionOption;

import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.*;

@RunWith(PaxExam.class)
public class  IdentityManagerCryptoIT  {

        @Inject
        private IdentityManager identityManager;

        @Configuration
        public Option[] config() {
            return new Option[] {
            		ITConfiguration.regressionDefaults(),
            		features(ITConfiguration.getFeaturesRepositoryURL(),"identity-manager-crypto-it"),
            };
        }

    @Test
    public void testIdentityManagerCrypto() {
    	try {
        	System.out.println("starting identity manager crypto test");
            Assert.assertEquals("eu.linksmart.network.identity.impl.crypto.IdentityManagerCertImpl",identityManager.getClass().getName());
			System.out.println("identity manager crypto test successfully completed");
        } catch(Exception e) {
        	e.printStackTrace();
        	fail(e.getMessage());
        }
    }
}
