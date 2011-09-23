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
package eu.linksmart.caf.daqc.subscription;

import eu.linksmart.caf.daqc.DataAcquisitionComponent;

/**
 * Represents the Result / Status of a {@link Subscription} to the {@link DataAcquisitionComponent}.<p>
 * Contains the id (data Id) of the {@link Subscription}, along with a message about the status,
 * and a boolean status of the success of the {@link Subscription}.
 * @author Michael Crouch
 *
 */
public class Result  implements java.io.Serializable {
    private java.lang.String id;

    private java.lang.String msg;

    private boolean success;

    /**
     * Constructor
     */
    public Result() {
    }

    /**
     * Constructor
     * @param id the id
     * @param msg the msg
     * @param success the success
     */
    public Result(
           java.lang.String id,
           java.lang.String msg,
           boolean success) {
           this.id = id;
           this.msg = msg;
           this.success = success;
    }


    /**
     * Gets the id value for this Result.
     * 
     * @return id
     */
    public java.lang.String getId() {
        return id;
    }


    /**
     * Sets the id value for this Result.
     * 
     * @param id
     */
    public void setId(java.lang.String id) {
        this.id = id;
    }


    /**
     * Gets the msg value for this Result.
     * 
     * @return msg
     */
    public java.lang.String getMsg() {
        return msg;
    }


    /**
     * Sets the msg value for this Result.
     * 
     * @param msg
     */
    public void setMsg(java.lang.String msg) {
        this.msg = msg;
    }


    /**
     * Gets the success value for this Result.
     * 
     * @return success
     */
    public boolean isSuccess() {
        return success;
    }


    /**
     * Sets the success value for this Result.
     * 
     * @param success
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Result)) return false;
        Result other = (Result) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.id==null && other.getId()==null) || 
             (this.id!=null &&
              this.id.equals(other.getId()))) &&
            ((this.msg==null && other.getMsg()==null) || 
             (this.msg!=null &&
              this.msg.equals(other.getMsg()))) &&
            this.success == other.isSuccess();
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getId() != null) {
            _hashCode += getId().hashCode();
        }
        if (getMsg() != null) {
            _hashCode += getMsg().hashCode();
        }
        _hashCode += (isSuccess() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        __hashCodeCalc = false;
        return _hashCode;
    }

}
