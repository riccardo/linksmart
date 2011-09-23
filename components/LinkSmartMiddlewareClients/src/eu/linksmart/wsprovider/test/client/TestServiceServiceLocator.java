/*
 * In case of German law being applicable to this license agreement, the following warranty and liability terms shall apply:
 *
 * 1. Licensor shall be liable for any damages caused by wilful intent or malicious concealment of defects.
 * 2. Licensor's liability for gross negligence is limited to foreseeable, contractually typical damages.
 * 3. Licensor shall not be liable for damages caused by slight negligence, except in cases 
 *    of violation of essential contractual obligations (cardinal obligations). Licensee's claims for 
 *    such damages shall be statute barred within 12 months subsequent to the delivery of the software.
 * 4. As the Software is licensed on a royalty free basis, any liability of the Licensor for indirect damages 
 *    and consequential damages - except in cases of intent - is excluded.
 *
 * This limitation of liability shall also apply if this license agreement shall be subject to law 
 * stipulating liability clauses corresponding to German law.
 */
/**
 * Copyright (C) 2006-2010 [Telefonica I+D]
 *                         the HYDRA consortium, EU project IST-2005-034891
 *
 * This file is part of LinkSmart.
 *
 * LinkSmart is free software: you can redistribute it and/or modify
 * it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE
 * version 3 as published by the Free Software Foundation.
 *
 * LinkSmart is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with LinkSmart.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package eu.linksmart.wsprovider.test.client;


public class TestServiceServiceLocator 
		extends org.apache.axis.client.Service
		implements eu.linksmart.wsprovider.test.client.TestServiceService {

	public TestServiceServiceLocator() {}

	public TestServiceServiceLocator(org.apache.axis.EngineConfiguration config) {
		super(config);
	}

	public TestServiceServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
		super(wsdlLoc, sName);
	}

	/* Use to get a proxy class for TestCase. */
	private java.lang.String TestCase_address = "http://localhost:8082/axis/services/TestCase";

	public java.lang.String getTestCaseAddress() {
		return TestCase_address;
	}

	/* The WSDD service name defaults to the port name. */
	private java.lang.String TestCaseWSDDServiceName = "TestCase";

	public java.lang.String getTestCaseWSDDServiceName() {
		return TestCaseWSDDServiceName;
	}

	public void setTestCaseWSDDServiceName(java.lang.String name) {
		TestCaseWSDDServiceName = name;
	}

	public eu.linksmart.wsprovider.test.TestService getTestCase() 
			throws javax.xml.rpc.ServiceException {
		
		java.net.URL endpoint;
		try {
			endpoint = new java.net.URL(TestCase_address);
		} catch (java.net.MalformedURLException e) {
			throw new javax.xml.rpc.ServiceException(e);
		}
		return getTestCase(endpoint);
	}

	public eu.linksmart.wsprovider.test.TestService getTestCase(
			java.net.URL portAddress) throws javax.xml.rpc.ServiceException 
			{
		try {
			eu.linksmart.wsprovider.test.client.TestCaseSoapBindingStub _stub = 
				new eu.linksmart.wsprovider.test.client.TestCaseSoapBindingStub(
					portAddress, this);
			_stub.setPortName(getTestCaseWSDDServiceName());
			return _stub;
		} catch (org.apache.axis.AxisFault e) {
			return null;
		}
	}

	public void setTestCaseEndpointAddress(java.lang.String address) {
		TestCase_address = address;
	}

	/**
	 * For the given interface, get the stub implementation.
	 * If this service has no port for the given interface,
	 * then ServiceException is thrown.
	 */
	public java.rmi.Remote getPort(Class serviceEndpointInterface) 
			throws javax.xml.rpc.ServiceException {
		
		try {
			if (eu.linksmart.wsprovider.test.TestService.class.isAssignableFrom(
					serviceEndpointInterface)) {
				eu.linksmart.wsprovider.test.client.TestCaseSoapBindingStub _stub = 
					new eu.linksmart.wsprovider.test.client.TestCaseSoapBindingStub(
						new java.net.URL(TestCase_address), this);
				_stub.setPortName(getTestCaseWSDDServiceName());
				return _stub;
			}
		} catch (java.lang.Throwable t) {
			throw new javax.xml.rpc.ServiceException(t);
		}
		throw new javax.xml.rpc.ServiceException("There is no stub " +
			"implementation for the interface:  " + 
			(serviceEndpointInterface == null ? "null" : 
				serviceEndpointInterface.getName()));
	}

	/**
	 * For the given interface, get the stub implementation.
	 * If this service has no port for the given interface,
	 * then ServiceException is thrown.
	 */
	public java.rmi.Remote getPort(javax.xml.namespace.QName portName, 
			Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
		
		if (portName == null) {
			return getPort(serviceEndpointInterface);
		}
		java.lang.String inputPortName = portName.getLocalPart();
		if ("TestCase".equals(inputPortName)) {
			return getTestCase();
		}
		else  {
			java.rmi.Remote _stub = getPort(serviceEndpointInterface);
			((org.apache.axis.client.Stub) _stub).setPortName(portName);
			return _stub;
		}
	}

	public javax.xml.namespace.QName getServiceName() {
		return new javax.xml.namespace.QName(
			"http://test.wsprovider.linksmart.eu", "TestServiceService");
	}

	private java.util.HashSet ports = null;

	public java.util.Iterator getPorts() {
		if (ports == null) {
			ports = new java.util.HashSet();
			ports.add(new javax.xml.namespace.QName(
				"http://test.wsprovider.linksmart.eu", "TestCase"));
		}
		return ports.iterator();
	}

	/**
	 * Set the endpoint address for the specified port name.
	 */
	public void setEndpointAddress(java.lang.String portName, 
			java.lang.String address) throws javax.xml.rpc.ServiceException {
		
		if ("TestCase".equals(portName)) {
			setTestCaseEndpointAddress(address);
		}
		else {
			throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint " +
				"Address for Unknown Port" + portName);
		}
	}

	/**
	 * Set the endpoint address for the specified port name.
	 */
	public void setEndpointAddress(javax.xml.namespace.QName portName, 
			java.lang.String address) throws javax.xml.rpc.ServiceException {
		
		setEndpointAddress(portName.getLocalPart(), address);
	}

}
