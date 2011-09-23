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
 * Copyright (C) 2006-2010 
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
package eu.linksmart.selfstar.cc.asl.parser;

import java.io.PrintStream;
import java.util.Hashtable;
import java.util.Vector;

public class SymTree {
	
	public static final int SCRIPT=99;
	public static final int EXPR=98;
	public static final int EXPRLIST=97;
	public static final int OPCALL=96;
	public static final int PARLIST=95;
	public static final int STRINGDEF=94;
	
	static Hashtable<String,String> stringvars;
	private int type;
	private Object value;
	private Vector<SymTree> children;
	
	public SymTree(int type, Object value){
		this(type,value,(type==EXPRLIST ? 20:1));
	}
	public SymTree(int type, Object value, int count){
		this.type=type;
		this.value=value;
		this.children=new Vector<SymTree>(count);
	}
    
	public Vector<SymTree> getChildren(){
		return children;
	}
	
	public Object getValue(){
		return value;
	}
	
	public SymTree(int type, Object value, SymTree child){
		this(type,value);
		addChild(child);
	}
	
	public SymTree getChild(int index){
		return children.get(index);
	}
	static void setStringVarsDict(Hashtable<String,String> dict){
		stringvars=dict;
	}
	public static Hashtable<String,String> getStringVarsDict(){
		return stringvars;
	}
	public static void PrintStringVars(){
		if (stringvars.isEmpty())
			System.out.println("No variables defined in this script.");
		for (String key:stringvars.keySet()){
			System.out.println("$"+key+"="+stringvars.get(key));
		}
	}
	
	/**
	 * Builds a textual representation of the script that this SymTree represents,
	 * that is, a string which when parsed would yield a SymTree equivalent to
	 * the instance on which this method was invoked. 
	 * 
	 * @param sb The StringBuilder to which the string should be appended. 
	 */
	public void appendStringRepresentation(StringBuilder sb){
		switch (type){
		case SCRIPT:
			sb.append("/* generated from parsed script */\n");
			for (SymTree child:children)
				child.appendStringRepresentation(sb);
			sb.append("\n");
			break;
		case EXPRLIST:
		case EXPR:
			for (SymTree child:children)
				child.appendStringRepresentation(sb);
			break;
		case OPCALL:
			sb.append(value+"(");
			for (SymTree child:children)
				child.appendStringRepresentation(sb);
			sb.append(");\n");
			break;
		case PARLIST:
			Vector<Parameter> pars=(Vector<Parameter>) value;
			for (Parameter p:pars){
				if (p.isString())
					sb.append(p.getString());
				if (p.isDesignator()){
					for(String key:p.getDesignator().keySet())
						sb.append("&"+key+"="+p.getDesignator().get(key));
				}
				if (pars.lastElement()!=p)
					sb.append(",");
			}
			break;
			case STRINGDEF:
				sb.append(value+";");
		}
	}
	
	public int getType(){
		return type;
	}


	public void addChild(SymTree c){
		children.add(c);
	}

	public void printTree(){
		printTree(0);
	}
	
	public void printTree(int indent){
		printTree(indent, System.out);
	}
	
	public void printTree(int indent, PrintStream s){
		for (int i=indent;i>0;i--)
			s.print(" ");
		s.println(sym2Name(type)+" "+value);
		for (SymTree c:children){
			c.printTree(indent+4, s);
		}
	}
	
	public static String sym2Name(int symb){
		switch (symb){
		case sym.COLON:
			return "COLON";
		case sym.ASSIGN:
			return "ASSIGN";
		case sym.COMMA:
			return "COMMA";
		case sym.DOLLAR:
			return "DOLLAR";
		case sym.EOF:
			return "EOF";
		case sym.error:
			return "error";
		case sym.LPAR:
			return "LPAR";
		case sym.RPAR:
			return "RPAR";
		case sym.PLUS:
			return "PLUS";
		case sym.SEMI:
			return "SEMI";
		case sym.STRING:
			return "STRING";
		case SCRIPT:
			return "SCRIPT";
		case EXPR:
			return "EXPR";
		case EXPRLIST:
			return "EXPRLIST";
		case PARLIST:
			return "PARLIST";
		case OPCALL:
			return "OPCALL";
		}
		return "UNRECOGNIZED - perhaps SymTree.sym2Name needs an update!";
	}
}
