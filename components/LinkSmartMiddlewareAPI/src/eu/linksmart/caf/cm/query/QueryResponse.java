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
package eu.linksmart.caf.cm.query;

import eu.linksmart.caf.cm.ContextManagerError;

/**
 * The response to a execution of a query, from the Context Manager.<p>
 * Contains the set of results, as {@link QueryRow}s, and any {@link ContextManagerError}s thrown
 * @author Michael Crouch
 *
 */
public class QueryResponse  implements java.io.Serializable {
    private eu.linksmart.caf.cm.ContextManagerError[] errors;

    private eu.linksmart.caf.cm.query.QueryRow[] results;

    /**
     * Constructor
     */
    public QueryResponse() {
    }

    /**
     * Constructor
     * @param errors the array of {@link ContextManagerError}s
     * @param results the array of {@link QueryRow}s
     */
    public QueryResponse(
           eu.linksmart.caf.cm.ContextManagerError[] errors,
           eu.linksmart.caf.cm.query.QueryRow[] results) {
           this.errors = errors;
           this.results = results;
    }


    /**
     * Gets the errors value for this QueryResponse.
     * 
     * @return errors
     */
    public eu.linksmart.caf.cm.ContextManagerError[] getErrors() {
        return errors;
    }


    /**
     * Sets the errors value for this QueryResponse.
     * 
     * @param errors
     */
    public void setErrors(eu.linksmart.caf.cm.ContextManagerError[] errors) {
        this.errors = errors;
    }


    /**
     * Gets the results value for this QueryResponse.
     * 
     * @return results
     */
    public eu.linksmart.caf.cm.query.QueryRow[] getResults() {
        return results;
    }


    /**
     * Sets the results value for this QueryResponse.
     * 
     * @param results
     */
    public void setResults(eu.linksmart.caf.cm.query.QueryRow[] results) {
        this.results = results;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof QueryResponse)) return false;
        QueryResponse other = (QueryResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.errors==null && other.getErrors()==null) || 
             (this.errors!=null &&
              java.util.Arrays.equals(this.errors, other.getErrors()))) &&
            ((this.results==null && other.getResults()==null) || 
             (this.results!=null &&
              java.util.Arrays.equals(this.results, other.getResults())));
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
        if (getResults() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getResults());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getResults(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

}
