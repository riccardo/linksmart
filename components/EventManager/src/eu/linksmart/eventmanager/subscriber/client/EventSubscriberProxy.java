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
 * Copyright (C) 2006-2010 [Fraunhofer FIT]
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

package eu.linksmart.eventmanager.subscriber.client;

public class EventSubscriberProxy implements eu.linksmart.eventmanager.EventSubscriber {
  private String _endpoint = null;
  private eu.linksmart.eventmanager.EventSubscriber eventSubscriber = null;
  
  public EventSubscriberProxy() {
    _initEventSubscriberProxy();
  }
  
  public EventSubscriberProxy(String endpoint) {
    _endpoint = endpoint;
    _initEventSubscriberProxy();
  }
  
  private void _initEventSubscriberProxy() {
    try {
      eventSubscriber = (new eu.linksmart.eventmanager.subscriber.client.EventSubscriberServiceLocator()).getBasicHttpBinding_EventSubscriber();
      if (eventSubscriber != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)eventSubscriber)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)eventSubscriber)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (eventSubscriber != null)
      ((javax.xml.rpc.Stub)eventSubscriber)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public eu.linksmart.eventmanager.EventSubscriber getEventSubscriber() {
    if (eventSubscriber == null)
      _initEventSubscriberProxy();
    return eventSubscriber;
  }
  
  public java.lang.Boolean notify(java.lang.String topic, eu.linksmart.eventmanager.Part[] parts) throws java.rmi.RemoteException{
    if (eventSubscriber == null)
      _initEventSubscriberProxy();
    return eventSubscriber.notify(topic, parts);
  }
  
  
}