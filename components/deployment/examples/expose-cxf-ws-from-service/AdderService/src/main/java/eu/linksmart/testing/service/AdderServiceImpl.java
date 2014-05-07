package eu.linksmart.testing.service;

import org.apache.felix.scr.annotations.*;
import org.osgi.service.component.ComponentContext;

/**
 * Created with IntelliJ IDEA.
 * User: carlos
 * Date: 24.04.14
 * Time: 16:11
 * To change this template use File | Settings | File Templates.
 */
@Component(name="AdderService", immediate=true)
@Service({AdderService.class})
@Properties({
        @Property(name="service.exported.interfaces", value="*"),
        @Property(name="service.exported.configs", value="org.apache.cxf.ws"),
        @Property(name="org.apache.cxf.ws.address", value="http://localhost:9191/cxf/services/AdderService"),
        @Property(name="org.apache.cxf.ws.wsdl.location ", value="http://localhost:9191/cxf/services/AdderService?wsdl"),
        @Property(name="org.apache.cxf.ws.databinding", value="jaxb")

})
public class AdderServiceImpl implements AdderService {
    @Override
    public int add(int a, int b) {
        return a+b;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Activate
    protected void activate(ComponentContext ccontext){
        System.out.println("activate AdderService");
    }

    @Deactivate
    protected void deactivate(ComponentContext ccontext){
        System.out.println("de-activate AdderService");
    }
}
