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
 * Copyright (C) 2006-2010 [University of Paderborn]
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

package eu.linksmart.limbo.storagemanagerdevice.backend;

import java.util.Collection;

import org.jdom.Element;

import eu.linksmart.storage.helper.StringResponse;
import eu.linksmart.storage.helper.VoidResponse;

public interface StorageDeviceBackend {

	/**
	 * Creates a new Storage Device and updates all referred devices.
	 * 
	 * @param config
	 *            the Configuration of the device to be created
	 * @return The updated Configuration including ID and SystemID
	 */
	public StringResponse createStorageDevice(String config);

	/**
	 * Creates a new Storage Device without updating referred devices. This
	 * method is used for communication between Storage Managers.
	 * 
	 * @param config
	 *            the Configuration of the device to be created
	 * @return The updated Configuration including ID and SystemID
	 */
	public StringResponse createStorageDeviceLocal(String config);

	/**
	 * Delete the Storage Device on all Storage Managers. This method is used
	 * for communication between Storage Managers.
	 * 
	 * @param id
	 *            The ID of the Storage Device to be removed.
	 * @return
	 */
	public VoidResponse deleteStorageDevice(String id);

	/**
	 * Delete the Storage Device on all Storage Managers.
	 * 
	 * @param id
	 *            The ID of the Storage Device to be removed.
	 * @return
	 */
	public VoidResponse deleteStorageDeviceLocal(String id);

	/**
	 * Returns a list of all supported Storage Devices. These are also the names
	 * of the root Elements of a Configuration of such devices.
	 * 
	 * @return a list of the supported devices
	 */
	public Collection<String> getSupportedStorageDevices();

	/**
	 * Returns a list of the IDs of all local Storage Devices supported by this
	 * StorageDeviceBackend.
	 * 
	 * @return a list of the IDs of all local Storage Devices supported by this
	 *         StorageDeviceBackend
	 */
	public Collection<String> getStorageDevices();

	/**
	 * Returns the actual Configuration of the given Device.
	 * 
	 * @param id
	 *            The Device the configuration is wanted for
	 * @return The configuration of the device
	 */
	public StringResponse getStorageDeviceConfig(java.lang.String id);

	/**
	 * Test if the given ID is hosted locally by this kind of StorageDevice.
	 * 
	 * @param id
	 *            The ID to be tested.
	 * @return <code>true</code> if the device is hosted by this
	 *         StorageDeviceBackend, <code>false</code> if not.
	 */
	public boolean hasID(String id);

	/**
	 * Test if the given Configuration can be handled by this StorageDevice.
	 * 
	 * @param e
	 *            The configuration to be tested
	 * @return <code>true</code> if the configuration can be handled by this
	 *         StorageDeviceBackend, <code>false</code> if not.
	 */
	public boolean supportsConfig(String config);

	/**
	 * Update the configuration of a device. The Device is specified by the ID,
	 * which is part of the Configuration.
	 * 
	 * @param config
	 *            The new Configuration.
	 * @return Only Error Messages.
	 */
	public VoidResponse updateStorageDevice(String config);

	/**
	 * Update the configuration of a device only on this host. The Device is
	 * specified by the ID, which is part of the Configuration. This method is
	 * used for communication between Storage Managers.
	 * 
	 * @param config
	 *            The new Configuration.
	 * @return Only Error Messages.
	 */
	public VoidResponse updateStorageDeviceLocal(String config);
}
