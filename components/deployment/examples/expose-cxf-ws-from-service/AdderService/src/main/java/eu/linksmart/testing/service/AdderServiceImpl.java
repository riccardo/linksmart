package eu.linksmart.testing.service;

import org.apache.felix.scr.annotations.*;
import org.osgi.service.component.ComponentContext;

@Component(name="AdderService", immediate=true)
@Service({AdderService.class})
@Properties({
        @Property(name="service.exported.interfaces", value="*"),
        @Property(name="service.exported.configs", value="org.apache.cxf.ws"),
        @Property(name="org.apache.cxf.ws.address", value="http://localhost:9191/cxf/services/AdderService")
})
public class AdderServiceImpl implements AdderService {
    @Override
    public int add(int a, int b) {
        return a+b;
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
