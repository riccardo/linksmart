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
 * Copyright (C) 2006-2010
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
package eu.linksmart.upnp;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.osgi.service.upnp.UPnPDevice;
import org.osgi.service.upnp.UPnPIcon;
import org.osgi.service.upnp.UPnPService;

import eu.linksmart.upnp.UPnPIconImpl;
import eu.linksmart.upnp.UPnPServiceImpl;

/**
 * <b>Class UPnPDeviceImpl</b>
 * This class represents an UPnP device. 
 * An UPnP device has zero or more icons and provides zero or more services.
 * This class implements the org.osgi.service.upnp.UPnPDevice interface.
 *
 */
public class UPnPDeviceImpl
{
  private Dictionary properties;
  private Vector<UPnPIconImpl> icons;
  private Hashtable<String, UPnPServiceImpl> services;
  private String deviceType;
  private String presentationURL;
  private String friendlyName;
  private String manufacturer;
  private String manufacturerURL;
  private String modelDescription;
  private String modelName;
  private String modelNumber;
  private String modelURL;
  private String UDN;
  private String baseURL;
  private String deviceFile;
  private boolean isLinkSmart;

  public UPnPDeviceImpl()
  {
    this.properties = new Hashtable();
    this.services = new Hashtable();
    this.icons = new Vector();
  }

  public String getDeviceType()
  {
    return this.deviceType;
  }

  public void setDeviceType(String devType)
  {
    this.deviceType = devType;
  }

  public String getDeviceFile()
  {
    return this.deviceFile;
  }

  public void setDeviceFile(String file)
  {
    this.deviceFile = file;
  }

  public String getPresentationUrl()
  {
    return this.presentationURL;
  }

  public void setPresentationUrl(String url)
  {
    this.presentationURL = url;
  }

  public String getBaseUrl()
  {
    return this.baseURL;
  }

  public void setBaseUrl(String url)
  {
    this.baseURL = url;
  }

  public String getFriendlyName()
  {
    return this.friendlyName;
  }

  public void setFriendlyName(String frName)
  {
    this.friendlyName = frName;
  }

  public String getManufacturer()
  {
    return this.manufacturer;
  }

  public void setManufacturer(String manuf)
  {
    this.manufacturer = manuf;
  }

  public String getManufacturerURL()
  {
    return this.manufacturerURL;
  }

  public void setManufacturerURL(String url)
  {
    this.manufacturerURL = url;
  }

  public String getModelDescription()
  {
    return this.modelDescription;
  }

  public void setModelDescription(String modelDesc)
  {
    this.modelDescription = modelDesc;
  }

  public String getModelName()
  {
    return this.modelName;
  }

  public void setModelName(String modelName)
  {
    this.modelName = modelName;
  }

  public String getModelNumber()
  {
    return this.modelNumber;
  }

  public void setModelNumber(String modelNumber)
  {
    this.modelNumber = modelNumber;
  }

  public String getModelURL()
  {
    return this.modelURL;
  }

  public void setModelURL(String modelUrl)
  {
    this.modelURL = modelUrl;
  }

  public String getUDN()
  {
    return this.UDN;
  }

  public void setUDN(String udn)
  {
    this.UDN = udn;
  }

  public void addService(UPnPServiceImpl service)
  {
    this.services.put(service.getId(), service);
  }

  public UPnPServiceImpl getService(String serviceId)
  {
    return (UPnPServiceImpl)this.services.get(serviceId);
  }

  public UPnPServiceImpl[] getServicesImpl()
  {
    Enumeration e = this.services.elements();
    if (e == null)
      return null;
    UPnPServiceImpl[] uPnPser = new UPnPServiceImpl[this.services.size()];
    int i = 0;
    while (e.hasMoreElements()) {
      uPnPser[i] = ((UPnPServiceImpl)e.nextElement());
      i++;
    }
    return uPnPser;
  }

  public Dictionary getDescriptions(String locale)
  {
    if (locale != null) {
      return null;
    }
    return this.properties;
  }

  public void setProperty(String property, Object obj)
  {
    this.properties.remove(property);
    this.properties.put(property, obj);
  }

  public void addIcon(UPnPIconImpl icon)
  {
    this.icons.add(icon);
  }

  public void setLinkSmart(boolean linksmart)
  {
    this.isLinkSmart = linksmart;
  }

  public boolean isLinkSmart()
  {
    return this.isLinkSmart;
  }
}