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
import eu.linksmart.caf.cm.rules.DeclaredFunction;
import eu.linksmart.caf.cm.rules.DeclaredType;

/**
 * Abstract class for DRL building classes, providing common methods for
 * encoding DRL content
 * 
 * @author Michael Crouch
 * 
 */
public abstract class BaseDrlBuilder {

	/**
	 * Encodes the base imports for all rule packages, along with any
	 * additionally specified as an array of String
	 * 
	 * @param imports
	 *            array of additional classes to import
	 * @param out
	 *            the {@link PrintStream} to output to
	 */
	public static void encodeImports(String[] imports, PrintStream out) {
		Set<String> importSet = new HashSet<String>();
		importSet.addAll(BaseImports.getImports());

		for (int i = 0; i < imports.length; i++) {
			if (!importSet.contains(imports[i]))
				importSet.add(imports[i]);
		}

		Iterator<String> it = importSet.iterator();
		while (it.hasNext()) {
			out.println("import " + it.next() + ";");
		}
	}

	/**
	 * Encodes the event declaration to the {@link PrintStream}
	 * 
	 * @param out
	 *            the {@link PrintStream}
	 */
	public static void encodeEventDeclaration(PrintStream out) {
		out.println("declare Event");
		out.println("@role( event )");
		// out.println("@duration( eventDuration )");
		out.println("end");
	}

	/**
	 * Encodes all {@link DeclaredFunction}s to the output
	 * 
	 * @param functions
	 *            array of {@link DeclaredFunction}
	 * @param out
	 *            the {@link PrintStream} to output to
	 */
	public static void encodeDeclaredFunctions(DeclaredFunction[] functions,
			PrintStream out) {
		if (functions == null)
			return;

		for (int i = 0; i < functions.length; i++) {
			DeclaredFunction func = functions[i];
			encodeDeclaredFunction(func, out);
		}
	}

	/**
	 * Encodes all {@link DeclaredType}s to the output
	 * 
	 * @param types
	 *            array of {@link DeclaredType}
	 * @param out
	 *            the {@link PrintStream} to output to
	 */
	public static void encodeDeclaredTypes(DeclaredType[] types, PrintStream out) {
		if (types == null)
			return;

		for (int j = 0; j < types.length; j++) {
			DeclaredType type = types[j];
			encodeDeclaredType(type, out);
		}
	}

	/**
	 * Encodes a single {@link DeclaredFunction}s to the output
	 * 
	 * @param func
	 *            the {@link DeclaredFunction}
	 * @param out
	 *            the {@link PrintStream} to output to
	 */
	public static void encodeDeclaredFunction(DeclaredFunction func,
			PrintStream out) {

		StringBuffer buffer = new StringBuffer("(");
		for (int i = 0; i < func.getArguments().length; i++) {
			Attribute arg = func.getArguments()[i];
			buffer.append(arg.getId()).append(" ").append(arg.getValue());
			if ((i + 1) < func.getArguments().length)
				buffer.append(", ");
		}
		buffer.append(")");

		out.println("function " + func.getReturnType() + " " + func.getName()
				+ buffer.toString() + "{");
		out.println(func.getCode());
		out.println("}");
	}

	/**
	 * Encodes a single {@link DeclaredType}s to the output
	 * 
	 * @param type
	 *            the {@link DeclaredType}
	 * @param out
	 *            the {@link PrintStream} to output to
	 */
	public static void encodeDeclaredType(DeclaredType type, PrintStream out) {
		out.println("declare " + type.getName() + "{");
		out.println("@role(" + type.getFactRole() + ")");
		for (int i = 0; i < type.getMetaAttributes().length; i++) {
			Attribute meta = type.getMetaAttributes()[i];
			out.println("@" + meta.getId() + "(" + meta.getValue() + ")");
		}

		for (int j = 0; j < type.getClassMembers().length; j++) {
			Attribute member = type.getClassMembers()[j];
			out.println(member.getId() + " : " + member.getValue());
		}
		out.println("}");
	}

}
