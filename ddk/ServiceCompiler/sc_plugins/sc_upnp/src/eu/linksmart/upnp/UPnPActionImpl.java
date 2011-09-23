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
import java.util.Hashtable;
import java.util.LinkedList;

import org.osgi.service.upnp.UPnPAction;
import org.osgi.service.upnp.UPnPStateVariable;

import eu.linksmart.upnp.ActionArgument;
import eu.linksmart.upnp.UPnPServiceImpl;

/**
 * <b>Class UPnPActionImpl</b>
 * This class represents an UPnP Action. A service can have zero or more actions.
 * Each action may have zero or more UPnP state variables as arguments.
 * This class implements the org.osgi.service.upnp.UPnPAction interface.
 *
 */
public class UPnPActionImpl
{
  private String actionName;
  private LinkedList<ActionArgument> argumentsList;
  private UPnPServiceImpl service;

  public UPnPActionImpl()
  {
    this.argumentsList = new LinkedList();
  }

  public UPnPActionImpl(String actionName, LinkedList<ActionArgument> argumentsList, UPnPServiceImpl ser)
  {
    this.actionName = actionName;
    this.argumentsList = argumentsList;
    this.service = ser;
  }

  public void setActionName(String newName)
  {
    this.actionName = newName;
  }

  public String getName()
  {
    return this.actionName;
  }

  public void addArgument(ActionArgument ac)
  {
    this.argumentsList.add(ac);
  }

  public String[] getInputArgumentNames()
  {
    String[] inputArgs = new String[this.argumentsList.size()];
    for (int i = 0; i < this.argumentsList.size(); i++) {
      if (((ActionArgument)this.argumentsList.get(i)).getDirection().equalsIgnoreCase("in"))
        inputArgs[i] = ((ActionArgument)this.argumentsList.get(i)).getArgumentName();
    }
    return inputArgs;
  }

  public String[] getOutputArgumentNames()
  {
    String[] outputArgs = new String[this.argumentsList.size()];
    for (int i = 0; i < this.argumentsList.size(); i++) {
      if (((ActionArgument)this.argumentsList.get(i)).getDirection().equalsIgnoreCase("out"))
        outputArgs[i] = ((ActionArgument)this.argumentsList.get(i)).getArgumentName();
    }
    return outputArgs;
  }

  public LinkedList<ActionArgument> getArgumentsList()
  {
    return this.argumentsList;
  }

  public LinkedList<ActionArgument> getInputArgumentList()
  {
    LinkedList inputArgumentsList = new LinkedList();

    for (int i = 0; i < this.argumentsList.size(); i++)
      if (((ActionArgument)this.argumentsList.get(i)).getDirection() == "in")
        inputArgumentsList.add((ActionArgument)this.argumentsList.get(i));
    return inputArgumentsList;
  }

  public LinkedList<ActionArgument> getOutputArgumentList()
  {
    LinkedList outputArgumentsList = new LinkedList();

    for (int i = 0; i < this.argumentsList.size(); i++)
      if (((ActionArgument)this.argumentsList.get(i)).getDirection() == "out")
        outputArgumentsList.add((ActionArgument)this.argumentsList.get(i));
    return outputArgumentsList;
  }

  public void setService(UPnPServiceImpl service)
  {
    this.service = service;
  }

  public UPnPServiceImpl getService()
  {
    return this.service;
  }

  public Dictionary invoke(Dictionary args)
    throws Exception
  {
    Dictionary result = new Hashtable();
    result.put("result", "false");
    return result;
  }

  public String getReturnArgumentName()
  {
    return null;
  }
}