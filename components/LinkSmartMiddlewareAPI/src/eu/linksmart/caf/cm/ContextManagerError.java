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
 * Represents an error caught in the Context Manager. <p>
 * The error contains:
 * <ol type="i">
 * <li>errorId - The id of the object causing the error</li> 
 * <li>errorSubject - The subject of the error, about where it was thrown</li> 
 * <li>errorDescription - The description of the error</li> 
 * </ol>
 * @author Michael Crouch
 *
 */
public class ContextManagerError  implements java.io.Serializable {
    private java.lang.String errorDescription;

    private java.lang.String errorId;

    private java.lang.String errorSubject;

    /**
     * Constructor
     */
    public ContextManagerError() {
    }

    /**
     * Constructor
     * @param errorDescription the description
     * @param errorId the id
     * @param errorSubject the subject
     */
    public ContextManagerError(
           java.lang.String errorDescription,
           java.lang.String errorId,
           java.lang.String errorSubject) {
           this.errorDescription = errorDescription;
           this.errorId = errorId;
           this.errorSubject = errorSubject;
    }


    /**
     * Gets the errorDescription value for this ContextManagerError.
     * 
     * @return errorDescription
     */
    public java.lang.String getErrorDescription() {
        return errorDescription;
    }


    /**
     * Sets the errorDescription value for this ContextManagerError.
     * 
     * @param errorDescription
     */
    public void setErrorDescription(java.lang.String errorDescription) {
        this.errorDescription = errorDescription;
    }


    /**
     * Gets the errorId value for this ContextManagerError.
     * 
     * @return errorId
     */
    public java.lang.String getErrorId() {
        return errorId;
    }


    /**
     * Sets the errorId value for this ContextManagerError.
     * 
     * @param errorId
     */
    public void setErrorId(java.lang.String errorId) {
        this.errorId = errorId;
    }


    /**
     * Gets the errorSubject value for this ContextManagerError.
     * 
     * @return errorSubject
     */
    public java.lang.String getErrorSubject() {
        return errorSubject;
    }


    /**
     * Sets the errorSubject value for this ContextManagerError.
     * 
     * @param errorSubject
     */
    public void setErrorSubject(java.lang.String errorSubject) {
        this.errorSubject = errorSubject;
    }


    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ContextManagerError)) return false;
        ContextManagerError other = (ContextManagerError) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.errorDescription==null && other.getErrorDescription()==null) || 
             (this.errorDescription!=null &&
              this.errorDescription.equals(other.getErrorDescription()))) &&
            ((this.errorId==null && other.getErrorId()==null) || 
             (this.errorId!=null &&
              this.errorId.equals(other.getErrorId()))) &&
            ((this.errorSubject==null && other.getErrorSubject()==null) || 
             (this.errorSubject!=null &&
              this.errorSubject.equals(other.getErrorSubject())));
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
        if (getErrorDescription() != null) {
            _hashCode += getErrorDescription().hashCode();
        }
        if (getErrorId() != null) {
            _hashCode += getErrorId().hashCode();
        }
        if (getErrorSubject() != null) {
            _hashCode += getErrorSubject().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }


}
