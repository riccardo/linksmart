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
 * Copyright (C) 2006-2010 [Fraunhofer SIT, Julian Schuette] the HYDRA
 * consortium, EU project IST-2005-034891
 * 
 * This file is part of LinkSmart.
 * 
 * LinkSmart is free software: you can redistribute it and/or modify it under
 * the terms of the GNU LESSER GENERAL PUBLIC LICENSE version 3 as published by
 * the Free Software Foundation.
 * 
 * LinkSmart is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with LinkSmart. If not, see <http://www.gnu.org/licenses/>.
 */

package eu.linksmart.policy.pip.ontology;

import java.io.StringReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import eu.linksmart.aom.ApplicationOntologyManager;
import com.sun.xacml.EvaluationCtx;
import com.sun.xacml.attr.AttributeValue;
import com.sun.xacml.attr.BagAttribute;
import com.sun.xacml.attr.BooleanAttribute;
import com.sun.xacml.attr.StringAttribute;
import com.sun.xacml.cond.EvaluationResult;
import com.sun.xacml.cond.FunctionBase;

/**
 * Implementation of the <code>sem:satisfiesQuery</code> function. <p/> This
 * XACML custom function takes two parameters:<br> 1) A
 * <code>StringAttribute</code> representing a query that is understood by the
 * ontology manager.<br> 2) A <code>BagAttribute</code> of
 * <code>StringAttribute</code>s, representing URIs of instances in an ontology.
 * <p/> This custom function executes the query and returns <code>true</code> if
 * at least one of the URIs provided in parameter (2) is contained in the list
 * of individuals returned by the query. <p/> An example condition using this
 * function looks like this:<br> <pre> &lt;!-- Requesting instance supports a
 * security protocol that has an assertion until 2010 (at least) --&gt;
 * &lt;Apply
 * FunctionId=&quot;urn:oasis:names:tc:xacml:1.0:function:sem:satisfiesQuery
 * &quot;&gt; &lt;AttributeValue
 * DataType=&quot;http://www.w3.org/2001/XMLSchema#
 * string&quot;&gt;evice:hasService/service:capability;service:playsVideo,
 * device:hasHardware/hardware:hasDisplay&lt;/AttributeValue&gt;
 * &lt;SubjectAttributeDesignator
 * DataType=&quot;http://www.w3.org/2001/XMLSchema#string&quot;
 * AttributeId=&quot;linksmart:policy:subject:pid&quot; /&gt;
 * &lt;/Apply&gt; </pre>
 * 
 * @author Julian Schuette
 */
public class QueryFunction extends FunctionBase {

	private static final Logger logger = Logger.getLogger(QueryFunction.class);
	// Full URL of the rdf:type relation
	public static final String rdfType =
			"http://www.w3.org/1999/02/22-rdf-syntax-ns#type";

	/**
	 * Identifier for this function.
	 */
	public static final String SATISFIES_DL_QUERY_FUNCTION =
			FUNCTION_NS + "sem:satisfiesQuery";

	// the parameter types, in order, and whether or not they're bags
	private static final String params[] =
			{ StringAttribute.identifier, StringAttribute.identifier };
	private static final boolean bagParams[] = { false, true };

	private static final String returnType = BooleanAttribute.identifier;

	// private mapping of standard functions to their argument types
	private static HashMap typeMap;

	/**
	 * Static initializer sets up a map of standard function names to their
	 * associated datatypes
	 */
	static {
		typeMap = new HashMap();
		typeMap.put(SATISFIES_DL_QUERY_FUNCTION, StringAttribute.identifier);
	}

	private ApplicationOntologyManager ontManager;

	public QueryFunction(String functionName) {
		this(functionName, getArgumentType(functionName));
	}

	public QueryFunction(String functionName, String argumentType) {
		super(functionName, 0, params, bagParams, returnType, false);
	}

	/**
	 * Private helper that returns the type used for the given standard
	 * type-equal function.
	 */
	private static String getArgumentType(String functionName) {
		String datatype = (String) (typeMap.get(functionName));

		if (datatype == null)
			throw new IllegalArgumentException("not a standard function: "
					+ functionName);

		logger.debug("GETARGUMENTTYPE CALLED. " + datatype);
		return datatype;
	}

	/**
	 * Returns a <code>Set</code> containing all the function identifiers
	 * supported by this class.
	 * 
	 * @return a <code>Set</code> of <code>String</code>s
	 */
	public static Set getSupportedIdentifiers() {
		return Collections.unmodifiableSet(typeMap.keySet());
	}

	/**
	 * Evaluate the function, using specified parameters.
	 * 
	 * @param inputs
	 *            a <code>List</code> of <code>Evaluatable</code> objects
	 *            representing the arguments passed to the function
	 * @param context
	 *            an <code>EvaluationCtx</code> so that the
	 *            <code>Evaluatable</code> objects can be evaluated
	 * @return an <code>EvaluationResult</code> representing the function's
	 *         result
	 */
	public EvaluationResult evaluate(List inputs, EvaluationCtx context) {
		logger.debug(SATISFIES_DL_QUERY_FUNCTION + ".evaluate called");
		long start = System.currentTimeMillis();
		boolean isContained = false;

		// Evaluate the arguments
		AttributeValue[] argValues = new AttributeValue[inputs.size()];
		EvaluationResult result = evalArgs(inputs, context, argValues);

		// Note: if no error: result==null
		if (result != null)
			return result;

		// If no subjects have been specified, return false.
		if (argValues.length == 0) {
			return EvaluationResult.getInstance(false);
		}

		/*
		 * A query will look like this:
		 * device:hasService/service:capability;service:playsVideo,
		 * device:hasHardware/hardware:hasDisplay
		 */
		// Retrieve the query from XACML policy
		String query = ((StringAttribute) argValues[0]).getValue();

		// Retrieve device instance from XACML policy (there should be only one,
		// we remember the last one)
		Iterator pids = ((BagAttribute) argValues[1]).iterator();
		String instance = null;
		while (pids.hasNext()) {
			instance = ((StringAttribute) pids.next()).getValue();
			logger.debug("Checking " + instance + " against query " + query);
		}

		try {
			// Send a query to ontology manager to retrieve all devices
			// that fulfill the query
			String deviceList = ontManager.getDevices(query, "");
			logger.debug("Possible devices matching the query: " + deviceList);

			// Convert response to an XML document
			DocumentBuilderFactory factory =
					DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document responseDoc =
					builder
							.parse(new InputSource(new StringReader(deviceList)));

			// Retrieve all device PIDs contained in the response
			javax.xml.xpath.XPath xpath =
					javax.xml.xpath.XPathFactory.newInstance().newXPath();
			javax.xml.xpath.XPathExpression xPathExp =
					xpath.compile("/response/device/@pid");
			NodeList nodes =
					(NodeList) xPathExp.evaluate(responseDoc,
							javax.xml.xpath.XPathConstants.NODESET);

			// Check if the given "instance" is contained in the retrieved
			// list of devices
			isContained = false;
			for (int i = 0; i < nodes.getLength(); i++) {
				if (nodes.item(i).getNodeValue().equals(instance)) {
					logger
							.debug("Found a match. Instance is contained in query.");
					isContained = true;
				}
			}
			result = EvaluationResult.getInstance(isContained);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		// return EvaluationResult.getInstance(isOfType(argValues[1].encode(),
		// argValues[0].encode()));
		long stop = System.currentTimeMillis();
		logger.info("	Evaluate in DL query function took " + (stop - start));
		return result;
	}

	public void setOntologyManager(ApplicationOntologyManager ontManager) {
		this.ontManager = ontManager;
	}
}