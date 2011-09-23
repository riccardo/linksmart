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
 * Defines a type declared inline in the {@link ContextRuleSet}, that can then be
 * used. <p>
 * Variable members of the type are defined with {@link Attribute}s (id =  name, value = type).<p>
 * MetaAttributes of the declared type can also be provided (See DROOLS Documentatin).<p>
 * The factRole of the type declares how it should be handled - as a 'fact', or as an 'event'.
 * @author Michael Crouch
 *
 */
public class DeclaredType  implements java.io.Serializable {
    private eu.linksmart.caf.Attribute[] classMembers;

    private java.lang.String factRole;

    private eu.linksmart.caf.Attribute[] metaAttributes;

    private java.lang.String name;

    /**
     * Constructor
     */
    public DeclaredType() {
    }

    /**
     * Constructor
     * @param classMembers the member variables of the type, as {@link Attribute}s
     * @param factRole the role of the type
     * @param metaAttributes any {@link Attribute}s of the type
     * @param name the name of the type
     */
    public DeclaredType(
           eu.linksmart.caf.Attribute[] classMembers,
           java.lang.String factRole,
           eu.linksmart.caf.Attribute[] metaAttributes,
           java.lang.String name) {
           this.classMembers = classMembers;
           this.factRole = factRole;
           this.metaAttributes = metaAttributes;
           this.name = name;
    }


    /**
     * Gets the classMembers value for this DeclaredType.
     * 
     * @return classMembers
     */
    public eu.linksmart.caf.Attribute[] getClassMembers() {
        return classMembers;
    }


    /**
     * Sets the classMembers value for this DeclaredType.
     * 
     * @param classMembers
     */
    public void setClassMembers(eu.linksmart.caf.Attribute[] classMembers) {
        this.classMembers = classMembers;
    }


    /**
     * Gets the factRole value for this DeclaredType.
     * 
     * @return factRole
     */
    public java.lang.String getFactRole() {
        return factRole;
    }


    /**
     * Sets the factRole value for this DeclaredType.
     * 
     * @param factRole
     */
    public void setFactRole(java.lang.String factRole) {
        this.factRole = factRole;
    }


    /**
     * Gets the metaAttributes value for this DeclaredType.
     * 
     * @return metaAttributes
     */
    public eu.linksmart.caf.Attribute[] getMetaAttributes() {
        return metaAttributes;
    }


    /**
     * Sets the metaAttributes value for this DeclaredType.
     * 
     * @param metaAttributes
     */
    public void setMetaAttributes(eu.linksmart.caf.Attribute[] metaAttributes) {
        this.metaAttributes = metaAttributes;
    }


    /**
     * Gets the name value for this DeclaredType.
     * 
     * @return name
     */
    public java.lang.String getName() {
        return name;
    }


    /**
     * Sets the name value for this DeclaredType.
     * 
     * @param name
     */
    public void setName(java.lang.String name) {
        this.name = name;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof DeclaredType)) return false;
        DeclaredType other = (DeclaredType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.classMembers==null && other.getClassMembers()==null) || 
             (this.classMembers!=null &&
              java.util.Arrays.equals(this.classMembers, other.getClassMembers()))) &&
            ((this.factRole==null && other.getFactRole()==null) || 
             (this.factRole!=null &&
              this.factRole.equals(other.getFactRole()))) &&
            ((this.metaAttributes==null && other.getMetaAttributes()==null) || 
             (this.metaAttributes!=null &&
              java.util.Arrays.equals(this.metaAttributes, other.getMetaAttributes()))) &&
            ((this.name==null && other.getName()==null) || 
             (this.name!=null &&
              this.name.equals(other.getName())));
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
        if (getClassMembers() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getClassMembers());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getClassMembers(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getFactRole() != null) {
            _hashCode += getFactRole().hashCode();
        }
        if (getMetaAttributes() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getMetaAttributes());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getMetaAttributes(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getName() != null) {
            _hashCode += getName().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

}
