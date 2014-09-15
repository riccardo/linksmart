package eu.linksmart.${artifactId};

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.Service;

import org.apache.log4j.Logger;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;


@Component(name="${artifactId}", immediate=true)
@Service
public class ${artifactId}Impl implements I${artifactId}{

    private Logger mLogger = Logger.getLogger(${artifactId}Impl.class.getName());
    protected ComponentContext mContext;

    @Activate
    protected void activate(ComponentContext context) {
        mLogger.info("${artifactId}Impl activated");
    }

    @Deactivate
    protected void deactivate(ComponentContext context) {
        mLogger.info("${artifactId}Impl deactivated");
    }
}
