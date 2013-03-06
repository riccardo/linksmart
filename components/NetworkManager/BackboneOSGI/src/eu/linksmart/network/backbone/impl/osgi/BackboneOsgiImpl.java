package eu.linksmart.network.backbone.impl.osgi;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import eu.linksmart.network.NMResponse;
import eu.linksmart.network.VirtualAddress;
import eu.linksmart.network.backbone.Backbone;
import eu.linksmart.network.routing.BackboneRouter;
import eu.linksmart.security.communication.SecurityProperty;

public class BackboneOsgiImpl implements Backbone{
	private Map<VirtualAddress, String> addressEndpointMap = null;
	private Logger LOG = Logger.getLogger(BackboneOsgiImpl.class.getName());
	private BundleContext bundleContext;
	private BackboneRouter bRouter;
	private static final String ENDPOINT_UNREACHABLE = "Unknown how to reach endpoint";

	@Override
	public NMResponse sendDataSynch(VirtualAddress senderVirtualAddress,
			VirtualAddress receiverVirtualAddress, byte[] data) {
		return executeServiceCall(receiverVirtualAddress, data);
	}

	@Override
	public NMResponse sendDataAsynch(VirtualAddress senderVirtualAddress,
			VirtualAddress receiverVirtualAddress, byte[] data) {
		//make call look asynchronous by return the status and separately sending response
		NMResponse resp = executeServiceCall(receiverVirtualAddress, data);
		if(bRouter != null) {
			Thread sender = new Thread(new ResponseSender(
					senderVirtualAddress, 
					receiverVirtualAddress, 
					resp));
			sender.start();
		}
		return new NMResponse(NMResponse.STATUS_SUCCESS);	
	}

	@Override
	public NMResponse receiveDataSynch(VirtualAddress senderVirtualAddress,
			VirtualAddress receiverVirtualAddress, byte[] data) {
		if(bRouter != null) {
			bRouter.receiveDataSynch(
					senderVirtualAddress, 
					receiverVirtualAddress, 
					data, 
					this);
			return new NMResponse(NMResponse.STATUS_SUCCESS);
		} else {
			NMResponse response = new NMResponse(NMResponse.STATUS_ERROR);
			response.setMessage("No BackboneRouter available");
			return response;
		}
	}

	@Override
	public NMResponse receiveDataAsynch(VirtualAddress senderVirtualAddress,
			VirtualAddress receiverVirtualAddress, byte[] data) {
		if(bRouter != null) {
			bRouter.receiveDataAsynch(
					senderVirtualAddress, 
					receiverVirtualAddress, 
					data, 
					this);
			return new NMResponse(NMResponse.STATUS_SUCCESS);
		} else {
			NMResponse response = new NMResponse(NMResponse.STATUS_ERROR);
			response.setMessage("No BackboneRouter available");
			return response;
		}
	}

	@Override
	public NMResponse broadcastData(VirtualAddress senderVirtualAddress,
			byte[] data) {
		//check for all available endpoints whether they have a receive data method
		List<ServiceMethodPair> broadcastableServices = new ArrayList<ServiceMethodPair>();
		Set<VirtualAddress> availableEndpoints = this.addressEndpointMap.keySet();

		for (VirtualAddress vAddress : availableEndpoints) { 
			ServiceReference sRef = 
					bundleContext.getServiceReference(
							this.addressEndpointMap.get(
									this.addressEndpointMap.get(vAddress)));
			Class<?> service = this.bundleContext.getService(sRef).getClass();
			Method[] methods = service.getMethods();

			for (Method method : methods) {
				//check if method name matches
				if (method.getName().contains("receiveData")) {
					//check if parameter requires sender and data
					List<Class<?>> parameters = 
							new ArrayList<Class<?>>(
									Arrays.asList(method.getParameterTypes()));
					if(parameters.contains(VirtualAddress.class)
							&& parameters.contains(byte[].class)) {
						broadcastableServices.add(
								new ServiceMethodPair(sRef, method));
					}	
				}
			}
		}

		//invoke all broadcastable services
		boolean success = true;
		for(ServiceMethodPair sm : broadcastableServices) {
			Object s = bundleContext.getService(sm.getService());
			//fill matching parameters
			Object[] parameters = new Object[sm.getMethod().getParameterTypes().length];
			List<Class<?>> params = 
					new ArrayList<Class<?>>(
							Arrays.asList(sm.getMethod().getParameterTypes()));
			//as it has already been checked that the parameters exist no null check
			parameters[params.indexOf(VirtualAddress.class)] = senderVirtualAddress;
			parameters[params.indexOf(byte[].class)] = data;

			try {
				sm.getMethod().invoke(s, parameters);
			} catch (Exception e) {
				LOG.debug("Could not broadcast data to service: " + s.getClass().getName());
				success = false;
			}
		}

		if(success) {
			return new NMResponse(NMResponse.STATUS_SUCCESS);
		} else {
			return new NMResponse(NMResponse.STATUS_ERROR);
		}
	}

	@Override
	public String getEndpoint(VirtualAddress virtualAddress) {
		return this.addressEndpointMap.get(virtualAddress);
	}

	@Override
	public boolean addEndpoint(VirtualAddress virtualAddress, String endpoint) {
		if (this.addressEndpointMap.containsKey(virtualAddress)) {
			LOG.debug("Virtual Address "
					+ virtualAddress.toString() + " is already registered");
			return false;
		} else {
			this.addressEndpointMap.put(virtualAddress, endpoint);
			return true;
		}
	}

	@Override
	public boolean removeEndpoint(VirtualAddress virtualAddress) {
		if (this.addressEndpointMap.containsKey(virtualAddress)) {
			this.addressEndpointMap.remove(virtualAddress);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public String getName() {
		return BackboneOsgiImpl.class.getName();
	}

	@Override
	public List<SecurityProperty> getSecurityTypesRequired() {
		ArrayList<SecurityProperty> secProps = new ArrayList<SecurityProperty>();
		secProps.add(SecurityProperty.NoEncoding);
		secProps.add(SecurityProperty.NoSecurity);

		return secProps;
	}

	@Override
	public void addEndpointForRemoteService(
			VirtualAddress senderVirtualAddress,
			VirtualAddress remoteVirtualAddress) {
		this.addressEndpointMap.put(
				remoteVirtualAddress, this.addressEndpointMap.get(senderVirtualAddress));
	}

	protected void activate (ComponentContext context) {
		this.bundleContext = context.getBundleContext();
		this.addressEndpointMap = new ConcurrentHashMap<VirtualAddress, String>();
		
		this.bRouter = (BackboneRouter) context
				.locateService(BackboneRouter.class.getSimpleName());
		
		LOG.info("BackboneOSGI started");
	}

	protected void deactivate (ComponentContext context) {
		LOG.info("BackboneOSGI stopped");
	}

	private String getHttpBody(byte[] data) {
		String httpMsg = new String(data);
		//take headers from data and add them to response
		String[] headers = httpMsg.split("(?<=\r\n)");
		//use it to get index of data element
		int i = 0;
		//go through headers until empty line is reached
		for (String header : headers) {	
			if(header.contentEquals("\r\n")) {
				break;
			}
			i++;
		}
		String body = new String();
		//concat remaining elements of 'headers' array (the real data) into response body
		for(i++;i < headers.length;i++) {
			body = body.concat(headers[i]);
		}
		return body;
	}

	private String putReturnValueSoap(String methodName, Object returnValue) {
		return "HTTP/1.1 200 OK\r\n\r\n" +
				"<?xml version=\"1.0\" encoding=\"utf-8\"?>"
				+ "<soap:Envelope "
				+ "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
				+ "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "
				+ "xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
				+ "<soap:Body><" + methodName + "Response>"
				+ "<return>" + String.valueOf(returnValue) + "</return>"
				+ "</" + methodName + "Response></soap:Body></soap:Envelope>";
	}

	private static String[] getParametersFromSoap(Node body) throws IOException{
		Node method = body.getFirstChild();
		if (method == null) {
			throw new IOException("No method element in body");
		} else {
			//put the values of the child nodes into an array
			NodeList parameters = method.getChildNodes();
			String[] paramsArray = new String[parameters.getLength()];
			for (int i=0; i<parameters.getLength();i++) {
				Node value = parameters.item(i).getFirstChild();
				if(value != null) {
					paramsArray[i] = value.getNodeValue();
				} else {
					throw new IOException("Error in SOAP message parameters");
				}
			}
			return paramsArray;
		}
	}

	private Node getSoapBody(String data) throws IOException {
		String soapMsg = data.toLowerCase();
		if (!soapMsg.contains("envelope")) {
			throw new IOException("Not SOAP message");
		} else {
			try {
				//create the appropriate objects for the soap parsing
				ByteArrayInputStream bis = new ByteArrayInputStream(soapMsg.getBytes());
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				dbf.setNamespaceAware(true);
				DocumentBuilder db = dbf.newDocumentBuilder();
				Document doc = db.parse(bis);

				//find body element
				NodeList nList = doc.getElementsByTagNameNS("*","body");
				if (!(nList.getLength() > 0)) {
					throw new IOException("No body element in message");
				} else {
					return nList.item(0);
				}
			} catch (ParserConfigurationException e) {
				throw new IOException(
						"Error parsing SOAP message: " + e.getMessage());
			} catch (SAXException e) {
				throw new IOException(
						"Error parsing SOAP message: " + e.getMessage());
			}
		}
	}

	private String getMethodFromSoap(Node body) throws IOException {
		//first child of body is the method
		Node method = body.getFirstChild();
		if (method == null) {
			throw new IOException("No method element in body");
		} else {
			return method.getNodeName();
		}
	}

	private Object convert(Class<?> targetType, String text) {
		PropertyEditor editor = PropertyEditorManager.findEditor(targetType);
		editor.setAsText(text);
		return editor.getValue();
	}

	private NMResponse executeServiceCall(VirtualAddress receiverVirtualAddress, byte[] data) {
		//create response object to be removed
		NMResponse resp = new NMResponse(NMResponse.STATUS_ERROR);

		//check if address is registered here
		if (!this.addressEndpointMap.containsKey(receiverVirtualAddress)) {
			resp.setMessage(ENDPOINT_UNREACHABLE);
			return resp;
		}

		ServiceReference sRef = 
				bundleContext.getServiceReference(
						this.addressEndpointMap.get(receiverVirtualAddress));

		//check if service is available
		if(sRef == null) {
			resp.setMessage(ENDPOINT_UNREACHABLE);
			return resp;
		}

		try {
			//get the method and parameters from the soap message
			String httpBody = getHttpBody(data);
			Node body = getSoapBody(httpBody);
			String methodName = getMethodFromSoap(body);
			String[] parametersStr = getParametersFromSoap(body);

			//get matching service over reflection
			Object serviceProvider = this.bundleContext.getService(sRef);
			Method[] services = serviceProvider.getClass().getMethods();
			Method service = null;
			boolean foundMethod = false;
			for (int i=0; i<services.length;i++) {
				//as soap messages not always contain type information we match against number of parameters
				if(services[i].getName().toLowerCase().contentEquals(methodName) &&
						services[i].getParameterTypes().length == parametersStr.length) {
					service = services[i];
					foundMethod = true;
					break;
				}
			}

			//if no matching method has been found return an error
			if(!foundMethod) {
				LOG.debug("Cannot find service " + methodName + " for VirtualAddress " + receiverVirtualAddress);
				resp.setMessage("Error finding service");
				return resp;
			}
			//try to parse the parameter strings to the method's parameters
			Object[] parameters = new Object[parametersStr.length];
			Class<?>[] parameterTypes = service.getParameterTypes();
			int i = 0;
			try {
				for (i=0; i<parameters.length; i++) {
					parameters[i] = convert(parameterTypes[i], parametersStr[i]);
				}
			} catch (Exception e) {
				LOG.debug("Error parsing parameter of type: " + parameterTypes[i].getName());
				throw new IOException("Cannot parse parameters: " + e.getMessage());
			}

			//consume the service and return it
			Object returnValue = service.invoke(serviceProvider, parameters);
			String soapResponse = putReturnValueSoap(methodName, returnValue);
			resp.setMessage(soapResponse);
		} catch (IOException e) {
			LOG.debug("Cannot get method from SOAP envelope");
			resp.setMessage("Error interpreting request: " + e.getMessage());
		} catch (IllegalAccessException e) {
			LOG.debug("Cannot access service");
			resp.setMessage("Error accessing service: " + e.getMessage());
		} catch (InvocationTargetException e) {
			LOG.debug("Error while consuming service");
			resp.setMessage("Error while consuming service: " + e.getMessage());
		}
		return resp;
	}

	private class ResponseSender implements Runnable {
		NMResponse response;
		private VirtualAddress senderVirtualAddress;
		private VirtualAddress receiverVirtualAddress;

		protected ResponseSender(
				VirtualAddress senderVirtualAddress, 
				VirtualAddress receiverVirtualAddress,
				NMResponse response) {
			this.senderVirtualAddress = senderVirtualAddress;
			this.receiverVirtualAddress = receiverVirtualAddress;
			this.response = response;
		}

		public void run() {
			bRouter.sendDataAsynch(
					senderVirtualAddress, 
					receiverVirtualAddress, 
					response.getMessage().getBytes());
		}
	}

	private class ServiceMethodPair {
		ServiceReference service;
		Method method;

		protected ServiceMethodPair(ServiceReference service, Method method) {
			this.service = service;
			this.method = method;
		}

		protected ServiceReference getService() {
			return this.service;
		}

		protected Method getMethod() {
			return this.method;
		}
	}
}
