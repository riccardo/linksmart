package eu.linksmart.security.cryptomanager.impl;

import org.apache.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;

/**
 * Created by carlos on 05.05.14.
 */
public class ProviderInit {

    private static boolean intialized = false;

    private final static Logger logger = Logger.getLogger(ProviderInit.class.getName());
    private static  org.bouncycastle.jce.provider.BouncyCastleProvider mProvider;

    private ProviderInit(){

    }

    //TODO workaround for bouncyCastel initalization
    // because of Class.forName initalization, the traditional import mechanisms are overriden
    // initializing some classes from the derby package make sure , those are imported properly by
    // maven bundle plugin

    static boolean initProvider() {
        if (intialized == false) {
            logger.debug("initializing bouncy castle provider...");
            mProvider = null;
            try {
                mProvider = new BouncyCastleProvider();
                Security.addProvider(mProvider);
                logger.debug("done.");
            } catch (Throwable t) {
                logger.error(t);
                return false;
            }
        }else{
            logger.debug("Bouncy castle provider already initialized.");
        }
        return true;
    }

}
