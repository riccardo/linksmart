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
import eu.linksmart.caf.Parameter;

/**
 * An action defined as the RHS of a {@link Rule}, as part of a {@link ContextRuleSet},
 * to be installed in the Context Manager. The Context Manager should be able to handle the
 * id of the {@link Action} given.<p>
 * {@link Attribute}s define the properties of the {@link Action}, for handling, whereas, the
 * {@link Parameter}s are expected to be particular to the processing of the {@link Action}
 * @author Michael Crouch
 *
 */
public class Action  implements java.io.Serializable {
    private eu.linksmart.caf.Attribute[] attributes;

    private java.lang.String id;

    private eu.linksmart.caf.Parameter[] parameters;

    /**
     * Constructor
     */
    public Action() {
    }

    /**
     * Constructor
     * @param attributes the {@link Attribute}s of the {@link Action}
     * @param id the id of the {@link Action}
     * @param parameters the {@link Parameter}s of the {@link Action}
     */
    public Action(
           eu.linksmart.caf.Attribute[] attributes,
           java.lang.String id,
           eu.linksmart.caf.Parameter[] parameters) {
           this.attributes = attributes;
           this.id = id;
           this.parameters = parameters;
    }


    /**
     * Gets the attributes value for this Action.
     * 
     * @return attributes
     */
    public eu.linksmart.caf.Attribute[] getAttributes() {
        return attributes;
    }


    /**
     * Sets the attributes value for this Action.
     * 
     * @param attributes
     */
    public void setAttributes(eu.linksmart.caf.Attribute[] attributes) {
        this.attributes = attributes;
    }


    /**
     * Gets the id value for this Action.
     * 
     * @return id
     */
    public java.lang.String getId() {
        return id;
    }


    /**
     * Sets the id value for this Action.
     * 
     * @param id
     */
    public void setId(java.lang.String id) {
        this.id = id;
    }


    /**
     * Gets the parameters value for this Action.
     * 
     * @return parameters
     */
    public eu.linksmart.caf.Parameter[] getParameters() {
        return parameters;
    }


    /**
     * Sets the parameters value for this Action.
     * 
     * @param parameters
     */
    public void setParameters(eu.linksmart.caf.Parameter[] parameters) {
        this.parameters = parameters;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Action)) return false;
        Action other = (Action) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.attributes==null && other.getAttributes()==null) || 
             (this.attributes!=null &&
              java.util.Arrays.equals(this.attributes, other.getAttributes()))) &&
            ((this.id==null && other.getId()==null) || 
             (this.id!=null &&
              this.id.equals(other.getId()))) &&
            ((this.parameters==null && other.getParameters()==null) || 
             (this.parameters!=null &&
              java.util.Arrays.equals(this.parameters, other.getParameters())));
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
        if (getAttributes() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getAttributes());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getAttributes(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getId() != null) {
            _hashCode += getId().hashCode();
        }
        if (getParameters() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getParameters());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getParameters(), i);
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
