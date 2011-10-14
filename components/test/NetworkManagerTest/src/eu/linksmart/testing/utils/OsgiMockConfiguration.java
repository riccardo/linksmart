package eu.linksmart.testing.utils;

import java.io.IOException;
import java.util.Dictionary;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

import eu.linksmart.network.backbone.BackboneManagerApplication;

public class OsgiMockConfiguration {
	
	private ComponentContext _componentContext;
	public ComponentContext getComponentContext() {
		return _componentContext;
	}

	private BundleContext _bundleContext;
	public BundleContext getBundleContext() {
		return _bundleContext;
	}
	
	private ServiceReference _serviceReference;
	public ServiceReference getServiceReference() {
		return _serviceReference;
	}
	
	private ConfigurationAdmin _configurationAdmin;
	public ConfigurationAdmin getConfigurationAdmin() {
		return _configurationAdmin;
	}
	
	private Configuration _configuration;
	public Configuration getConfiguration() {
		return _configuration;
	}
	
	private Dictionary _configurationProperties;
	public Dictionary getConfigurationProperties() {
		return _configurationProperties;
	}
	
	private HttpService _httpService;
	public HttpService getHttpService() {
		return _httpService;
	}
	
	public static OsgiMockConfiguration getMinimalConfiguratoin(final Mockery context) throws ServletException, NamespaceException, IOException {
		
		OsgiMockConfiguration result = new OsgiMockConfiguration();
		
		result._componentContext = context.mock(ComponentContext.class);
		result._bundleContext = context.mock(BundleContext.class);
		result._serviceReference = context.mock(ServiceReference.class);
		result._configurationAdmin = context.mock(ConfigurationAdmin.class);
		result._configuration = context.mock(Configuration.class);
		result._configurationProperties = context.mock(Dictionary.class);
		result._httpService = context.mock(HttpService.class);
		
		initHttpService(context, result._httpService);
		initConfigurationProperties(context, result._configurationProperties);
		initConfiguration(context, result._configuration, result._configurationProperties);
		initConfigurationAdmin(context, result._configurationAdmin, result._configuration);
		initBundleContext(context, result._bundleContext, result._serviceReference, result._configurationAdmin);
		initComponentContext(context, result._componentContext, result._bundleContext, result._httpService);
		
		return result;
	}

	private static void initComponentContext(final Mockery context,
			final ComponentContext cc, final BundleContext bundleContext,
			final HttpService httpService) {
		
		context.checking(new Expectations() {{
			((ComponentContext)one(cc)).getBundleContext();
			will(returnValue(bundleContext));
			((ComponentContext)one(cc)).locateService(with(equal("HttpService")));
			will(returnValue(httpService));
		}});
	}

	private static void initBundleContext(final Mockery context,
			final BundleContext bundleContext, final ServiceReference configurationAdminServiceReference,
			final ConfigurationAdmin configurationAdmin) {
		
		context.checking(new Expectations() {{
			((BundleContext)one(bundleContext)).getServiceReference(with(equal(ConfigurationAdmin.class.getName())));
			will(returnValue(configurationAdminServiceReference));
			((BundleContext)one(bundleContext)).getService(with(equal(configurationAdminServiceReference)));
			will(returnValue(configurationAdmin));
			((BundleContext)one(bundleContext)).registerService(with(any(String.class)), with(any(Object.class)), with(any(Dictionary.class)));
			will(returnValue(context.mock(ServiceRegistration.class)));
		}});
	}

	private static void initConfigurationAdmin(final Mockery context,
			final ConfigurationAdmin confAdmin, final Configuration config)
			throws IOException {
		
		context.checking(new Expectations() {{
			((ConfigurationAdmin)one(confAdmin)).getConfiguration(with(any(String.class)));
			will(returnValue(config));
		}});
	}

	private static void initConfiguration(final Mockery context,
			final Configuration config, final Dictionary configProps) {
		
		context.checking(new Expectations() {{
			((Configuration)allowing(config)).getProperties();
			will(returnValue(configProps));
		}});
	}

	private static void initConfigurationProperties(final Mockery context, 
			final Dictionary configProps) {
		
		context.checking(new Expectations() {{
			((Dictionary)allowing(configProps)).get(with(equal("TrustManager.trustThreshold")));
			will(returnValue("0.0"));
			((Dictionary)allowing(configProps)).get(with(equal("TrustManager.trustManagerURL")));
			will(returnValue("http://localhost:9090/TrustManager/TrustManagerService"));
			((Dictionary)allowing(configProps)).get(with(equal("Session.GeneratorName")));
			will(returnValue("UUID"));
			((Dictionary)allowing(configProps)).get(with(equal("Session.Delay")));
			will(returnValue("60000"));
			((Dictionary)allowing(configProps)).get(with(equal("Session.DataPath")));
			will(returnValue("NetworkManager/linksmart-sessions/"));
			((Dictionary)allowing(configProps)).get(with(equal("Session.MaxClients")));
			will(returnValue("1000"));
			((Dictionary)allowing(configProps)).get(with(equal("Session.MaxServers")));
			will(returnValue("1000"));
			((Dictionary)allowing(configProps)).get(with(equal("Session.CleaningFrequency")));
			will(returnValue("1000"));
			((Dictionary)allowing(configProps)).get(with(equal("Session.CleaningFrequency")));
			will(returnValue("10000"));
			((Dictionary)allowing(configProps)).get(with(equal("Session.SyncFrequency")));
			will(returnValue("10000"));
			((Dictionary)allowing(configProps)).get(with(equal(BackboneManagerApplication.servicePath)));
			will(returnValue(BackboneManagerApplication.servicePath));
			
			((Dictionary)allowing(configProps)).get(with(equal("Backbone.JXTALogs")));
			will(returnValue("false"));
			
			((Dictionary)allowing(configProps)).get(with(equal("NetworkManager.HID")));
			will(returnValue("10.10.10.10"));
			
			((Dictionary)allowing(configProps)).get(with(equal("Backbone.PeerName")));
			will(returnValue("LinkSmartNetworkManager"));
			((Dictionary)allowing(configProps)).get(with(equal("Backbone.AnnounceValidity")));
			will(returnValue("5000"));
			((Dictionary)allowing(configProps)).get(with(equal("Backbone.Factor")));
			will(returnValue("3"));
			((Dictionary)allowing(configProps)).get(with(equal("Backbone.WaitForRdvTime")));
			will(returnValue("5000"));
			((Dictionary)allowing(configProps)).get(with(equal("Backbone.Synchronized")));
			will(returnValue("No"));
			((Dictionary)allowing(configProps)).get(with(equal("Backbone.Mode")));
			will(returnValue("Node"));
			((Dictionary)allowing(configProps)).get(with(equal("Backbone.Relayed")));
			will(returnValue("true"));
			((Dictionary)allowing(configProps)).get(with(equal("Backbone.Multicast")));
			will(returnValue("true"));
			((Dictionary)allowing(configProps)).get(with(equal("Backbone.TcpPort")));
			will(returnValue("9701"));
			((Dictionary)allowing(configProps)).get(with(equal("Backbone.HttpPort")));
			will(returnValue("9700"));
			((Dictionary)allowing(configProps)).get(with(equal("NetworkManager.Description")));
			will(returnValue("NetworkManager:LinkSmartUser"));
			((Dictionary)allowing(configProps)).get(with(equal("Network.MultimediaPort")));
			will(returnValue("9091"));
			
			((Dictionary)allowing(configProps)).get(with(equal("Security.Protocol")));
			will(returnValue("securesession"));
			((Dictionary)allowing(configProps)).get(with(equal("secureSessionServerHID")));
			will(returnValue("11.12.13.14"));
			((Dictionary)allowing(configProps)).get(with(equal("startSecureSessionGUI")));
			will(returnValue("true"));
			((Dictionary)allowing(configProps)).get(with(equal("Security.Access.DefaultDeny")));
			will(returnValue("false"));
		}});
	}

	private static void initHttpService(final Mockery context, 
			final HttpService httpService) throws ServletException, NamespaceException {
		
		context.checking(new Expectations() {{
			((HttpService)allowing(httpService)).registerServlet(with(any(String.class)), with(any(Servlet.class)), with(any(Dictionary.class)), with(any(HttpContext.class)));
			((HttpService)allowing(httpService)).registerResources(with(any(String.class)), with(any(String.class)), with(any(HttpContext.class)));
		}});
	}
}
