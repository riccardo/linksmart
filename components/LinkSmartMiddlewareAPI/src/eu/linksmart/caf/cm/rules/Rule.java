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
 * Represents a Rule from the {@link ContextRuleSet}. The LHS is provided as DRL code, as 
 * the "whenClause". The ruleId is the name of the rule, and must be unique inside the 
 * {@link ContextRuleSet}.<p>
 * The RHS of the {@link Rule} is provided as an array of {@link Action}s, to be interpreted
 * by the Context Manager.<p>
 * Rule Attributes can be provided as well, as defined by DROOLS, that dictate how the Rule Engine
 * handles the rule.
 * @author Michael Crouch
 *
 */
public class Rule  implements java.io.Serializable {
    private eu.linksmart.caf.cm.rules.Action[] actions;

    private eu.linksmart.caf.Attribute[] ruleAttributes;

    private java.lang.String ruleId;

    private java.lang.String whenClause;

    /**
     * Constrcutor
     */
    public Rule() {
    }

    /**
     * Constructor
     * @param actions array of {@link Action}s
     * @param ruleAttributes array of Rule {@link Attribute}s
     * @param ruleId the id of the {@link Rule}
     * @param whenClause the LHS DROOLS WHEN clauses
     */
    public Rule(
           eu.linksmart.caf.cm.rules.Action[] actions,
           eu.linksmart.caf.Attribute[] ruleAttributes,
           java.lang.String ruleId,
           java.lang.String whenClause) {
           this.actions = actions;
           this.ruleAttributes = ruleAttributes;
           this.ruleId = ruleId;
           this.whenClause = whenClause;
    }


    /**
     * Gets the actions value for this Rule.
     * 
     * @return actions
     */
    public eu.linksmart.caf.cm.rules.Action[] getActions() {
        return actions;
    }


    /**
     * Sets the actions value for this Rule.
     * 
     * @param actions
     */
    public void setActions(eu.linksmart.caf.cm.rules.Action[] actions) {
        this.actions = actions;
    }


    /**
     * Gets the ruleAttributes value for this Rule.
     * 
     * @return ruleAttributes
     */
    public eu.linksmart.caf.Attribute[] getRuleAttributes() {
        return ruleAttributes;
    }


    /**
     * Sets the ruleAttributes value for this Rule.
     * 
     * @param ruleAttributes
     */
    public void setRuleAttributes(eu.linksmart.caf.Attribute[] ruleAttributes) {
        this.ruleAttributes = ruleAttributes;
    }


    /**
     * Gets the ruleId value for this Rule.
     * 
     * @return ruleId
     */
    public java.lang.String getRuleId() {
        return ruleId;
    }


    /**
     * Sets the ruleId value for this Rule.
     * 
     * @param ruleId
     */
    public void setRuleId(java.lang.String ruleId) {
        this.ruleId = ruleId;
    }


    /**
     * Gets the whenClause value for this Rule.
     * 
     * @return whenClause
     */
    public java.lang.String getWhenClause() {
        return whenClause;
    }


    /**
     * Sets the whenClause value for this Rule.
     * 
     * @param whenClause
     */
    public void setWhenClause(java.lang.String whenClause) {
        this.whenClause = whenClause;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Rule)) return false;
        Rule other = (Rule) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.actions==null && other.getActions()==null) || 
             (this.actions!=null &&
              java.util.Arrays.equals(this.actions, other.getActions()))) &&
            ((this.ruleAttributes==null && other.getRuleAttributes()==null) || 
             (this.ruleAttributes!=null &&
              java.util.Arrays.equals(this.ruleAttributes, other.getRuleAttributes()))) &&
            ((this.ruleId==null && other.getRuleId()==null) || 
             (this.ruleId!=null &&
              this.ruleId.equals(other.getRuleId()))) &&
            ((this.whenClause==null && other.getWhenClause()==null) || 
             (this.whenClause!=null &&
              this.whenClause.equals(other.getWhenClause())));
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
        if (getActions() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getActions());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getActions(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getRuleAttributes() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getRuleAttributes());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getRuleAttributes(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getRuleId() != null) {
            _hashCode += getRuleId().hashCode();
        }
        if (getWhenClause() != null) {
            _hashCode += getWhenClause().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

}
