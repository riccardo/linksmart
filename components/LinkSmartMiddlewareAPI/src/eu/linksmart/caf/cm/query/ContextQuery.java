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

import eu.linksmart.caf.Attribute;

/**
 * A named query to install in the Context Manager. Is identified by the name,
 * which must be unique in the Context Manager. 
 * 
 * Contains:
 * <ol type="i">
 * <li>The name</li>
 * <li>Array of {@link Attribute}s used to defined the arguments 
 * as input to the query (id = type, value = name)</li>
 * <li>Array of {@link Attribute} defining the output of the query (id = variable name, value = type)</li>
 * <li>The DRL Query code</li>
 * </ol>
 * @author Michael Crouch
 *
 */
public class ContextQuery  implements java.io.Serializable {
    private eu.linksmart.caf.Attribute[] arguments;

    private java.lang.String name;

    private eu.linksmart.caf.Attribute[] output;

    private java.lang.String query;

    /**
     * Constructor
     */
    public ContextQuery() {
    }

    /**
     * Constructor 
     * @param arguments array of {@link Attribute} arguments
     * @param name the name of the Query
     * @param output array of {@link Attribute} outputs
     * @param query the query
     */
    public ContextQuery(
           eu.linksmart.caf.Attribute[] arguments,
           java.lang.String name,
           eu.linksmart.caf.Attribute[] output,
           java.lang.String query) {
           this.arguments = arguments;
           this.name = name;
           this.output = output;
           this.query = query;
    }


    /**
     * Gets the arguments value for this ContextQuery.
     * 
     * @return arguments
     */
    public eu.linksmart.caf.Attribute[] getArguments() {
        return arguments;
    }


    /**
     * Sets the arguments value for this ContextQuery.
     * 
     * @param arguments
     */
    public void setArguments(eu.linksmart.caf.Attribute[] arguments) {
        this.arguments = arguments;
    }


    /**
     * Gets the name value for this ContextQuery.
     * 
     * @return name
     */
    public java.lang.String getName() {
        return name;
    }


    /**
     * Sets the name value for this ContextQuery.
     * 
     * @param name
     */
    public void setName(java.lang.String name) {
        this.name = name;
    }


    /**
     * Gets the output value for this ContextQuery.
     * 
     * @return output
     */
    public eu.linksmart.caf.Attribute[] getOutput() {
        return output;
    }


    /**
     * Sets the output value for this ContextQuery.
     * 
     * @param output
     */
    public void setOutput(eu.linksmart.caf.Attribute[] output) {
        this.output = output;
    }


    /**
     * Gets the query value for this ContextQuery.
     * 
     * @return query
     */
    public java.lang.String getQuery() {
        return query;
    }


    /**
     * Sets the query value for this ContextQuery.
     * 
     * @param query
     */
    public void setQuery(java.lang.String query) {
        this.query = query;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ContextQuery)) return false;
        ContextQuery other = (ContextQuery) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.arguments==null && other.getArguments()==null) || 
             (this.arguments!=null &&
              java.util.Arrays.equals(this.arguments, other.getArguments()))) &&
            ((this.name==null && other.getName()==null) || 
             (this.name!=null &&
              this.name.equals(other.getName()))) &&
            ((this.output==null && other.getOutput()==null) || 
             (this.output!=null &&
              java.util.Arrays.equals(this.output, other.getOutput()))) &&
            ((this.query==null && other.getQuery()==null) || 
             (this.query!=null &&
              this.query.equals(other.getQuery())));
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
        if (getArguments() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getArguments());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getArguments(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getName() != null) {
            _hashCode += getName().hashCode();
        }
        if (getOutput() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getOutput());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getOutput(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getQuery() != null) {
            _hashCode += getQuery().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }


}
