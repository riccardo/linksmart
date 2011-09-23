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
package eu.linksmart.caf.cm;

/**
 * Class for returning a response from the ContextManager, containing the
 * related contextId, the status, and any {@link ContextManagerError}s
 * @author Michael Crouch
 *
 */
public class ContextResponse  implements java.io.Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 7282157300944087649L;

	private java.lang.String contextId;

    private eu.linksmart.caf.cm.ContextManagerError[] errors;

    private boolean ok;

    /**
     * Constructor
     */
    public ContextResponse() {
    }

    /**
     * Constructor
     * @param contextId the contextId
     * @param errors array of {@link ContextManagerError}s
     * @param ok the status
     */
    public ContextResponse(
           java.lang.String contextId,
           eu.linksmart.caf.cm.ContextManagerError[] errors,
           boolean ok) {
           this.contextId = contextId;
           this.errors = errors;
           this.ok = ok;
    }


    /**
     * Gets the contextId value for this ContextResponse.
     * 
     * @return contextId
     */
    public java.lang.String getContextId() {
        return contextId;
    }


    /**
     * Sets the contextId value for this ContextResponse.
     * 
     * @param contextId
     */
    public void setContextId(java.lang.String contextId) {
        this.contextId = contextId;
    }


    /**
     * Gets the errors value for this ContextResponse.
     * 
     * @return errors
     */
    public eu.linksmart.caf.cm.ContextManagerError[] getErrors() {
        return errors;
    }


    /**
     * Sets the errors value for this ContextResponse.
     * 
     * @param errors
     */
    public void setErrors(eu.linksmart.caf.cm.ContextManagerError[] errors) {
        this.errors = errors;
    }


    /**
     * Gets the ok value for this ContextResponse.
     * 
     * @return ok
     */
    public boolean isOk() {
        return ok;
    }


    /**
     * Sets the ok value for this ContextResponse.
     * 
     * @param ok
     */
    public void setOk(boolean ok) {
        this.ok = ok;
    }


    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ContextResponse)) return false;
        ContextResponse other = (ContextResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.contextId==null && other.getContextId()==null) || 
             (this.contextId!=null &&
              this.contextId.equals(other.getContextId()))) &&
            ((this.errors==null && other.getErrors()==null) || 
             (this.errors!=null &&
              java.util.Arrays.equals(this.errors, other.getErrors()))) &&
            this.ok == other.isOk();
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
        if (getContextId() != null) {
            _hashCode += getContextId().hashCode();
        }
        if (getErrors() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getErrors());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getErrors(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        _hashCode += (isOk() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        __hashCodeCalc = false;
        return _hashCode;
    }


}
