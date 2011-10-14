package eu.linksmart.network.impl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Properties;
import java.util.concurrent.Callable;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.action.ReturnValueAction;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.runner.RunWith;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

import com.sun.org.apache.xpath.internal.axes.OneStepIterator;

import eu.linksmart.network.backbone.BackboneManagerApplication;
import eu.linksmart.network.identity.HIDManagerApplication;
import eu.linksmart.testing.utils.OsgiMockConfiguration;

@RunWith(value=JMock.class)
public class NetworkManagerApplicationSoapBindingImplTest {
	Mockery context;
    
    public NetworkManagerApplicationSoapBindingImplTest() {
        context = new JUnit4Mockery() {{
                setImposteriser(ClassImposteriser.INSTANCE);
        	}};
    }
    
    @Before
    public void setup() {
        System.setProperty("org.osgi.service.http.port", "8082");
    }
    
    @Test
    public void TestInit() throws IOException, ServletException, NamespaceException {
    	NetworkManagerApplicationSoapBindingImpl impl = new NetworkManagerApplicationSoapBindingImpl();
    	
    	OsgiMockConfiguration osgiMock = OsgiMockConfiguration.getMinimalConfiguratoin(context);
    	
    	impl.activate(osgiMock.getComponentContext());
    }
}
