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
package eu.linksmart.policy.pdp.ext.function.impl;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.sun.xacml.EvaluationCtx;
import com.sun.xacml.ParsingException;
import com.sun.xacml.UnknownIdentifierException;
import com.sun.xacml.attr.AttributeFactory;
import com.sun.xacml.attr.AttributeValue;
import com.sun.xacml.attr.BagAttribute;
import com.sun.xacml.attr.IntegerAttribute;
import com.sun.xacml.attr.StringAttribute;
import com.sun.xacml.cond.EvaluationResult;
import com.sun.xacml.cond.Function;
import com.sun.xacml.cond.FunctionBase;
import com.sun.xacml.ctx.Status;

import eu.linksmart.policy.pdp.impl.LinkSmartAttributeFactory;
import eu.linksmart.policy.pdp.impl.PdpXacmlConstants;

/**
 * <p>This function takes some XML content and an XPath query and returns a 
 * bag of all matching nodes encoded as {@link StringAttribute}s</p> 
 * 
 * @author Michael Crouch
 * @author Marco Tiemann
 * 
 */
public class XPathFunctions extends FunctionBase {
		
	/** get node */
	private static final String GET_NODE = "-xpath-value";
	
	/** get node bag */
	private static final String GET_NODE_BAG = "-xpath-value-bag"; 

	/*
	 * The parameter types, in order, and whether or not they're bags
	 * 
	 * Parameters:
	 * 		1. XML Content
	 * 		2. XPath Query
	 */
	
	/** parameter types, "in order" (sic) */
    private static final String PARAMS[] = { StringAttribute.identifier,
                                              StringAttribute.identifier};
    
    /** parameter bag flags */
    private static final boolean BAG_PARAMS[] = { false, false };

    /** {@link AttributeFactory} */
    private final AttributeFactory attributeFactory 
    		= new LinkSmartAttributeFactory();
    
    /** flag indicating whether a bag is returned */
    private final boolean returnsBag;
    
    /** return <code>URI</code> */
    private URI returnURI = null;
	
    /**
     * Constructor
     * 
     * @param theIdentifier
     * 				the identifier
     * @param theFunctionName
     * 				the function name
     * @param theReturnsBag
     * 				flag indicating whether a bag is returned
     */
	public XPathFunctions(String theIdentifier, String theFunctionName, 
			boolean theReturnsBag) {
		super(theFunctionName, 0, PARAMS, BAG_PARAMS, theIdentifier, 
				theReturnsBag);
		returnURI = URI.create(theIdentifier);
		returnsBag = theReturnsBag;		
	}

	/* (non-Javadoc)
	 * @see com.sun.xacml.cond.Function#evaluate(java.util.List, 
	 * 		com.sun.xacml.EvaluationCtx)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public EvaluationResult evaluate(List theParams, EvaluationCtx theCtx) {
		AttributeValue[] argValues = new AttributeValue[theParams.size()];
		EvaluationResult result = evalArgs(theParams, theCtx, argValues);
		if (result != null) {
            return result;
		}
		String xml = ((StringAttribute) argValues[0]).getValue();
		String query = ((StringAttribute) argValues[1]).getValue();		
		XPathFactory xpfactory = XPathFactory.newInstance();
		XPath xpath = xpfactory.newXPath();
		NodeList nodes = null;
		try {
			XPathExpression expr = xpath.compile(query);
			InputSource src = new InputSource(new ByteArrayInputStream(
					xml.getBytes()));
			nodes = (NodeList) expr.evaluate(src, XPathConstants.NODESET);
		}
		catch (Exception e) {
			return getError(Status.STATUS_PROCESSING_ERROR, 
					"Exception processing XPath Query - " 
							+ e.getLocalizedMessage());
		}
		if (returnsBag) {
			// if the bag function			
			Set attrs = new HashSet();
			int nl = nodes.getLength();
			for (int i = 0; i < nl; i++) {
				String value = nodes.item(i).getNodeValue();
				if (returnURI.toString().equals(IntegerAttribute.identifier)) {
					value = chopFloatingPoint(value);
				}
				try {
					attrs.add(attributeFactory.createValue(returnURI, value));
				} catch (UnknownIdentifierException e) {
					return getError(Status.STATUS_PROCESSING_ERROR, 
							e.getLocalizedMessage());
				} catch (ParsingException e) {
					return getError(Status.STATUS_PROCESSING_ERROR, 
							e.getLocalizedMessage());
				}
			}
			if (attrs.isEmpty()) {
				return new EvaluationResult(
						BagAttribute.createEmptyBag(returnURI));
			}
			BagAttribute bag = new BagAttribute(returnURI, attrs);
			return new EvaluationResult(bag);
		}
		// return single node (first result, if any)
		if (nodes.getLength() == 0) {
			return new EvaluationResult(BagAttribute.createEmptyBag(returnURI));
		}		
		String value = nodes.item(0).getNodeValue();
		if (returnURI.toString().equals(IntegerAttribute.identifier)) {
			value = chopFloatingPoint(value);
		}
		try {
			AttributeValue attr = attributeFactory.createValue(returnURI, value);
			return new EvaluationResult(attr);
		} catch (UnknownIdentifierException uie) {
				return getError(Status.STATUS_PROCESSING_ERROR, 
						uie.getLocalizedMessage());
			} catch (ParsingException pe) {
				return getError(Status.STATUS_PROCESSING_ERROR, 
						pe.getLocalizedMessage());
			}	
	}
	
	/**
	 * @param identifier
	 * 				the function identifier
	 * @return
	 * 				the {@link Function}
	 */
	public static Function getSingleFunction(String identifier) {
		return new XPathFunctions(identifier, 
				PdpXacmlConstants.FUNCTION_LINK_SMART_PREFIX.getUrn() 
						+ extractName(identifier) + GET_NODE, false);
	}

	/**
	 * @param identifier
	 * 				the function identifier
	 * @return
	 * 				the {@link Function}
	 */
	public static Function getBagFunction(String identifier) {
		return new XPathFunctions(identifier, 
				PdpXacmlConstants.FUNCTION_LINK_SMART_PREFIX.getUrn() 
						+ extractName(identifier) + GET_NODE_BAG, true);
	}

	/**
	 * @param theCode
	 * 				the status code
	 * @param theMsg
	 * 				the message
	 * @return
	 * 				the {@link EvaluationResult}
	 */
	private static EvaluationResult getError(String theCode, String theMsg) {
		ArrayList<String> code = new ArrayList<String>();
        code.add(theCode);
        Status status = new Status(code, theMsg);
        return new EvaluationResult(status);
	}
	
	/**
	 * @param theIdentifier
	 * 				the identifier
	 * @return
	 * 				the function name
	 */
	private static String extractName(String theIdentifier) {		
		// get last # or :
		int lastHash = theIdentifier.lastIndexOf('#');
		int lastColon = theIdentifier.lastIndexOf(':');
		int index = lastColon;
		if (lastHash > lastColon) {
			index = lastHash;
		}
		return theIdentifier.substring(index + 1);
	}

	/**
	 * @param theLine
	 * 				the input <code>String</code>
	 * @return
	 * 				the "chopped" (sic) <code>String</code>
	 */
	private static String chopFloatingPoint(String theLine) {
		return (theLine.contains(".")) 
			? theLine.substring(0, theLine.indexOf('.')) : theLine;		
	}
	
}
