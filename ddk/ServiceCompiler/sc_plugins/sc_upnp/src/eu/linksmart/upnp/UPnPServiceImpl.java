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

import java.util.Enumeration;
import java.util.Hashtable;

import org.osgi.service.upnp.UPnPAction;
import org.osgi.service.upnp.UPnPService;
import org.osgi.service.upnp.UPnPStateVariable;

import eu.linksmart.upnp.UPnPActionImpl;
import eu.linksmart.upnp.UPnPStateVariableImpl;

/**
 * <b>Class UPnPServiceImpl</b>
 * This class represents an UPnP Service. Each UPnP device contains zero or more services. 
 * The UPnP description for a service defines actions, their arguments, and event characteristics.
 * This class implements the org.osgi.service.upnp.UPnPStateVariable.
 *
 */
public class UPnPServiceImpl
{
  private Hashtable<String, UPnPActionImpl> actions;
  private Hashtable<String, UPnPStateVariableImpl> stateVariables;
  private String serviceType;
  private String serviceId;
  private String SCPDURL;
  private String controlUrl;
  private String eventSubUrl;
  private String serviceName;

  public UPnPServiceImpl()
  {
    this.actions = new Hashtable();
    this.stateVariables = new Hashtable();
  }

  public UPnPServiceImpl(Hashtable<String, UPnPActionImpl> act, Hashtable<String, UPnPStateVariableImpl> stateVariables, String sType, String servId, String SCPDUrl, String controlUrl, String eventSubUrl)
  {
    this.actions = act;
    this.stateVariables = stateVariables;
    this.serviceType = sType;
    this.serviceId = servId;
    this.SCPDURL = SCPDUrl;
    this.controlUrl = controlUrl;
    this.eventSubUrl = eventSubUrl;
  }

  public void setServiceName(String name) {
    this.serviceName = name;
  }

  public String getServiceName() {
    return this.serviceName;
  }

  public String getId()
  {
    return this.serviceId;
  }

  public void setId(String id)
  {
    this.serviceId = id;
  }

  public String getType()
  {
    return this.serviceType;
  }

  public void setType(String type)
  {
    this.serviceType = type;
  }

  public String getVersion()
  {
    String[] splited = this.serviceType.split(":");
    return splited[(splited.length - 1)];
  }

  public UPnPActionImpl getAction(String name)
  {
    return (UPnPActionImpl)this.actions.get(name);
  }

  public void addAction(UPnPActionImpl action)
  {
    this.actions.put(action.getName(), action);
  }

  public Hashtable<String, UPnPActionImpl> getActionsImplHash()
  {
    return this.actions;
  }

  public UPnPActionImpl[] getActionsImpl()
  {
    Enumeration e = this.actions.elements();
    if (e == null) {
      return null;
    }
    UPnPActionImpl[] uPnPacts = new UPnPActionImpl[this.actions.size()];
    int i = 0;
    while (e.hasMoreElements()) {
      uPnPacts[i] = ((UPnPActionImpl)e.nextElement());
      i++;
    }
    return uPnPacts;
  }

  public String getSCPDURL()
  {
    return this.SCPDURL;
  }

  public void setSCPDURL(String scpdurl)
  {
    this.SCPDURL = scpdurl;
  }

  public String getControlUrl()
  {
    return this.controlUrl;
  }

  public void setControlUrl(String controlURL)
  {
    this.controlUrl = controlURL;
  }

  public String getEventSubUrl()
  {
    return this.eventSubUrl;
  }

  public void setEventSubUrl(String eventSubUrl)
  {
    this.eventSubUrl = eventSubUrl;
  }

  public void addStateVariable(UPnPStateVariableImpl statVar)
  {
    this.stateVariables.put(statVar.getName(), statVar);
  }

  public UPnPStateVariableImpl[] getStateVariablesImpl()
  {
    UPnPStateVariableImpl[] vars = new UPnPStateVariableImpl[this.stateVariables.size()];
    Enumeration e = this.stateVariables.elements();
    if (e == null) {
      return null;
    }
    int i = 0;
    while (e.hasMoreElements()) {
      vars[i] = ((UPnPStateVariableImpl)e.nextElement());
      i++;
    }
    return vars;
  }

  public UPnPStateVariableImpl getStateVariable(String name)
  {
    return (UPnPStateVariableImpl)this.stateVariables.get(name);
  }
}