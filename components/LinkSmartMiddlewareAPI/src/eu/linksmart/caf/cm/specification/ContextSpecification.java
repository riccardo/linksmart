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
package eu.linksmart.caf.cm.specification;

import eu.linksmart.caf.cm.rules.ContextRuleSet;
import eu.linksmart.caf.daqc.subscription.Subscription;

/**
 * The Context Specification defines a Context in terms of its {@link Definition},
 * a {@link ContextRuleSet}, and any {@link Subscription} that the {@link ContextSpecification}
 * may have for data.
 * @author Michael Crouch
 *
 */
public class ContextSpecification  implements java.io.Serializable {
	private eu.linksmart.caf.cm.specification.Definition definition;
	
	private eu.linksmart.caf.cm.rules.ContextRuleSet ruleSet;
    
    private String requirementsXml;
    

    /**
     * Gets the definition value for this ContextSpecification.
     * 
     * @return definition
     */
    public eu.linksmart.caf.cm.specification.Definition getDefinition() {
        return definition;
    }


    /**
     * Sets the definition value for this ContextSpecification.
     * 
     * @param definition
     */
    public void setDefinition(eu.linksmart.caf.cm.specification.Definition definition) {
        this.definition = definition;
    }


    /**
     * Gets the ruleSet value for this ContextSpecification.
     * 
     * @return ruleSet
     */
    public eu.linksmart.caf.cm.rules.ContextRuleSet getRuleSet() {
        return ruleSet;
    }


    /**
     * Sets the ruleSet value for this ContextSpecification.
     * 
     * @param ruleSet
     */
    public void setRuleSet(eu.linksmart.caf.cm.rules.ContextRuleSet ruleSet) {
        this.ruleSet = ruleSet;
    }

	public String getRequirementsXml() {
		return requirementsXml;
	}


	public void setRequirementsXml(String requirementsXml) {
		this.requirementsXml = requirementsXml;
	}
    
	private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ContextSpecification)) return false;
        ContextSpecification other = (ContextSpecification) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.definition==null && other.getDefinition()==null) || 
             (this.definition!=null &&
              this.definition.equals(other.getDefinition()))) &&
            ((this.requirementsXml==null && other.getRequirementsXml()==null) || 
             (this.requirementsXml!=null &&
              this.requirementsXml.equals(other.getRequirementsXml()))) &&
            ((this.ruleSet==null && other.getRuleSet()==null) || 
             (this.ruleSet!=null &&
              this.ruleSet.equals(other.getRuleSet())));
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
        if (getDefinition() != null) {
            _hashCode += getDefinition().hashCode();
        }
        if (getRequirementsXml() != null) {
            _hashCode += getRequirementsXml().hashCode();
        }
        if (getRuleSet() != null) {
            _hashCode += getRuleSet().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }
}
