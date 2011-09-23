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

import eu.linksmart.caf.Attribute;

/**
 * The {@link Definition} of a {@link ContextSpecification}, including the name,
 * and a instanceOf String, providing a reference to the type of context.<p>
 * The Context {@link Definition} also contains arrays of {@link Attribute} properties,
 * and {@link Member}s.
 * @author Michael Crouch
 *
 */
public class Definition  implements java.io.Serializable {
	 private java.lang.String name;

	    private java.lang.String author;
	    private java.lang.String version;
	    private java.lang.String applicationUri;

	    private eu.linksmart.caf.cm.specification.Member[] members;

	   
	    /**
	     * Constructor
	     */
	    public Definition() {
	    }

	    /**
	     * Constructor
	     * @param name the name
	     * @param instanceOf the instanceOf 
	     * @param members array of {@link Member}s
	     * @param properties array of {@link Attribute} properties
	     */
	    public Definition(
	           java.lang.String name,
	           java.lang.String applicationUri,
	           java.lang.String author,
	           java.lang.String version,
	           eu.linksmart.caf.cm.specification.Member[] members) {
	           this.name = name;
	           this.author = author;
	           this.version = version;
	           this.applicationUri = applicationUri;
	           this.members = members;
	    }
   
	    
		/**
		 * 	Gets the name
		 *	@return the name
		 */
		public java.lang.String getName() {
			return name;
		}

		
		/**
		 * 	Sets the name
		 * 	@param name the name to set
		 */
		public void setName(java.lang.String name) {
			this.name = name;
		}

		/**
	     * Gets the Application Author
	     * @return the author
	     */
	    public java.lang.String getAuthor() {
			return author;
		}

	    /**
	     * Sets the Application author
	     * @param author the author
	     */
		public void setAuthor(java.lang.String author) {
			this.author = author;
		}

		/**
		 * Gets the version
		 * @return the version
		 */
		public java.lang.String getVersion() {
			return version;
		}

		/**
		 * Sets the version
		 * @param version the version
		 */
		public void setVersion(java.lang.String version) {
			this.version = version;
		}

		/**
		 * Gets the application uri
		 * @return the application uri
		 */
		public java.lang.String getApplicationUri() {
			return applicationUri;
		}

		/**
		 * Sets the applicationUri
		 * @param applicationUri the application uri
		 */
		public void setApplicationUri(java.lang.String applicationUri) {
			this.applicationUri = applicationUri;
		}

		/**
	     * Gets the members value for this Definition.
	     * 
	     * @return members
	     */
	    public eu.linksmart.caf.cm.specification.Member[] getMembers() {
	        return members;
	    }


	    /**
	     * Sets the members value for this Definition.
	     * 
	     * @param members
	     */
	    public void setMembers(eu.linksmart.caf.cm.specification.Member[] members) {
	        this.members = members;
	    }

	    private java.lang.Object __equalsCalc = null;
	    public synchronized boolean equals(java.lang.Object obj) {
	        if (!(obj instanceof Definition)) return false;
	        Definition other = (Definition) obj;
	        if (obj == null) return false;
	        if (this == obj) return true;
	        if (__equalsCalc != null) {
	            return (__equalsCalc == obj);
	        }
	        __equalsCalc = obj;
	        boolean _equals;
	        _equals = true && 
	            ((this.applicationUri==null && other.getApplicationUri()==null) || 
	             (this.applicationUri!=null &&
	              this.applicationUri.equals(other.getApplicationUri()))) &&
	            ((this.author==null && other.getAuthor()==null) || 
	             (this.author!=null &&
	              this.author.equals(other.getAuthor()))) &&
	            ((this.name==null && other.getName()==null) || 
	             (this.name!=null &&
	              this.name.equals(other.getName()))) &&
	            ((this.members==null && other.getMembers()==null) || 
	             (this.members!=null &&
	              java.util.Arrays.equals(this.members, other.getMembers()))) &&
	            ((this.version==null && other.getVersion()==null) || 
	             (this.version!=null &&
	              this.version.equals(other.getVersion())));
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
	        if (getApplicationUri() != null) {
	            _hashCode += getApplicationUri().hashCode();
	        }
	        if (getAuthor() != null) {
	            _hashCode += getAuthor().hashCode();
	        }
	        if (getName() != null) {
	            _hashCode += getName().hashCode();
	        }
	        if (getMembers() != null) {
	            for (int i=0;
	                 i<java.lang.reflect.Array.getLength(getMembers());
	                 i++) {
	                java.lang.Object obj = java.lang.reflect.Array.get(getMembers(), i);
	                if (obj != null &&
	                    !obj.getClass().isArray()) {
	                    _hashCode += obj.hashCode();
	                }
	            }
	        }
	        if (getVersion() != null) {
	            _hashCode += getVersion().hashCode();
	        }
	        __hashCodeCalc = false;
	        return _hashCode;
	    }
}
