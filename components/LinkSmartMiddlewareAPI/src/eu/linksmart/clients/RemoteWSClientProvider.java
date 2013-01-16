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

package eu.linksmart.clients;

/**
 * This interface can be used for retrieve the client stub for a
 * LinkSmart Java Manager
 **/
public interface RemoteWSClientProvider {
	
	/**
	 * Retrieves a list of available manager clients
	 * 
	 * @return an array with the list of available manager clients
	 */
	public String[] getManagerClients();
	
	/**
	 * This method retrieves the client stubs for a LinkSmart Java Manager. 
	 * Providing a className for the service desired, this method returns a 
	 * client Remote object, that can be casted to the specific service 
	 * interface in order to call the desired methods (it is required that 
	 * the Manager WS interface extends java.rmi.Remote
	 * <p>The interface of this method also accepts to configure the endpoint 
	 * where the service is located (even using SOAP Tunneling) and the Core 
	 * LinkSmart Security Configuration.
	 * 
	 * @param className The class name for the service interface to be called 
	 * (for example, eu.linksmart.network.NetworkManagerApplication)
	 * @param endpoint The endpoint to be used, or null if you want to use the 
	 * default one
	 * @param coreSecurityConfig true if you want use coreLinkSmartSecurity 
	 * (will encrypt messages)
	 * @throws Exception if there is an exception during code generation 
	 * @return {@link java.rmi.Remote} containing the client stubs for 
	 * the required service
	 */
	public Object getRemoteWSClient(String className, String endpoint) throws Exception; 
	
}
