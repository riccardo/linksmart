package eu.linksmart.maven.examples.activator;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.apache.log4j.Logger;

public class HelloMavenBundle implements BundleActivator {

    private Logger LOG = Logger.getLogger(HelloMavenBundle.class);
    
    /**
     * Implements BundleActivator.start().
     * @param bundleContext - the framework context for the bundle.
     **/
    public void start(BundleContext bundleContext) {
        LOG.info("Starting eu.linksmart.maven.examples.activator.HelloMavenBundle");
    }

    /**
     * Implements BundleActivator.stop()
     * @param bundleContext - the framework context for the bundle.
     **/
    public void stop(BundleContext bundleContext) {
    	LOG.info("Stopping eu.linksmart.maven.examples.activator.HelloMavenBundle");
    }
}
