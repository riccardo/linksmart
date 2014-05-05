package eu.linksmart.security.cryptomanager.tests;

import eu.linksmart.it.utils.ITConfiguration;
import eu.linksmart.security.cryptomanager.CryptoManager;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;

import javax.inject.Inject;

import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.*;

@RunWith(PaxExam.class)
public class  CryptoManagerIT  {

        @Inject
        private CryptoManager cryptoManager;

        @Configuration
        public Option[] config() {
            return new Option[] {
            		ITConfiguration.regressionDefaults(),
            		features(ITConfiguration.getFeaturesRepositoryURL(),"crypto-manager"),  
            };
        }

    @Test
    public void testCryptoManager() {
        String fromService = cryptoManager.getClass().getName();
        System.out.println("crypto class name : " + fromService);
        Assert.assertEquals("eu.linksmart.security.cryptomanager.impl.CryptoManagerImpl",fromService);
    }
}
