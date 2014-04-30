package eu.linksmart.security.cryptomanager.tests;

import java.io.File;
import java.util.List;

import eu.linksmart.security.cryptomanager.CryptoManager;
import junit.framework.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.karaf.options.LogLevelOption;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;

import javax.inject.Inject;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.*;

/**
 * Created with IntelliJ IDEA.
 * User: carlos
 * Date: 03.04.14
 * Time: 14:02
 * To change this template use File | Settings | File Templates.
 */
@RunWith(PaxExam.class)

@ExamReactorStrategy(PerMethod.class)
public class  CryptoManagerIT  {

        @Inject
        private CryptoManager cryptoManager;

        @Configuration
        public Option[] config() {
            return new Option[] {
                    // Provision and launch a container based on a distribution of Karaf (Apache ServiceMix)
                    karafDistributionConfiguration()
                            .frameworkUrl(
                                    maven()
                                            .groupId("org.apache.servicemix")
                                            .artifactId("apache-servicemix")
                                            .type("zip")
                                            .version("5.0.0"))
                            .karafVersion("3.3.0")
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
                    logLevel(LogLevelOption.LogLevel.INFO),
                    /*
                    * karaf feature will be provisioned to the test container from a local or remote Maven repository
                    * using the standard Maven lookup and caching procedures
                    */
                    features("mvn:eu.linksmart/linksmart-features/2.2.0-SNAPSHOT/xml/features","cryptomanager-it")
                    //features("mvn:eu.linksmart/soap-integration-feature/1.0.0/xml/features","soap-integration-feature")

            };
        }

    @Test
    public void basicIntegrationChain(){


        // TESTING class retrieval  & listing of security types

        String fromService = cryptoManager.getClass().getName();
        System.out.println("class name : "+fromService);

        Assert.assertEquals("eu.linksmart.security.cryptomanager.impl.CryptoManagerImpl",fromService);

    }
}
