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

import eu.linksmart.caf.cm.rules.DeclaredFunction;

/**
 * The {@link QuerySet} that can be installed to the Context Manager, featuring a unique package
 * name, and an array of {@link ContextQuery}s. <p>
 * Additionally, as the {@link QuerySet} gets parsed into a DRL Rule Package by the Context 
 * Manager, it provides the ability for inline {@link DeclaredFunction}s to be also specified, to
 * be used in the {@link ContextQuery}s, and also any additional imports.
 * @author Michael Crouch
 *
 */
public class QuerySet  implements java.io.Serializable {
    private eu.linksmart.caf.cm.rules.DeclaredFunction[] functions;

    private java.lang.String[] imports;

    private java.lang.String packageName;

    private eu.linksmart.caf.cm.query.ContextQuery[] queries;

    /**
     * Constructor
     */
    public QuerySet() {
    }

    /**
     * Constructor
     * @param functions the array of {@link DeclaredFunction}s
     * @param imports the imports
     * @param packageName the package name
     * @param queries the array of {@link ContextQuery}s
     */
    public QuerySet(
           eu.linksmart.caf.cm.rules.DeclaredFunction[] functions,
           java.lang.String[] imports,
           java.lang.String packageName,
           eu.linksmart.caf.cm.query.ContextQuery[] queries) {
           this.functions = functions;
           this.imports = imports;
           this.packageName = packageName;
           this.queries = queries;
    }


    /**
     * Gets the functions value for this QuerySet.
     * 
     * @return functions
     */
    public eu.linksmart.caf.cm.rules.DeclaredFunction[] getFunctions() {
        return functions;
    }


    /**
     * Sets the functions value for this QuerySet.
     * 
     * @param functions
     */
    public void setFunctions(eu.linksmart.caf.cm.rules.DeclaredFunction[] functions) {
        this.functions = functions;
    }


    /**
     * Gets the imports value for this QuerySet.
     * 
     * @return imports
     */
    public java.lang.String[] getImports() {
        return imports;
    }


    /**
     * Sets the imports value for this QuerySet.
     * 
     * @param imports
     */
    public void setImports(java.lang.String[] imports) {
        this.imports = imports;
    }


    /**
     * Gets the packageName value for this QuerySet.
     * 
     * @return packageName
     */
    public java.lang.String getPackageName() {
        return packageName;
    }


    /**
     * Sets the packageName value for this QuerySet.
     * 
     * @param packageName
     */
    public void setPackageName(java.lang.String packageName) {
        this.packageName = packageName;
    }


    /**
     * Gets the queries value for this QuerySet.
     * 
     * @return queries
     */
    public eu.linksmart.caf.cm.query.ContextQuery[] getQueries() {
        return queries;
    }


    /**
     * Sets the queries value for this QuerySet.
     * 
     * @param queries
     */
    public void setQueries(eu.linksmart.caf.cm.query.ContextQuery[] queries) {
        this.queries = queries;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof QuerySet)) return false;
        QuerySet other = (QuerySet) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.functions==null && other.getFunctions()==null) || 
             (this.functions!=null &&
              java.util.Arrays.equals(this.functions, other.getFunctions()))) &&
            ((this.imports==null && other.getImports()==null) || 
             (this.imports!=null &&
              java.util.Arrays.equals(this.imports, other.getImports()))) &&
            ((this.packageName==null && other.getPackageName()==null) || 
             (this.packageName!=null &&
              this.packageName.equals(other.getPackageName()))) &&
            ((this.queries==null && other.getQueries()==null) || 
             (this.queries!=null &&
              java.util.Arrays.equals(this.queries, other.getQueries())));
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
        if (getFunctions() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getFunctions());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getFunctions(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getImports() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getImports());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getImports(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getPackageName() != null) {
            _hashCode += getPackageName().hashCode();
        }
        if (getQueries() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getQueries());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getQueries(), i);
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
