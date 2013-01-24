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
 * Copyright (C) 2006-2010 University of Reading,
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
package eu.linksmart.policy.pdp;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Policy Decision Point (PDP) administrator interface
 * 
 * The PDP administrator exposes policy CRUD functionalities, and 
 * functionalities to activate and deactivate policies.
 * 
 * Additionally, it exposes functionalities to set generic properties in the 
 * PDP administrator implementation itself. There are currently no mandatory 
 * properties that must be handled by implementing classes.
 * 
 * PdpAdmin implementations must throw a {@link RemoteException} whenever 
 * any of the causing errors specified in the method descriptions given below 
 * occur. Server-side <code>Throwable</code>s may be returned as part of those 
 * exceptions. If an exception is thrown, the requested operation may not be 
 * carried out server-side. 
 * 
 * {@link PdpAdminError} enumerates those errors and should be used 
 * to throw {@link RemoteException}s server-side and to parse them client-side. 
 * 
 * @author Michael Crouch
 * @author Marco Tiemann
 * 
 * @see PdpAminErrors
 */
public interface PdpAdmin extends Remote {
	
	/** 
	 * Activates the argument policy
	 * 
	 * @param thePolicyId
	 * 				the policy ID to activate
	 * @return
	 * 				true if the argument policy has been activated or was 
	 * 					already activated before that method call
	 * 				false if not and no specific exception is thrown
	 * @throws RemoteException
	 * 				PdpAdminError.PDP_ADMIN_INTERNAL_ERROR 
	 * 					if the PDP administrator is not in a working state 
	 * 				PdpAdminError.POLICY_NOT_FOUND 
	 * 					if no policy could be found for the argument ID 
	 * 				any non-PDP-admin {@link RemoteException} that is thrown
	 */
    public boolean activatePolicy(String thePolicyId) throws RemoteException;
    
    /**
     * Deactivates the argument policy
     * 
     * @param thePolicyId
     * 				the policy ID to deactivate
     * @return
	 * 				true if the argument policy has been deactivated or was 
	 * 					already deactivated before that method call 
	 * 				false if not and no specific exception is thrown
     * @throws RemoteException
     * 				PdpAdminError.PDP_ADMIN_INTERNAL_ERROR 
	 * 					if the PDP administrator is not in a working state
	 * 				PdpAdminError.POLICY_NOT_FOUND 
	 * 					if no policy could be found for the argument ID
     * 				any non-PDP-admin {@link RemoteException} that is thrown
     */
    public boolean deactivatePolicy(String thePolicyId) throws RemoteException;
    
    /**
     * Returns the policy XML <code>String</code>
     * 
     * @param thePolicyId
     * 				the policy ID
     * @return
     * 				the policy XML <code>String</code>, never <code>null</code> 
     * 				or a malformed <code>String</code>
     * @throws RemoteException
     * 				PdpAdminError.PDP_ADMIN_INTERNAL_ERROR 
	 * 					if the PDP administrator is not in a working state 
	 * 				PdpAdminError.POLICY_NOT_FOUND
	 * 					if no policy could be found for the argument ID 
	 * 				PdpAdminError.POLICY_NOT_VALID
	 * 					if an invalid policy was found
     * 				any non-PDP-admin {@link RemoteException} that is thrown
     */
    public String getPolicy(String thePolicyId) throws RemoteException;
    
    /**
     * @return
     * 				the list of active policies, an empty list if no active 
     * 				policies are present
     * @throws RemoteException
     * 				PdpAdminError.PDP_ADMIN_INTERNAL_ERROR 
	 * 					if the PDP administrator is not in a working state
     * 				any non-PDP-admin {@link RemoteException} that is thrown
     */
    public String[] getActivePolicyList() throws RemoteException;
    
    /**
     * @return
     * 				the list of inactive policies, an empty list if no active 
     * 				policies are present
     * @throws RemoteException
     * 				PdpAdminError.PDP_ADMIN_INTERNAL_ERROR 
	 * 					if the PDP administrator is not in a working state 
     * 				any non-PDP-admin {@link RemoteException} that is thrown
     */
    public String[] getInActivePolicyList() throws RemoteException;
    
    /**
     * Publishes a policy to the PDP policy repository 
     * 
     * @param thePolicyId
     * 				the policy ID
     * @param thePolicy
     * 				the policy XML <code>String</code>
     * @return
     * 				true iff the argument policy has been published, 
	 * 				false if not and no specific exception is thrown
     * @throws RemoteException
     * 				PdpAdminError.PDP_ADMIN_INTERNAL_ERROR 
	 * 					if the PDP administrator is not in a working state 
     * 				PdpAdminError.POLICY_ID_ALREADY_TAKEN 
     * 					if the argument policy ID is already taken 
     * 				PdpAdminError.POLICY_NOT_VALID
     * 					if the argument policy is not a valid XACML policy 
     * 				any non-PDP-admin {@link RemoteException} that is thrown
     */
    public boolean publishPolicy(String thePolicyId, String thePolicy) 
    		throws RemoteException;
    
    /**
     * Removes a policy from the PDP policy repository 
     * 
     * @param thePolicyId
     * 				the policy ID to remove
     * @return
     * 				true if the argument policy has been removed or was not 
     * 				there in the first place
     * 				false if not and no specific exception is thrown
     * @throws RemoteException
     * 				PdpAdminError.PDP_ADMIN_INTERNAL_ERROR 
	 * 					if the PDP administrator is not in a working state 
     * 				any non-PDP-admin {@link RemoteException} that is thrown
     */
    public boolean removePolicy(String thePolicyId) throws RemoteException;
        
    /**
     * @param theKey
     * 				the property key
     * @return
     * 				the property value, may be <code>null</code> iff the 
     * 				property is supported and has been set to <code>null</code> 
     * @throws RemoteException
     * 				PdpAdminError.PDP_ADMIN_INTERNAL_ERROR 
	 * 					if the PDP administrator is not in a working state 
	 * 				PdpAdminError.PROPERTY_NOT_SUPPORTED
	 * 					if the property is not supported by the implementation 
     * 				any non-PDP-admin {@link RemoteException} that is thrown
     */
    public String getProperty(String theKey) throws RemoteException;
    
    /**
     * @param theKey
     * 				the property key
     * @param theValue
     * 				the property value
     * @return
     * 				true iff the argument property has been set,
     * 				false in any other case
     * @throws RemoteException
     * 				PdpAdminError.PDP_ADMIN_INTERNAL_ERROR 
	 * 					if the PDP administrator is not in a working state 
	 * 				PdpAdminError.PROPERTY_NOT_SUPPORTED
	 * 					if the property is not supported by the implementation 
     * 				any non-PDP-admin {@link RemoteException} that is thrown
     */
    public boolean setProperty(String theKey, String theValue) 
    		throws RemoteException;
    
}
