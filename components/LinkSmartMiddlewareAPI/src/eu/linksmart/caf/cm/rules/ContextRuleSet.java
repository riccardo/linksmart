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
package eu.linksmart.caf.cm.rules;

import eu.linksmart.caf.cm.specification.ContextSpecification;

/**
 * Contains the contents of the Rule Package to be created, includeds {@link Rule}s,
 * any inline {@link DeclaredFunction}s and {@link DeclaredType}s, and also any imports
 * for the {@link Rule}s. <p>
 * The associated {@link ContextSpecification} contextId, is used as the package name, when 
 * parsing to DRL.
 * @author Michael Crouch
 *
 */
public class ContextRuleSet  implements java.io.Serializable {
    private eu.linksmart.caf.cm.rules.DeclaredFunction[] functions;

    private java.lang.String[] imports;

    private eu.linksmart.caf.cm.rules.Rule[] rules;

    private eu.linksmart.caf.cm.rules.DeclaredType[] types;

    /**
     * Constructor
     */
    public ContextRuleSet() {
    }

    /**
     * Constructor
     * @param functions the array of {@link DeclaredFunction}s
     * @param imports the imports
     * @param rules the {@link Rule}s
     * @param types the {@link DeclaredType}s
     */
    public ContextRuleSet(
           eu.linksmart.caf.cm.rules.DeclaredFunction[] functions,
           java.lang.String[] imports,
           eu.linksmart.caf.cm.rules.Rule[] rules,
           eu.linksmart.caf.cm.rules.DeclaredType[] types) {
           this.functions = functions;
           this.imports = imports;
           this.rules = rules;
           this.types = types;
    }


    /**
     * Gets the functions value for this ContextRuleSet.
     * 
     * @return functions
     */
    public eu.linksmart.caf.cm.rules.DeclaredFunction[] getFunctions() {
        return functions;
    }


    /**
     * Sets the functions value for this ContextRuleSet.
     * 
     * @param functions
     */
    public void setFunctions(eu.linksmart.caf.cm.rules.DeclaredFunction[] functions) {
        this.functions = functions;
    }


    /**
     * Gets the imports value for this ContextRuleSet.
     * 
     * @return imports
     */
    public java.lang.String[] getImports() {
        return imports;
    }


    /**
     * Sets the imports value for this ContextRuleSet.
     * 
     * @param imports
     */
    public void setImports(java.lang.String[] imports) {
        this.imports = imports;
    }


    /**
     * Gets the rules value for this ContextRuleSet.
     * 
     * @return rules
     */
    public eu.linksmart.caf.cm.rules.Rule[] getRules() {
        return rules;
    }


    /**
     * Sets the rules value for this ContextRuleSet.
     * 
     * @param rules
     */
    public void setRules(eu.linksmart.caf.cm.rules.Rule[] rules) {
        this.rules = rules;
    }


    /**
     * Gets the types value for this ContextRuleSet.
     * 
     * @return types
     */
    public eu.linksmart.caf.cm.rules.DeclaredType[] getTypes() {
        return types;
    }


    /**
     * Sets the types value for this ContextRuleSet.
     * 
     * @param types
     */
    public void setTypes(eu.linksmart.caf.cm.rules.DeclaredType[] types) {
        this.types = types;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ContextRuleSet)) return false;
        ContextRuleSet other = (ContextRuleSet) obj;
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
            ((this.rules==null && other.getRules()==null) || 
             (this.rules!=null &&
              java.util.Arrays.equals(this.rules, other.getRules()))) &&
            ((this.types==null && other.getTypes()==null) || 
             (this.types!=null &&
              java.util.Arrays.equals(this.types, other.getTypes())));
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
        if (getRules() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getRules());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getRules(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getTypes() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getTypes());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getTypes(), i);
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
