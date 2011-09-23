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
package eu.linksmart.caf.cm.managers;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.drools.builder.KnowledgeBuilder;
import org.drools.runtime.rule.QueryResults;
import org.drools.runtime.rule.QueryResultsRow;


import eu.linksmart.caf.Attribute;
import eu.linksmart.caf.Parameter;
import eu.linksmart.caf.cm.ContextManagerError;
import eu.linksmart.caf.cm.engine.Encodeable;
import eu.linksmart.caf.cm.engine.contexts.BaseContext;
import eu.linksmart.caf.cm.exceptions.ContextManagerException;
import eu.linksmart.caf.cm.exceptions.ErrorListException;
import eu.linksmart.caf.cm.impl.CmManagerHub;
import eu.linksmart.caf.cm.query.ContextQuery;
import eu.linksmart.caf.cm.query.QueryResponse;
import eu.linksmart.caf.cm.query.QueryRow;
import eu.linksmart.caf.cm.query.QuerySet;
import eu.linksmart.caf.cm.rules.drl.QueryPkgBuilder;

/**
 * Manages the storage of {@link QuerySet}s, in a {@link QueryStore} and handles
 * the processing of queries.
 */
public class QueryManager extends CmInternalManager {

	/** The Id for this Internal Manager */
	public static final String MANAGER_ID = "eu.linksmart.caf.cm.QueryManager";

	/** the {@link Logger} */
	private static final Logger logger = Logger.getLogger(QueryManager.class);

	/** Query package prefix for a "SingleQuery" */
	private static final String SINGLE_QUERY_PKG =
			"eu.linksmart.caf.cm.singlequerypackage.";

	/** Expected output variable for single queries */
	private static final String SINGLE_QUERY_OUTPUT = "output";

	/**
	 * {@link HashMap} the installed {@link QuerySet}s, indexed by their package
	 * names
	 */
	private HashMap<String, QuerySet> querySets;

	/** the {@link RuleEngine} */
	private RuleEngine ruleEngine;

	/**
	 * Constructor
	 */
	public QueryManager() {
		querySets = new HashMap<String, QuerySet>();
	}

	@Override
	public void initialise(CmManagerHub hub) {
		this.ruleEngine = (RuleEngine) hub.getManager(RuleEngine.MANAGER_ID);
	}
	
	@Override
	public void completedInit() {
		//Do nothing		
	}

	@Override
	public void shutdown() {
		// None
	}

	@Override
	public String getManagerId() {
		return MANAGER_ID;
	}

	/**
	 * Loads the {@link QuerySet} to the {@link RuleEngine}, including parsing
	 * it to DRL
	 * 
	 * @param querySet
	 *            the {@link QuerySet}
	 * @throws ErrorListException
	 */
	public void loadQuerySetToEngine(QuerySet querySet)
			throws ErrorListException {

		// Check that no query names clash
		ErrorListException error = new ErrorListException();
		for (ContextQuery query : querySet.getQueries()) {
			if (hasQuery(querySet.getPackageName(), query.getName()))
				error
						.addError(querySet.getPackageName(), query.getName(),
								"Query with this name already exists in another installed package");
		}

		if (error.getErrorList().size() > 0)
			throw error;

		// Parse into DRL
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(baos);

		QueryPkgBuilder.encodeQuerySet(querySet, out);

		KnowledgeBuilder kBuilder = ruleEngine.getKnowledgeBuilderSession();
		// Compile query rule package
		ruleEngine.compileRulePackage(baos.toString(), kBuilder);
		ruleEngine.addBuiltKnowledge(kBuilder);

	}

	/**
	 * Stores the {@link QuerySet} to the {@link QueryStore}
	 * 
	 * @param querySet
	 *            the {@link QuerySet} to store
	 */
	public void storeQuerySet(QuerySet querySet) {
		querySets.put(querySet.getPackageName(), querySet);
	}

	/**
	 * Gets the {@link QuerySet} with the given package name from the
	 * {@link QueryStore}
	 * 
	 * @param pkgName
	 *            the package name
	 * @return the {@link QuerySet}
	 * @throws ContextManagerException
	 */
	public QuerySet getQuerySet(String pkgName) throws ContextManagerException {

		QuerySet qs = querySets.get(pkgName);
		if (qs == null) {
			throw new ContextManagerException(pkgName, "Error getting Query",
					"Query doesn't exist");
		}
		return qs;

	}

	/**
	 * Returns whether the {@link QueryStore} contains a {@link QuerySet} with
	 * the given package name
	 * 
	 * @param pkgName
	 *            the package name
	 * @return whether it exists
	 */
	public boolean hasQuerySet(String pkgName) {

		String[] ids = getStoredPkgs();
		for (String id : ids) {
			if (id.equalsIgnoreCase(pkgName))
				return true;
		}
		return false;
	}

	/**
	 * Returns the {@link ContextQuery} with the given name, from the
	 * {@link QuerySet} with the given package name. If it doesn't exist, null
	 * is returned
	 * 
	 * @param pkgName
	 *            the package name of the {@link QuerySet} to get the
	 *            {@link ContextQuery} from
	 * @param queryName
	 *            the name of the {@link ContextQuery} - unique in the Rule
	 *            Engine
	 * @return the found {@link ContextQuery} object
	 */
	public ContextQuery getQuery(String pkgName, String queryName) {
		QuerySet qs = querySets.get(pkgName);
		if (qs != null) {
			for (ContextQuery query : qs.getQueries()) {
				if (query.getName().equals(queryName))
					return query;
			}
		}
		return null;
	}

	/**
	 * Searches all stored {@link QuerySet}s, to find the {@link ContextQuery}
	 * with the given name. Should be unique.
	 * 
	 * @param queryName
	 *            the unique name of the {@link ContextQuery}
	 * @return the found {@link ContextQuery}, or null if it doesn't exist
	 */
	public ContextQuery getQuery(String queryName) {
		Iterator<String> it = querySets.keySet().iterator();
		while (it.hasNext()) {
			ContextQuery query = getQuery(it.next(), queryName);
			if (query != null)
				return query;
		}
		return null;
	}

	/**
	 * Checks whether there isn't already a {@link ContextQuery} with the same
	 * name, in a different {@link QuerySet}
	 * 
	 * @param addedPackage
	 *            the package of the the {@link QuerySet} to be excluded from
	 *            the search
	 * @param queryName
	 *            the name of the {@link ContextQuery} to search for
	 * @return boolean stating whether it exists or not
	 */
	public boolean hasQuery(String addedPackage, String queryName) {
		Iterator<String> it = querySets.keySet().iterator();
		while (it.hasNext()) {
			String pkgName = it.next();
			if (!pkgName.equals(addedPackage)) {
				if (getQuery(pkgName, queryName) != null)
					return true;
			}
		}
		return false;
	}

	/**
	 * Returns a String array of all the package names of stored
	 * {@link QuerySet}s
	 * 
	 * @return a String array
	 */
	public String[] getStoredPkgs() {

		Set<String> pkgSet = querySets.keySet();
		if (pkgSet.size() > 0) {
			return pkgSet.toArray(new String[pkgSet.size()]);
		} else
			return new String[0];
	}

	/**
	 * Removes the {@link QuerySet} with the given package name from the
	 * {@link RuleEngine} and from the {@link QueryStore}
	 * 
	 * @param pkgName
	 *            the package anme to remove
	 */
	public void removeQuerySet(String pkgName) {
		// remove from rule engine
		ruleEngine.removePackage(pkgName);
		querySets.remove(pkgName);
	}

	/**
	 * Gets an array of the defined output of a {@link ContextQuery}, as an
	 * array of {@link Attribute}s
	 * 
	 * @param namedQuery
	 *            the name of the {@link ContextQuery}
	 * @return an array of {@link Attribute}
	 * @throws ContextManagerException
	 */
	public Attribute[] getQueryOutput(String namedQuery)
			throws ContextManagerException {

		ContextQuery query = getQuery(namedQuery);
		if (query != null) {
			Attribute[] output = query.getOutput();
			if (output != null) {
				return output;
			} else {
				throw new ContextManagerException(namedQuery,
						"Error getting output from ContextQuery",
						"Query output is null");
			}
		}
		throw new ContextManagerException(namedQuery, "Error getting Query",
				"Query does not exist");
	}

	/**
	 * Executes the Named Query in the Rule Engine, parsing and returning the
	 * results. Encodes query results to String.
	 * 
	 * @param queryName
	 *            the name of the query
	 * @param queryArgs
	 *            the arguments to pass to the query
	 * @return the {@link QueryResponse}
	 * @throws ContextManagerException
	 */
	public QueryResponse executeNamedQuery(String queryName,
			Parameter[] queryArgs) throws ContextManagerException {
		QueryResponse response = new QueryResponse();
		response.setResults(new QueryRow[0]);

		List<QueryRow> queryRows = new ArrayList<QueryRow>();

		QueryResults results =
				ruleEngine.executeNamedQuery(queryName, queryArgs);
		if (results == null) {
			response.setResults(new QueryRow[0]);
			response.setErrors(new ContextManagerError[0]);
			return response;
		}
		Attribute[] output = getQueryOutput(queryName);
		for (String id : results.getIdentifiers()) {
			for (Attribute attr : output) {
				if (attr.getId().equals(id)) {
					QueryRow row = buildQueryRow(id, results);
					queryRows.add(row);
				}
			}
		}

		if (queryRows.size() > 0) {
			response.setResults(queryRows
					.toArray(new QueryRow[queryRows.size()]));
		}

		return response;
	}

	/**
	 * Handles the processing of a single query.<p> A temporary rule package is
	 * created for the {@link ContextQuery}, with unique names given to both the
	 * rule package and the query. It is then installed in the
	 * {@link RuleEngine}, executed, and removed, with the results returned.
	 * 
	 * @param query
	 *            the {@link ContextQuery}
	 * @return the {@link QueryResponse}
	 * @throws ErrorListException
	 * @throws ContextManagerException
	 */
	public QueryResponse executeSingleQuery(ContextQuery query)
			throws ErrorListException, ContextManagerException {

		// create query drl with temp packagename and query name
		String pkgName = SINGLE_QUERY_PKG + "pkg" + System.currentTimeMillis();
		String queryName = query.getName() + "_" + System.currentTimeMillis();
		query.setName(queryName);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(baos);

		QueryPkgBuilder.encodeQueryHeaders(pkgName, new String[0], out);
		QueryPkgBuilder.encodeContextQuery(query, out);

		// compile and add to rule engine
		KnowledgeBuilder builder = ruleEngine.getKnowledgeBuilderSession();
		ruleEngine.compileRulePackage(baos.toString(), builder);
		ruleEngine.addBuiltKnowledge(builder);

		// executre query
		QueryResults results = ruleEngine.executeNamedQuery(queryName, null);
		// build response
		QueryResponse response = new QueryResponse();
		if (results != null) {
			// Encode results to XML
			QueryRow qRow = buildQueryRow(SINGLE_QUERY_OUTPUT, results);
			// Remove temp query package from rule enigne
			ruleEngine.removePackage(pkgName);

			QueryRow[] rows = { qRow };
			response.setResults(rows);
		} else
			response.setResults(new QueryRow[0]);

		response.setErrors(new ContextManagerError[0]);
		return response;
	}

	/**
	 * H Executes the given query on the {@link RuleEngine}, to return all
	 * matching contextIds
	 * 
	 * @param query
	 *            the query (DRL)
	 * @return the array of matching contextIds
	 * @throws ErrorListException
	 * @throws ContextManagerException
	 */
	public String[] executeContextIdQuery(String query)
			throws ErrorListException, ContextManagerException {
		String pkgName = SINGLE_QUERY_PKG + "pkg" + System.currentTimeMillis();
		String queryName = "contextIdQuery_" + System.currentTimeMillis();

		ContextQuery cQuery = new ContextQuery();
		cQuery.setQuery(query);
		cQuery.setName(queryName);
		cQuery.setArguments(new Attribute[0]);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(baos);

		QueryPkgBuilder.encodeQueryHeaders(pkgName, new String[0], out);
		QueryPkgBuilder.encodeContextQuery(cQuery, out);

		// compile and add to rule engine
		KnowledgeBuilder builder = ruleEngine.getKnowledgeBuilderSession();
		ruleEngine.compileRulePackage(baos.toString(), builder);
		ruleEngine.addBuiltKnowledge(builder);

		// execute query
		QueryResults results = ruleEngine.executeNamedQuery(queryName, null);
		Set<String> cIds = new HashSet<String>();
		if (results != null) {
			Iterator<QueryResultsRow> it = results.iterator();
			while (it.hasNext()) {
				QueryResultsRow row = it.next();
				Object obj = row.get(SINGLE_QUERY_OUTPUT);
				if (obj instanceof BaseContext) {
					BaseContext baseContext = (BaseContext) obj;
					cIds.add(baseContext.getContextId());
				}
			}

			// Remove temp query package from rule engine
			ruleEngine.removePackage(pkgName);

			if (cIds.size() > 0)
				return (String[]) cIds.toArray(new String[cIds.size()]);
		}

		return new String[0];
	}

	/**
	 * Builds the result {@link QueryRow} for the given id and
	 * {@link QueryResultsRow}
	 * 
	 * @param id
	 *            the id
	 * @param results
	 *            the {@link QueryResultsRow}
	 * @return the built {@link QueryRow}
	 */
	private QueryRow buildQueryRow(String id, QueryResults results) {
		ByteArrayOutputStream resBaos = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(resBaos);
		out.println("<" + id + ">");
		QueryRow qRow = new QueryRow();
		qRow.setRowId(id);
		Iterator<QueryResultsRow> it = results.iterator();
		while (it.hasNext()) {
			QueryResultsRow row = it.next();
			Object obj = row.get(id);
			if (obj instanceof Encodeable) {
				qRow.setResultType("EncodedXML");
				Encodeable outObj = (Encodeable) obj;
				outObj.encode(out);
			} else if (obj instanceof Collection<?>) {
				qRow.setResultType("EncodedXML");
				Iterator colIt = ((Collection) obj).iterator();
				while (colIt.hasNext()) {
					Object colObj = colIt.next();
					if (colObj instanceof Encodeable) {
						((Encodeable) colObj).encode(out);
					} else {
						out.println("<value>" + colObj.toString() + "</value>");
					}
				}
			} else {
				qRow.setResultContent("Value");
				out.println("<value>" + obj.toString() + "</value>");
			}
		}
		out.println("</" + id + ">");
		qRow.setResultContent(resBaos.toString());
		return qRow;
	}
}
