package eu.linksmart.security.communication.impl.sym.integration;

import java.util.List;

import eu.linksmart.security.communication.CommunicationSecurityManager;
import eu.linksmart.security.communication.SecurityProperty;

import eu.linksmart.it.utils.ITConfiguration;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertNotNull;
//import static junit.framework.Assert.assertTrue;
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.fail;

import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;

import javax.inject.Inject;

import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.*;


@RunWith(PaxExam.class)
public class CommunicationSecurityManagerIT {
	 
//    private static Logger LOG = Logger.getLogger(ComponentIT.class.getName());
 
    @Inject
    private CommunicationSecurityManager securityManager;
 
    @Configuration
    public Option[] config() {
    	return new Option[] {
        		ITConfiguration.regressionDefaults(),
        		features(ITConfiguration.getFeaturesRepositoryURL(),"security-manager-sym-it"),  
        };
    }
 
    @Test
    public void basicIntegrationChain(){

        // TEST #1
        // this test retrieves class name & list of security types
    	
        String fromService = "CanBroadcast: "+(new Boolean(securityManager.canBroadcast())).toString();
        System.out.println(fromService);
        Assert.assertEquals("CanBroadcast: false",fromService);
        List<SecurityProperty> securityTypes = securityManager.getProperties();
        System.out.println("number of security types : "+securityTypes.size());
        assertNotNull(securityTypes);
 
        assertNotNull(securityManager.getClass());
        String className = securityManager.getClass().getCanonicalName();
        System.out.println("ClassName: "+className);
   
    }
}