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

/**
 * A row of query results, from the Context Manager, as part of the 
 * {@link QueryResponse}. There is one {@link QueryRow} for each output
 * defined - denoted by the rowID - in the {@link ContextQuery} the results are for. 
 * @author Michael Crouch
 *
 */
public class QueryRow  implements java.io.Serializable {
    private java.lang.String resultContent;

    private java.lang.String resultType;

    private java.lang.String rowId;

    /**
     * Constructor
     */
    public QueryRow() {
    }

    /**
     * Constructor
     * @param resultContent the encoded, String, query result
     * @param resultType the type of the result
     * @param rowId the rowId
     */
    public QueryRow(
           java.lang.String resultContent,
           java.lang.String resultType,
           java.lang.String rowId) {
           this.resultContent = resultContent;
           this.resultType = resultType;
           this.rowId = rowId;
    }


    /**
     * Gets the resultContent value for this QueryRow.
     * 
     * @return resultContent
     */
    public java.lang.String getResultContent() {
        return resultContent;
    }


    /**
     * Sets the resultContent value for this QueryRow.
     * 
     * @param resultContent
     */
    public void setResultContent(java.lang.String resultContent) {
        this.resultContent = resultContent;
    }


    /**
     * Gets the resultType value for this QueryRow.
     * 
     * @return resultType
     */
    public java.lang.String getResultType() {
        return resultType;
    }


    /**
     * Sets the resultType value for this QueryRow.
     * 
     * @param resultType
     */
    public void setResultType(java.lang.String resultType) {
        this.resultType = resultType;
    }


    /**
     * Gets the rowId value for this QueryRow.
     * 
     * @return rowId
     */
    public java.lang.String getRowId() {
        return rowId;
    }


    /**
     * Sets the rowId value for this QueryRow.
     * 
     * @param rowId
     */
    public void setRowId(java.lang.String rowId) {
        this.rowId = rowId;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof QueryRow)) return false;
        QueryRow other = (QueryRow) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.resultContent==null && other.getResultContent()==null) || 
             (this.resultContent!=null &&
              this.resultContent.equals(other.getResultContent()))) &&
            ((this.resultType==null && other.getResultType()==null) || 
             (this.resultType!=null &&
              this.resultType.equals(other.getResultType()))) &&
            ((this.rowId==null && other.getRowId()==null) || 
             (this.rowId!=null &&
              this.rowId.equals(other.getRowId())));
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
        if (getResultContent() != null) {
            _hashCode += getResultContent().hashCode();
        }
        if (getResultType() != null) {
            _hashCode += getResultType().hashCode();
        }
        if (getRowId() != null) {
            _hashCode += getRowId().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

}
