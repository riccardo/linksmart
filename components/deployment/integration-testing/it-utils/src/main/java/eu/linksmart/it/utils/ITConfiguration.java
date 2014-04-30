package eu.linksmart.it.utils;

import static org.ops4j.pax.exam.CoreOptions.composite;
import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.ops4j.pax.exam.CoreOptions.when;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.configureConsole;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.karafDistributionConfiguration;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.keepRuntimeFolder;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.logLevel;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.charset.Charset;

import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.karaf.options.KarafDistributionOption;
import org.ops4j.pax.exam.karaf.options.LogLevelOption.LogLevel;
import org.ops4j.pax.exam.options.MavenArtifactUrlReference;

public class ITConfiguration {
	
	private static String OSGI_CONTAINER_KARAF = "karaf"; 
	private static String OSGI_CONTAINER_SERVICEMIX = "servicemix"; 
	
	private static String KARAF_DISTRO_GROUP_ID = "org.apache.karaf"; 
	private static String KARAF_DISTRO_ARTIFACT_ID = "apache-karaf";  
	private static String KARAF_DISTRO_BINARY_TYPE = "zip"; 
	private static String KARAF_DISTRO_VERSION = "3.0.0"; 
	private static String KARAF_DISTRO_NAME = "Apache Karaf"; 
	
	private static boolean DEBUG = false;
	private static int DEBUG_PORT = 8889;
	
	private static int HTTP_PORT = 9090;
	
	private static final String FEATURES_REPOSITORY_URL = "mvn:eu.linksmart/linksmart-features/2.2.0-SNAPSHOT/xml/features";
	
	public static Option regressionDefaults() {
        return regressionDefaults(OSGI_CONTAINER_KARAF);
    }
	
	public static Option regressionDefaults(String osgiContainerType) {
        return regressionDefaults(osgiContainerType, DEBUG, DEBUG_PORT, null);
    }
	
	public static Option regressionDefaults(boolean debugFlag) {
        return regressionDefaults(OSGI_CONTAINER_KARAF, debugFlag, DEBUG_PORT, null);
    }
	
	public static Option regressionDefaults(int debugPort) {
        return regressionDefaults(OSGI_CONTAINER_KARAF, true, debugPort, null);
    }

    public static Option regressionDefaults(String osgiContainerType, boolean debugFlag, int debugPort, String unpackDir) {
    	
    	switchPlatformEncodingToUTF8();
    	setOSGiContainer(osgiContainerType);
    	DEBUG = debugFlag;
    	DEBUG_PORT = debugPort;
    	
        return composite(

        		/*
        		 * if using apache service-mix distro, then no need to provision Jetty (for httpService), because Jetty Server is started up during bootstrapping of service-mix 
        		 */
        		// Provision and launch a container based on a distribution of Karaf (Apache ServiceMix/Plain Karaf)
        		karafDistributionConfiguration()
        			.frameworkUrl(mvnKarafDist())
        			.karafVersion(KARAF_DISTRO_VERSION)
        			.name(KARAF_DISTRO_NAME)
        			.unpackDirectory(unpackDir == null ? null : new File(unpackDir))
        			.useDeployFolder(false),
            
                /*
                 * keeping container sticks around after the test so we can check the contents
                   of the data directory when things go wrong.        
                 */
                //keepRuntimeFolder(),
                /*
                 * don't bother with local console output as it just ends up cluttering the logs
                 */
                configureConsole().ignoreLocalConsole(),
                /*
                 * force the log level to INFO so we have more details during the test. It defaults to WARN.
                 */
                logLevel(LogLevel.INFO),
                
                // set the system property for pax web
                KarafDistributionOption.editConfigurationFilePut("etc/org.ops4j.pax.web.cfg", "org.osgi.service.http.port", Integer.toString(HTTP_PORT)),
                
                /*
                 * activates debugging on the Karaf container using the provided port and holds
                 * the vm till you've attached the debugger.
                 */
                when(isDebug()).useOptions(KarafDistributionOption.debugConfiguration(Integer.toString(DEBUG_PORT), true)));
        
    }

    private static MavenArtifactUrlReference mvnKarafDist() {
        return maven().groupId(KARAF_DISTRO_GROUP_ID).artifactId(KARAF_DISTRO_ARTIFACT_ID).type(KARAF_DISTRO_BINARY_TYPE).version(KARAF_DISTRO_VERSION);
    }
    
    public static void enableDebug(boolean flag) {
    	DEBUG = flag;
    }
    
    public static boolean isDebug() {
        return DEBUG;
    }
    
    public static void setContainerHttpPort(int port) {
    	HTTP_PORT = port;
    }
    
    public static String getFeaturesRepositoryURL() {
    	return FEATURES_REPOSITORY_URL;
    }
    
    private static void setOSGiContainer(String osgiContainerType) {
    	if(osgiContainerType.equals(OSGI_CONTAINER_KARAF)) {
    		KARAF_DISTRO_GROUP_ID = "org.apache.karaf"; 
    		KARAF_DISTRO_ARTIFACT_ID = "apache-karaf";  
    		KARAF_DISTRO_BINARY_TYPE = "zip"; 
    		KARAF_DISTRO_VERSION = "3.0.0"; 
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
