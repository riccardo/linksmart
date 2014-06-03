package eu.linksmart.maven.examples.it;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.charset.Charset;

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

import eu.linksmart.maven.examples.ds.service.IExampleService;

/*
* Based on the convention of the maven-failsafe-plugin you should name your integration tests
* like *IT.java.
*/

/* 
 * @RunWith(PaxExam.class) hooks Pax Exam into JUnit and does its magic of 
 * setting up a test container with an OSGi framework and bundles under test
 */
@RunWith(PaxExam.class)
/* @ExamReactorStrategy determines whether or not the OSGi framework gets restarted for every single test. 
 * The value below is the default (so this annotation could be dropped), indicating that 
 * the framework will be restarted for each test to provide a maximum of isolation
 */
@ExamReactorStrategy(PerMethod.class)
public class ExampleServiceTestIT {

	/*
	 * @Inject triggers the injection of OSGi services into the test.
	 * the test method can access the BundleContext of the probe bundle or any service obtained from the OSGi service registry
	 */
    @Inject
    private IExampleService exampleService;

    /*
     * @Configuration returns an array of configuration options. 
     * These options define the set of bundles to be provisioned to the OSGi framework in the test container 
     */
    @Configuration
    public Option[] config() {
    	switchPlatformEncodingToUTF8();
        return new Option[] {
                // Provision and launch a container based on a distribution of Karaf (Apache ServiceMix)
                karafDistributionConfiguration()
                        .frameworkUrl(
                        		maven()
                        		.groupId("org.apache.servicemix")
                        		.artifactId("apache-servicemix")
                        		.type("zip")
                        		.version("4.5.2"))
                        .karafVersion("3.2.0")
                        .name("Apache ServiceMix")
                        .unpackDirectory(new File("target/servicemix-karaf"))
                        .useDeployFolder(false),
                /*
                 * keeping container sticks around after the test so we can check the contents
                // of the data directory when things go wrong.        
                 */
                keepRuntimeFolder(),
                /*
                 * don't bother with local console output as it just ends up cluttering the logs
                 */
                configureConsole().ignoreLocalConsole(),
                /*
                 * force the log level to INFO so we have more details during the test. It defaults to WARN.
                 */
                logLevel(LogLevel.INFO),
                /*
                 * maven bundles will be provisioned to the test container from a local or remote Maven repository 
                 * using the standard Maven lookup and caching procedures
                 */  
                mavenBundle("org.apache.felix","org.apache.felix.scr","1.6.2"),
                mavenBundle("org.apache.felix","org.apache.felix.metatype","1.0.4"),
                mavenBundle("eu.linksmart","scr-declerative-service","2.2.0-SNAPSHOT")
                /*
                 * bundle() provisions an OSGi bundle from any kind of URL supported by Pax URL,
                 * this includes the standard file: and http: protocols.
                 */
                //bundle("http://www.example.com/repository/foo-1.2.3.jar"),
        };
    }
    
    @Test
    public void testExampleService() throws Exception {      
        try {
            String fromService = exampleService.sayHi("Weee!");
            System.out.println("response from service: " + fromService);
            assertEquals("Hello-Back-from-ExampleService [Weee!]", fromService);
        } catch(Exception e) {
        	e.printStackTrace();
        	fail(e.getMessage());
        }
    }
    
    //
    // hack for plateform encodind error (bug in Apache Karaf used version)
    // "Underlying stream encoding 'Cp1252' and input paramter for writeStartDocument() method 'UTF-8' do not match"
    //
    private static void switchPlatformEncodingToUTF8() {
        try {
          System.setProperty("file.encoding","UTF-8");
          Field charset = Charset.class.getDeclaredField("defaultCharset");
          charset.setAccessible(true);
          charset.set(null,null);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
}