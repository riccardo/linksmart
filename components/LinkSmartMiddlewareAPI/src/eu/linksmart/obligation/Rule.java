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
 * Rule.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package eu.linksmart.obligation;

public class Rule implements java.io.Serializable {
	private static final long serialVersionUID = 938041823316468473L;

	private java.lang.Object[] actions;

	private java.lang.String event;

	private java.lang.Object[] targets;

	public Rule() {
	}

	public Rule(java.lang.Object[] actions, java.lang.String event, java.lang.Object[] targets) {
		this.actions = actions;
		this.event = event;
		this.targets = targets;
	}

	/**
	 * Gets the actions value for this Rule.
	 * 
	 * @return actions
	 */
	public java.lang.Object[] getActions() {
		return actions;
	}

	/**
	 * Sets the actions value for this Rule.
	 * 
	 * @param actions
	 */
	public void setActions(java.lang.Object[] actions) {
		this.actions = actions;
	}

	/**
	 * Gets the event value for this Rule.
	 * 
	 * @return event
	 */
	public java.lang.String getEvent() {
		return event;
	}

	/**
	 * Sets the event value for this Rule.
	 * 
	 * @param event
	 */
	public void setEvent(java.lang.String event) {
		this.event = event;
	}

	/**
	 * Gets the targets value for this Rule.
	 * 
	 * @return targets
	 */
	public java.lang.Object[] getTargets() {
		return targets;
	}

	/**
	 * Sets the targets value for this Rule.
	 * 
	 * @param targets
	 */
	public void setTargets(java.lang.Object[] targets) {
		this.targets = targets;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof Rule))
			return false;
		Rule other = (Rule) obj;
		if (this == obj)
			return true;
		if (__equalsCalc != null) {
			return (__equalsCalc == obj);
		}
		__equalsCalc = obj;
		boolean _equals;
		_equals = true
				&& ((this.actions == null && other.getActions() == null) || (this.actions != null && java.util.Arrays.equals(this.actions,
						other.getActions())))
				&& ((this.event == null && other.getEvent() == null) || (this.event != null && this.event.equals(other.getEvent())))
				&& ((this.targets == null && other.getTargets() == null) || (this.targets != null && java.util.Arrays.equals(this.targets,
						other.getTargets())));
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
			for (int i = 0; i < java.lang.reflect.Array.getLength(getActions()); i++) {
				java.lang.Object obj = java.lang.reflect.Array.get(getActions(), i);
				if (obj != null && !obj.getClass().isArray()) {
					_hashCode += obj.hashCode();
				}
			}
		}
		if (getEvent() != null) {
			_hashCode += getEvent().hashCode();
		}
		if (getTargets() != null) {
			for (int i = 0; i < java.lang.reflect.Array.getLength(getTargets()); i++) {
				java.lang.Object obj = java.lang.reflect.Array.get(getTargets(), i);
				if (obj != null && !obj.getClass().isArray()) {
					_hashCode += obj.hashCode();
				}
			}
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	
}
