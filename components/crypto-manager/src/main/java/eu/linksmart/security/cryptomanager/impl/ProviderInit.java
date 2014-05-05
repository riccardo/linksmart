package eu.linksmart.security.cryptomanager.impl;

import org.apache.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;

public class ProviderInit {

    private static boolean initialized = false;

    private final static Logger logger = Logger.getLogger(ProviderInit.class.getName());
    private static org.bouncycastle.jce.provider.BouncyCastleProvider mProvider;

    private ProviderInit() {
    }

    //TODO workaround for bouncyCastel initalization
    // because of Class.forName initalization, the traditional import mechanisms are overriden
    // initializing some classes from the derby package make sure , those are imported properly by
    // maven bundle plugin

    static boolean initProvider() {
    	try {
        	if (!initialized) {
                logger.debug("initializing & adding bouncy castle provider");
                mProvider = null;
                mProvider = new BouncyCastleProvider();
                Security.addProvider(mProvider);
                logger.debug("bouncy castel provider is added");
                initialized = true;
        	} else {
                logger.debug("Bouncy castle provider already initialized.");
            }
        } catch (Throwable t) {
            logger.error(t);
            return false;
        }
        return true;
    }

}
