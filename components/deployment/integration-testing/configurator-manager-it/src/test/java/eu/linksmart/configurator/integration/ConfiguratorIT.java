package eu.linksmart.configurator.integration;

import static org.junit.Assert.*;

import junit.framework.Assert;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;

import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.features;

import org.apache.log4j.Logger;

import eu.linksmart.it.utils.ITConfiguration;
//

import eu.linksmart.configurator.ConfiguratorActivator;
//import eu.linksmart.configurator.impl.ConfiguratorImpl;
//import org.osgi.service.cm.ConfigurationAdmin;

import static org.junit.Assert.*;
import static org.ops4j.pax.exam.CoreOptions.*;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.*;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.karaf.options.LogLevelOption.LogLevel;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import java.io.File;
import java.lang.reflect.Field;
import java.nio.charset.Charset;


/* 
 * @RunWith(PaxExam.class) hooks Pax Exam into JUnit and does its magic of 
 * setting up a test container with an OSGi framework and bundles under test
 */
@RunWith(PaxExam.class)
public class ConfiguratorIT {

    /*
	 * @Inject triggers the injection of OSGi services into the test.
	 * the test method can access the BundleContext of the probe bundle or any service obtained from the OSGi service registry
	 */
    @Inject
    private ConfiguratorActivator activator;
    //private Configurator configuratorManager;

    @Configuration
    public Option[] config() {
        return new Option[] {
        		ITConfiguration.regressionDefaults(),
        		features(ITConfiguration.getFeaturesRepositoryURL(),"configurator-manager")
        };
    }

    
    @Test
    public void testService() throws Exception {   
    	
//    	String[] configurations = configuratorManager.getAvailableConfigurations();
//        System.out.println("number of available configurations: "+configurations.length);
//        assertNotNull(configurations);
        
        assertNotNull(activator.getClass());
        String className = activator.getClass().getCanonicalName();
        System.out.println("ClassName: "+className);

        try {
        	assertTrue(true);
        } catch(Exception e) {
        	fail(e.getMessage());
        }
    }
 
}