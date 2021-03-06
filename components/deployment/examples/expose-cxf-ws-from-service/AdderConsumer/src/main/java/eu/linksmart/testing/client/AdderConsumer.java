package eu.linksmart.testing.client;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.osgi.service.component.ComponentContext;

import javax.xml.namespace.QName;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by carlos on 07.05.14.
 * Simple consumer of AdderService
 */
@Component(name="AdderConsumer", immediate=true)
public class AdderConsumer{

        URL wsdlURL;
        Object[] response;

        @Activate
        protected void activate(ComponentContext ccontext){
            System.out.println("activate AdderConsumer");

            System.out.println("calling ws...");

            try {
                wsdlURL = new URL("http://localhost:9191/cxf/services/AdderService?wsdl");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
            Client client = dcf.createClient("http://localhost:9191/cxf/services/AdderService?wsdl");

            try {

                // ten continous calls of the ws
                for(int i=0; i < 10; i++){
                    response = client.invoke(new QName("http://service.testing.linksmart.eu/","add"),i,0);
                    System.out.println("response from service: "+ response[0]);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Deactivate
        protected void deactivate(ComponentContext ccontext)
        {
            System.out.println("de-activate AdderConsumer");
        }
}
