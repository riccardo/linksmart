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
package eu.linksmart.policy.pdp.impl;

import java.util.Map;
import java.util.Map.Entry;

import org.w3c.dom.Node;

import com.sun.xacml.ParsingException;
import com.sun.xacml.UnknownIdentifierException;
import com.sun.xacml.attr.AttributeProxy;
import com.sun.xacml.attr.AttributeValue;
import com.sun.xacml.attr.BaseAttributeFactory;
import com.sun.xacml.attr.StandardAttributeFactory;
import com.sun.xacml.attr.proxy.AnyURIAttributeProxy;
import com.sun.xacml.attr.proxy.Base64BinaryAttributeProxy;
import com.sun.xacml.attr.proxy.BooleanAttributeProxy;
import com.sun.xacml.attr.proxy.DateAttributeProxy;
import com.sun.xacml.attr.proxy.DateTimeAttributeProxy;
import com.sun.xacml.attr.proxy.DayTimeDurationAttributeProxy;
import com.sun.xacml.attr.proxy.DoubleAttributeProxy;
import com.sun.xacml.attr.proxy.HexBinaryAttributeProxy;
import com.sun.xacml.attr.proxy.IntegerAttributeProxy;
import com.sun.xacml.attr.proxy.RFC822NameAttributeProxy;
import com.sun.xacml.attr.proxy.StringAttributeProxy;
import com.sun.xacml.attr.proxy.TimeAttributeProxy;
import com.sun.xacml.attr.proxy.X500NameAttributeProxy;
import com.sun.xacml.attr.proxy.YearMonthDurationAttributeProxy;

/**
 * <p>LinkSmart {@link AttributeFactory} implementation</p>
 * 
 * <p>This implementation extends {@link BaseAttributeFactory} and can be 
 * extended with additional {@link AttributeProxy}s. Note that exceptions are 
 * handled slightly different compared to the Sun {@link BaseAttributeFactory} 
 * in that exceptions are thrown in fewer conditions. Instead, instances 
 * using this implementation must handle returned <code>null</code>.</p>
 * 
 * @author Marco Tiemann
 *
 */
public class LinkSmartAttributeFactory extends BaseAttributeFactory {

	/** No-args constructor */
	@SuppressWarnings("unchecked")
	public LinkSmartAttributeFactory() {
		super();
		Map<String, AttributeProxy> standardDataTypes 
				= StandardAttributeFactory.getFactory().getStandardDatatypes();
		for (Entry<String, AttributeProxy> ntr : standardDataTypes.entrySet()) {
			addDatatype(ntr.getKey(), ntr.getValue());
		}
		// some of this will be redundant
		addDatatype("anyURI", new AnyURIAttributeProxy());
		addDatatype("base64Binary", new Base64BinaryAttributeProxy());
		addDatatype("boolean", new BooleanAttributeProxy());
		addDatatype("date", new DateAttributeProxy());
		addDatatype("dateTime", new DateTimeAttributeProxy());
		addDatatype("dayTimeDuration", new DayTimeDurationAttributeProxy());
		addDatatype("double", new DoubleAttributeProxy());
		addDatatype("hexBinary", new HexBinaryAttributeProxy());
		addDatatype("decimal", new IntegerAttributeProxy());
		addDatatype("email", new RFC822NameAttributeProxy());
		addDatatype("string", new StringAttributeProxy());
		addDatatype("time", new TimeAttributeProxy());
		addDatatype("x500", new X500NameAttributeProxy());
		addDatatype("yearMonthDuration", new YearMonthDurationAttributeProxy());
	}

	/* (non-Javadoc)
	 * @see com.sun.xacml.attr.BaseAttributeFactory#createValue(
	 * 		org.w3c.dom.Node)
	 */
	@Override
	public AttributeValue createValue(Node theNode)
			throws UnknownIdentifierException, ParsingException {
		if (theNode == null) {
			return null;
		}
		return super.createValue(theNode);
	}
	
}
