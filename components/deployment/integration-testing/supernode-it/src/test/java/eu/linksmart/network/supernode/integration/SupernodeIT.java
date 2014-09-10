package eu.linksmart.network.supernode.integration;

/**
 * Created by carlos on 10.09.14.
 */
import static org.junit.Assert.*;

import java.io.File;
import java.lang.reflect.Field;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.List;

import eu.linksmart.it.utils.ITConfiguration;
import junit.framework.Assert;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;

import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.*;

import org.apache.log4j.Logger;

import eu.linksmart.it.utils.ITConfiguration;
import org.ops4j.pax.exam.karaf.options.KarafDistributionOption;
import org.ops4j.pax.exam.karaf.options.LogLevelOption;
import org.ops4j.pax.exam.options.MavenArtifactUrlReference;

/*
 * @RunWith(PaxExam.class) hooks Pax Exam into JUnit and does its magic of
 * setting up a test container with an OSGi framework and bundles under test
 */
@RunWith(PaxExam.class)
public class SupernodeIT {

    private static String OSGI_CONTAINER_KARAF = "karaf";
    private static String OSGI_CONTAINER_SERVICEMIX = "servicemix";

    private static String KARAF_DISTRO_GROUP_ID = "org.apache.karaf";
    private static String KARAF_DISTRO_ARTIFACT_ID = "apache-karaf";
    private static String KARAF_DISTRO_BINARY_TYPE = "zip";
    // the version will be filtered by maven plugin
    private static String KARAF_DISTRO_VERSION = "3.0.1";
    private static String KARAF_DISTRO_NAME = "Apache Karaf";
    // the version will be filtered by maven plugin
    private static String LINKSMART_VERSION = "${project.version}";

//    private static Logger LOG = Logger.getLogger(ComponentIT.class.getName());

    /*
	 * @Inject triggers the injection of OSGi services into the test.
	 * the test method can access the BundleContext of the probe bundle or any service obtained from the OSGi service registry
	 */

    //@Inject
    //private Backbone backboneData;

    /*
     * @Configuration returns an array of configuration options.
     * These options define the set of bundles to be provisioned to the OSGi framework in the test container
     */
    @Configuration
    public Option[] config() {
        return new Option[] {
                karafDistributionConfiguration()
                        .frameworkUrl(mvnKarafDist())
                        .karafVersion(KARAF_DISTRO_VERSION)
                        .name(KARAF_DISTRO_NAME)
                        .unpackDirectory(new File("target/paxexam/unpack/"))
                        .useDeployFolder(false),
                //features(maven().groupId("eu.linksmart.features").artifactId("linksmart-supernode-features").version("2.2.0-SNAPSHOT").type("xml").classifier("features").versionAsInProject(), "linksmart-supernode"),
                // Force the log level to INFO so we have more details during the test.  It defaults to WARN.
                logLevel(LogLevelOption.LogLevel.INFO),
                features(
                        "mvn:eu.linksmart.features/linksmart-supernode-features/2.2.0-SNAPSHOT/xml/features",
                        "linksmart-supernode"),
        };
    }


    @Test
    public void testService() throws Exception {

        // test if both supernode ports, namely extAddrHttp and extAddrTcp are open
        // both ports are configured in /config/NM.properties
        Thread.sleep(500);
        Socket socket = new Socket("localhost",9100);
        String message = 9100 + " is open on " + "localhost";
        System.out.println(message);
        socket.close();
        socket = new Socket("localhost",9101);
        message = 9101 + " is open on " + "localhost";
        System.out.println(message);
        socket.close();


    }
    private static MavenArtifactUrlReference mvnKarafDist() {
        return maven().groupId(KARAF_DISTRO_GROUP_ID).artifactId(KARAF_DISTRO_ARTIFACT_ID).type(KARAF_DISTRO_BINARY_TYPE).version(KARAF_DISTRO_VERSION);
    }


    private static void setOSGiContainer(String osgiContainerType) {
        if(osgiContainerType.equals(OSGI_CONTAINER_KARAF)) {
            KARAF_DISTRO_GROUP_ID = "org.apache.karaf";
            KARAF_DISTRO_ARTIFACT_ID = "apache-karaf";
            KARAF_DISTRO_BINARY_TYPE = "zip";
            KARAF_DISTRO_VERSION = "3.0.1";
            KARAF_DISTRO_NAME = "Apache Karaf";
        }
        if(osgiContainerType.equals(OSGI_CONTAINER_SERVICEMIX)) {
            KARAF_DISTRO_GROUP_ID = "org.apache.servicemix";
            KARAF_DISTRO_ARTIFACT_ID = "apache-servicemix";
            KARAF_DISTRO_BINARY_TYPE = "zip";
            KARAF_DISTRO_VERSION = "5.0.0";
            KARAF_DISTRO_NAME = "Apache ServiceMix";
        }
    }

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
