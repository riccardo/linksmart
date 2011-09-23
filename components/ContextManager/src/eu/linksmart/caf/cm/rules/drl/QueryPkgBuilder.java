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
package eu.linksmart.caf.cm.rules.drl;

import java.io.PrintStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import eu.linksmart.caf.Attribute;
import eu.linksmart.caf.cm.query.ContextQuery;
import eu.linksmart.caf.cm.query.QuerySet;
import eu.linksmart.caf.cm.rules.DeclaredFunction;

/**
 * Extends {@link BaseDrlBuilder} to provide functionality for parsing
 * {@link QuerySet}s into DRL rule packages, as String.
 * 
 * @author Michael Crouch
 * 
 */
public final class QueryPkgBuilder extends BaseDrlBuilder {

	/**
	 * Private Constructor
	 */
	private QueryPkgBuilder() {
	};

	/**
	 * Encodes a {@link QuerySet}
	 * 
	 * @param querySet
	 *            the {@link QuerySet}
	 * @param out
	 *            the {@link PrintStream} to output to
	 */
	public static void encodeQuerySet(QuerySet querySet, PrintStream out) {
		encodeQueryHeaders(querySet.getPackageName(), querySet.getImports(),
				out);

		for (DeclaredFunction func : querySet.getFunctions()) {
			encodeDeclaredFunction(func, out);
		}
		out.println("");
		for (ContextQuery query : querySet.getQueries()) {
			encodeContextQuery(query, out);
		}
	}

	/**
	 * Encodes the Query header
	 * 
	 * @param pkgName
	 *            the package name
	 * @param imports
	 *            the imports
	 * @param out
	 *            the {@link PrintStream}
	 */
	public static void encodeQueryHeaders(String pkgName, String[] imports,
			PrintStream out) {
		out.println("package " + pkgName);
		out.println("");
		encodeImports(imports, out);
		out.println("");
	}

	/**
	 * Encodes a {@link ContextQuery}
	 * 
	 * @param query
	 *            the {@link ContextQuery}
	 * @param out
	 *            the {@link PrintStream} to output to
	 */
	public static void encodeContextQuery(ContextQuery query, PrintStream out) {

		String argStr = "";
		if (query.getArguments().length > 0) {
			for (Attribute arg : query.getArguments()) {
				argStr += arg.getId() + " " + arg.getValue() + ", ";
			}
			if (argStr.endsWith(", "))
				argStr = argStr.substring(0, argStr.length() - 2);
			argStr = "( " + argStr + ")";
		}
		out.println("query \"" + query.getName() + "\"" + argStr);
		out.println(query.getQuery());
		out.println("end");
	}
}
