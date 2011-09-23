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
/**
 * ApplicationOntologyManager.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package eu.linksmart.tuk.aom;

public interface ApplicationOntologyManager extends java.rmi.Remote {
    public java.lang.String getResourceURI(java.lang.String prefix, java.lang.String resourceName) throws java.rmi.RemoteException;
    public java.lang.String getResourceURISPARQL(java.lang.String prefix, java.lang.String resourceName) throws java.rmi.RemoteException;
    public java.lang.String answerSPARQLAsText(java.lang.String query) throws java.rmi.RemoteException;
    public java.lang.String answerSPARQLAsHTMLTable(java.lang.String query, java.lang.String styleClass, java.lang.String headerRowClass, java.lang.String headerColumnClasses, java.lang.String columnClasses, java.lang.String rowClasses) throws java.rmi.RemoteException;
    public boolean processSAWSDL(java.lang.String deviceId, java.lang.String sawsdlURI) throws java.rmi.RemoteException;
    public java.lang.String[] processLIMBOCall(java.lang.String deviceId, java.lang.String sawsdlString) throws java.rmi.RemoteException;
}
