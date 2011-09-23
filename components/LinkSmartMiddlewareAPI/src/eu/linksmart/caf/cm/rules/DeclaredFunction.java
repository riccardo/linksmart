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

import eu.linksmart.caf.Attribute;

/**
 * Defines a function declared inline, as part of the {@link ContextRuleSet}, that
 * can be called.<p>
 * The arguments to the function are defined as {@link Attribute} (id = type, value = variable name).
 * @author Michael Crouch
 *
 */
public class DeclaredFunction  implements java.io.Serializable {
    private eu.linksmart.caf.Attribute[] arguments;

    private java.lang.String code;

    private java.lang.String name;

    private java.lang.String returnType;

    /**
     * Constructor
     */
    public DeclaredFunction() {
    }

    /**
     * Constructor
     * @param arguments the {@link Attribute} arguments for the function
     * @param code the function code
     * @param name the name of the function
     * @param returnType the return type of the function - can be qualified
     */
    public DeclaredFunction(
           eu.linksmart.caf.Attribute[] arguments,
           java.lang.String code,
           java.lang.String name,
           java.lang.String returnType) {
           this.arguments = arguments;
           this.code = code;
           this.name = name;
           this.returnType = returnType;
    }


    /**
     * Gets the arguments value for this DeclaredFunction.
     * 
     * @return arguments
     */
    public eu.linksmart.caf.Attribute[] getArguments() {
        return arguments;
    }


    /**
     * Sets the arguments value for this DeclaredFunction.
     * 
     * @param arguments
     */
    public void setArguments(eu.linksmart.caf.Attribute[] arguments) {
        this.arguments = arguments;
    }


    /**
     * Gets the code value for this DeclaredFunction.
     * 
     * @return code
     */
    public java.lang.String getCode() {
        return code;
    }


    /**
     * Sets the code value for this DeclaredFunction.
     * 
     * @param code
     */
    public void setCode(java.lang.String code) {
        this.code = code;
    }


    /**
     * Gets the name value for this DeclaredFunction.
     * 
     * @return name
     */
    public java.lang.String getName() {
        return name;
    }


    /**
     * Sets the name value for this DeclaredFunction.
     * 
     * @param name
     */
    public void setName(java.lang.String name) {
        this.name = name;
    }


    /**
     * Gets the returnType value for this DeclaredFunction.
     * 
     * @return returnType
     */
    public java.lang.String getReturnType() {
        return returnType;
    }


    /**
     * Sets the returnType value for this DeclaredFunction.
     * 
     * @param returnType
     */
    public void setReturnType(java.lang.String returnType) {
        this.returnType = returnType;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof DeclaredFunction)) return false;
        DeclaredFunction other = (DeclaredFunction) obj;
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
            ((this.code==null && other.getCode()==null) || 
             (this.code!=null &&
              this.code.equals(other.getCode()))) &&
            ((this.name==null && other.getName()==null) || 
             (this.name!=null &&
              this.name.equals(other.getName()))) &&
            ((this.returnType==null && other.getReturnType()==null) || 
             (this.returnType!=null &&
              this.returnType.equals(other.getReturnType())));
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
        if (getCode() != null) {
            _hashCode += getCode().hashCode();
        }
        if (getName() != null) {
            _hashCode += getName().hashCode();
        }
        if (getReturnType() != null) {
            _hashCode += getReturnType().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

}
