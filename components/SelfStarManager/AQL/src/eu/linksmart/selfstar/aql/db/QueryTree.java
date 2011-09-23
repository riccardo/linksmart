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
package eu.linksmart.selfstar.aql.db;

import java.util.ArrayList;
import java.util.Hashtable;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;

import eu.linksmart.selfstar.aql.db.parser.aqlLexer;
import eu.linksmart.selfstar.aql.db.parser.aqlParser;
import eu.linksmart.selfstar.aql.distribution.QueryManager;

public abstract class QueryTree {

	public QueryTree parent;
	/**
	 * Equijoin cf RelationalOperators.EquiJoinIterator(String fname, TupleIterator outer, TupleIterator inner)
	 * @author ingstrup
	 *
	 */
	
	abstract void printBefore(StringBuilder b);
	void printAfter(StringBuilder b){
		b.append(")");
	}

	static class EquiJoinNode extends QueryTree{
		protected QueryTree inner,outer;
		String joinfieldname;
		
		public EquiJoinNode(QueryTree inner, QueryTree outer,
				String joinfieldname) {
			super();
			this.inner = inner;
			inner.parent=this;
			this.outer = outer;
			outer.parent=this;
			this.joinfieldname = joinfieldname;
		}
		
		public boolean equals(QueryTree object){
			if (!(object instanceof QueryTree.EquiJoinNode))
				return false;
			else {
				QueryTree.EquiJoinNode other = (EquiJoinNode) object;
				boolean rval= this.joinfieldname.equals(other.joinfieldname);
				rval &= this.inner.equals(other.inner);
				rval &= this.outer.equals(other.outer);
				return rval;
			}
		}

		public TupleIterator instantiate(){
			TupleIterator tin=inner.instantiate(),
							tout = outer.instantiate();
			return RelationalOperators.equijoinIterator(joinfieldname, tout, tin);
		}

		void printBefore(StringBuilder b){
			b.append("EQUIJOIN \""+joinfieldname+"\" (");
		}

		public void print(StringBuilder b){
			b.append("\n");
			printindent(b);
			b.append("EQUIJOIN \""+joinfieldname+"\" (");
			incindent();
			inner.print(b);
			outer.print(b);
			decindent();
			b.append("\n");
			printindent(b);
			b.append(")");
		}

		@Override
		public TupleIterator instantiateGlobal(ArrayList<QueryTree> markers,
				ArrayList<TupleIterator> substitutes) {
			TupleIterator tin,tout;
			if (markers.contains(inner))
				tin=substitutes.get(markers.indexOf(inner));
			else
				tin=inner.instantiateGlobal(markers,substitutes);
			if (markers.contains(outer))
				tout=substitutes.get(markers.indexOf(outer));
			else
				tout=outer.instantiateGlobal(markers,substitutes);
			return RelationalOperators.equijoinIterator(joinfieldname, tout, tin);
		}

	}

	static class NaturalJoinNode extends QueryTree{
		protected QueryTree inner,outer;
		
		public NaturalJoinNode(QueryTree inner, QueryTree outer) {
			super();
			this.inner = inner;
			inner.parent=this;
			this.outer = outer;
			outer.parent=this;
		}
		
		public boolean equals(QueryTree object){
			if (!(object instanceof QueryTree.EquiJoinNode))
				return false;
			else {
				QueryTree.EquiJoinNode other = (EquiJoinNode) object;
				boolean rval = this.inner.equals(other.inner) && this.outer.equals(other.outer);
				return rval;
			}
		}

		public TupleIterator instantiate(){
			TupleIterator tin=inner.instantiate(),
							tout = outer.instantiate();
			return RelationalOperators.naturaljoinIterator(tout, tin);
		}

		void printBefore(StringBuilder b){
			b.append("NATURALJOIN (");
		}

		public void print(StringBuilder b){
			b.append("\n");
			printindent(b);
			b.append("NATURALJOIN (");
			incindent();
			inner.print(b);
			outer.print(b);
			decindent();
			b.append("\n");
			printindent(b);
			b.append(")");
		}

		@Override
		public TupleIterator instantiateGlobal(ArrayList<QueryTree> markers,
				ArrayList<TupleIterator> substitutes) {
			TupleIterator tin,tout;
			debug("Instantiating global: naturaljoinnode:"+this); 

			if (markers.contains(this)) 
				return substitutes.get(markers.indexOf(this));
			if (markers.contains(inner))
				tin=substitutes.get(markers.indexOf(inner));
			else
				tin=inner.instantiateGlobal(markers,substitutes);
			if (markers.contains(outer))
				tout=substitutes.get(markers.indexOf(outer));
			else
				tout=outer.instantiateGlobal(markers,substitutes);
			
			if (QueryManager.getInstance().debug){
				debug("inner:"+tin+" (in substitutes?="+substitutes.contains(tin)+")");
				debug("outer:"+tout+" (in substitutes?="+substitutes.contains(tout)+")");
				debug("substitutes.length="+substitutes.size());
				debug("inner ("+inner+") replaced = "+(inner!=tin)+" outer ("+outer+") replaced = "+(outer!=tout));
				tin = new CachingIterator(tin);
				tout = new CachingIterator(tout);
				debug("inner:"+tin.toString()+" outer:"+tout.toString());
			}
			return RelationalOperators.naturaljoinIterator(tout, tin);
		}

	}
	
	

	
	/**
	 * Selection cf. RelationalOperators.SelectionIterator(TupleIterator source, TupleFilter filter)
	 * @author ingstrup
	 *
	 */
	static class SelectNode extends QueryTree{
		protected QueryTree source;
		protected String fieldname;
		protected Object constant;
		
		public SelectNode(QueryTree source, String fieldname, Object constant) {
			super();
			this.source = source;
			source.parent=this;
			this.fieldname = fieldname;
			this.constant = constant;
		}
		
		public boolean equals(QueryTree object){
			if (!(object instanceof QueryTree.SelectNode))
				return false;
			else {
				SelectNode other = (SelectNode) object;
				boolean rval= this.source.equals(other.source);
				rval &= this.fieldname.equals(other.fieldname);
				rval &= this.constant.equals(other.constant);
				return rval;
			}
		}

		public TupleIterator instantiate(){
			if (source==null || fieldname==null || constant==null)
				return null;
			TupleIterator tsrc=source.instantiate();
			TupleFilter filter = CommonTupleFilters.equals(constant, fieldname);
			return RelationalOperators.selectionIterator(tsrc, filter);
		}

		void printBefore(StringBuilder b){
			b.append("SELECT "+fieldname+"==\""+constant+"\" (");
		}

		public void print(StringBuilder b){
			b.append("\n");
			printindent(b);
			b.append("SELECT "+fieldname+"==\""+constant+"\" (");
			incindent();
			source.print(b);
			decindent();
			b.append("\n");
			printindent(b);
			b.append(")");
		}

		@Override
		public TupleIterator instantiateGlobal(ArrayList<QueryTree> markers,
				ArrayList<TupleIterator> substitutes) {
			TupleIterator ti;
			if (markers.contains(this))
				return substitutes.get(markers.indexOf(this));
			if (markers.contains(source))
				ti=substitutes.get(markers.indexOf(source));
			else
				ti=source.instantiateGlobal(markers, substitutes);
			TupleFilter filter = CommonTupleFilters.equals(constant, fieldname);
			return RelationalOperators.selectionIterator(ti, filter);
		}

	}
	
	/**
	 * Projection cf RelationalOperators.ProjectIterator(Schema newschema, TupleIterator source)
	 * @author ingstrup
	 *
	 */
	static class ProjectNode extends QueryTree{
		protected QueryTree source;
		String fieldnames[];
		
		public ProjectNode(QueryTree source, String[] fieldnames) {
			super();
			this.source = source;
			source.parent=this;
			this.fieldnames = fieldnames;
		}

		public TupleIterator instantiate(){
			return RelationalOperators.projectionIterator(fieldnames, source.instantiate());
		}
		
		public boolean equals(QueryTree object){
			if (!(object instanceof QueryTree.ProjectNode))
				return false;
			else {
				ProjectNode other = (ProjectNode) object;
				if (fieldnames.length!=other.fieldnames.length)
					return false;
				boolean rval= this.source.equals(other.source);
				for (int i=0;i<fieldnames.length;i++)
					rval &= fieldnames[i].equals(other.fieldnames[i]);
				return rval;
			}
		}


		void printBefore(StringBuilder b){
			b.append("PROJECT [ ");
			for (String s:fieldnames)
				b.append(s+" ");
			b.append("] (");
		}
		
		public void print(StringBuilder b){
			b.append("\n");
			printindent(b);
			b.append("PROJECT [ ");
			for (String s:fieldnames)
				b.append(s+" ");
			b.append("] (");
			incindent();
			source.print(b);
			decindent();
			b.append("\n");
			printindent(b);
			b.append(")");
		}

		@Override
		public TupleIterator instantiateGlobal(ArrayList<QueryTree> markers,
				ArrayList<TupleIterator> substitutes) {
			TupleIterator ti;
			if (markers.contains(source))
				ti=substitutes.get(markers.indexOf(source));
			else
				ti=source.instantiateGlobal(markers, substitutes);
			return RelationalOperators.projectionIterator(fieldnames, ti);
		}
	}
	
	static class RenameNode extends QueryTree{
		
		QueryTree source;
		Hashtable<String,String> map;
		RenameNode(QueryTree source, Hashtable<String,String> map){
			this.source=source;
			this.map=map;
		}
		
		void printBefore(StringBuilder b){
			b.append("RENAME [ ");
			for (String k:map.keySet())
				b.append(k+"->"+map.get(k));
			b.append("] (");
		}
		
		public void print(StringBuilder b){
			b.append("\n");
			printindent(b);
			b.append("RENAME [ ");
			for (String k:map.keySet())
				b.append(k+"->"+map.get(k)+" ");
			b.append("] (");
			incindent();
			source.print(b);
			decindent();
			b.append("\n");
			printindent(b);
			b.append(")");
		}

		
		
		@Override
		public TupleIterator instantiateGlobal(ArrayList<QueryTree> markers,
				ArrayList<TupleIterator> substitutes) {
			TupleIterator ti;
			System.out.println("Instantiating global: renamenode:"+this);
			if (markers.contains(this))
				return substitutes.get(markers.indexOf(this));
			if (markers.contains(source))
				ti=substitutes.get(markers.indexOf(source));
			else
				ti=source.instantiateGlobal(markers, substitutes);
			//TupleFilter filter = CommonTupleFilters.equals(constant, fieldname);
			return RelationalOperators.renameIterator(map, ti);
		}

		@Override
		public TupleIterator instantiate() {
			return RelationalOperators.renameIterator(map,source.instantiate());
		}

		@Override
		public boolean equals(QueryTree object) {
			if (!(object instanceof QueryTree.RenameNode))
				return false;
			else {
				RenameNode other = (RenameNode) object;
				if (!map.equals(other.map))
					return false;
				return this.source.equals(other.source);
			}
			
		}

		
	}
	
	/** A QueryTree 'leaf' node, ie it gets its data directly from a table registered in the TableRegistry.
	 * 
	 * @author ingstrup
	 *
	 */
	static class TableNode extends QueryTree{
		String sourcetablename;
		
		TableNode(String tablename){
			sourcetablename=tablename;
		}
		
		public TupleIterator instantiate(){
			return TableRegistry.getInstance().getIterator(sourcetablename); //getTable(sourcetablename).iterator();
		}
		
		public TupleIterator instantiateGlobal(ArrayList<QueryTree> markers, ArrayList<TupleIterator> substitutes){
			throw new RuntimeException("This shouldn't be called - all leaves in the query tree must be replaced with iterators from data received on the eventmanager.");
			//return instantiate();
		}
		
		public boolean equals(QueryTree object){
			if (!(object instanceof TableNode))
				return false;
			else {
				return sourcetablename.equals(((TableNode)object).sourcetablename);
			}
		}
		void printBefore(StringBuilder b){
			b.append(sourcetablename);
		}
		public void print(StringBuilder b){
			b.append("\n");
			printindent(b);
			b.append(sourcetablename);
		}
	}
	
	/**
	 * Makes an abstract representation of a TupleIterator over a table of the given name.
	 * @param sourcetablename
	 * @return
	 */
	public static QueryTree makeTableNode(String sourcetablename){
		return new TableNode(sourcetablename);
	}
	
	/**
	 * Makes an abstract representation of a rename operator. The tuples returned are identical to those
	 * returned by the source, but the schema where the column names are defined differs.
	 * @param source
	 * @param map
	 * @return
	 */
	public static QueryTree makeRenameNode(QueryTree source, Hashtable<String,String> map){
		return new RenameNode(source,map);
	}
	
	/**
	 * Makes an abstract representation of an EquiJoinIterator on the joinfield of the two given tables. 
	 * @param inner
	 * @param outer
	 * @param joinfield
	 * @return
	 */
	public static QueryTree makeEquiJoinNode(QueryTree inner, QueryTree outer, String joinfield){
		return new EquiJoinNode(inner, outer, joinfield);
	}
	/**
	 * Makes an abstract representation of a NaturalJoinIterator of the two given sources. The natural 
	 * join of two sources include elements with identical values in fields of the same name from the cross 
	 * product of the sources' elements.
	 * @param inner
	 * @param outer
	 * @return
	 */
	public static QueryTree makeNaturalJoinNode(QueryTree inner, QueryTree outer) {
		QueryTree r=new NaturalJoinNode(inner,outer);
		return r;
	}

	/**
	 * Makes a projection node, ie the node represents a selection of a subset of the 
	 * columns in the iterator over the source.
	 * 
	 * @param fieldnames
	 * @param source
	 * @return
	 */
	public static QueryTree makeProjectNode(String[] fieldnames, QueryTree source){
		return new ProjectNode(source,fieldnames);
	}
	
	/**
	 * Creates an abstract representation of a TupleIterator that includes only those tuples
	 * from the source for which the field of the given name equals the constant. 
	 * @param source
	 * @param fieldname
	 * @param constant
	 * @return
	 */
	public static QueryTree makeSelectNode(QueryTree source, String fieldname, Object constant){
		return new SelectNode(source,fieldname,constant);
	}
	/**
	 * Binds this query to source tables and returns an iterator over its result.
	 * @return
	 */
	public abstract TupleIterator instantiate();
	public abstract void print(StringBuilder b);
	public abstract boolean equals(QueryTree other);
	public abstract TupleIterator instantiateGlobal(ArrayList<QueryTree> markers, ArrayList<TupleIterator> substitutes);
	
	public String toString(){
		StringBuilder b = new StringBuilder();
		print(b);
		return b.toString();
	}
	public void print(){
		
		//StringBuilder b = new StringBuilder();
		//print(b);
		//System.out.println(b);
		System.out.println(visitPrint());
	}
	
	public boolean isLocal(){
		return type==LOCAL;
	}
	public boolean isGlobal(){
		return type==GLOBAL;
	}
	
	String visitPrint(){
		final StringBuilder b = new StringBuilder();
		new QueryTreeVisitor(false) {
			
			void visitAll(QueryTree tree){
				if (tree.isLocal() || tree.isGlobal()){
					b.append("\n");
					printindent(b);
					if (tree.isLocal())
						b.append("LOCAL (");
					if (tree.isGlobal())
						b.append("GLOBAL (");
					incindent();
					printindent(b);
					visitNormal(tree);
					decindent();
					b.append("\n");
					printindent(b);
					b.append(")");
				} else
					visitNormal(tree);
			}
		
			void visitNormal(QueryTree tree){
				b.append("\n");
				printindent(b);
				tree.printBefore(b);
				incindent();
				printindent(b);
				super.visitAll(tree);
				decindent();
				b.append("\n");
				printindent(b);
				tree.printAfter(b);
			}
			
			@Override
			void visit(QueryTree tree) {
				
			}
		}.visitAll(this);
		return b.toString();
	}
	
	
	static int indent=0;
	
	static void incindent(){ 
		indent++;
	}
	
	static void decindent(){ 
		indent--;
	}
	
	private static void printindent(StringBuilder b){ 
		for( int i=0;i<indent;i++)
			b.append("\t");
	}	
	
	public static int 	
		LOCAL=1,
		GLOBAL=0;
	int type =-1;
	public void setType(int t){
		this.type=t;
	}
	
	public static QueryTree parseQuery(String query) throws RecognitionException{
		ANTLRStringStream s = new ANTLRStringStream(query);
		// create a lexer that feeds off of input CharStream
		aqlLexer lexer = new aqlLexer(s); 
		// create a buffer of tokens pulled from the lexer
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		// create a parser that feeds off the tokens' buffer
		aqlParser parser = new aqlParser(tokens); // begin parsing at rule r 
		return parser.start();
	}
	/*
	public static void main(String[] args){
		// patterns:
		String 
			ptype = "(SELECT)|(EQUIJOIN)|(PROJECT)|(?:\\A\\s*\"([^\"]+)\")",
			select_args = "";
		
		Pattern pattern = Pattern.compile("\\s*((?:SELECT)|(?:EQUIJOIN)(?:PROJECT))([^\\(]+)\\(((.*))\\)\\s*");
		
		Pattern p_type = Pattern.compile(ptype);//"\\s*(SELECT)|(?:EQUIJOIN)|(?:PROJECT)|(?:\"([^\"]+)\")");
		Pattern select_arg = Pattern.compile("\\s*(\\w+)==\"([^\"]+)\"");
		
		StringBuffer buffer = new StringBuffer(" \nSELECT author==\"Philip K. Dick\" ( \"Books\")");
		Matcher matcher = p_type.matcher(buffer);
		while (matcher.find()){
			for(int i=0;i<matcher.groupCount();i++)
				System.out.println("Group "+i+">"+matcher.group(i)+"<");
		}
		
	}
	*/
	
	private abstract class QueryTreeVisitor {
		
		@SuppressWarnings("unused") // could be used in parser code generated by antlr
		QueryTreeVisitor(){
		}
		
		QueryTreeVisitor(boolean depthfirst){
			this.depthfirst=depthfirst;
		}
		
		private boolean depthfirst=true;
		
		void visitAll(QueryTree tree){
			if (!depthfirst)
				visit(tree);
			if (tree instanceof ProjectNode)
				visitAll(((ProjectNode)tree).source);
			if (tree instanceof SelectNode)
				visitAll(((SelectNode)tree).source);
			if (tree instanceof EquiJoinNode){
				visitAll(((EquiJoinNode)tree).inner);
				visitAll(((EquiJoinNode)tree).outer);
			}
			if (tree instanceof NaturalJoinNode){
				visitAll(((NaturalJoinNode)tree).inner);
				visitAll(((NaturalJoinNode)tree).outer);
				
			}
			if (tree instanceof RenameNode){
				visitAll(((RenameNode)tree).source);
			}
			//if (tree instanceof TableNode): no children
			if (depthfirst)
				visit(tree);
		}
		
		abstract void visit(QueryTree tree);
	}

	
	
	/*
	QueryTree getLocalQuery(QueryTree distributedquery){
		final ArrayList<QueryTree> leaves=new ArrayList<QueryTree>(5);
		new  QueryTreeVisitor(true){
			void visit(QueryTree tree){
				if (tree instanceof TableNode)
					leaves.add(tree);
					
			}
		}.visitAll(distributedquery);
		// all leaves of the tree are in the leaves list
		// now for each one, trace it to its parent until reaching the first join, if any
		
		return null;
	}
	*/
	
	/**
	 * split a query into local and global parts. 
	 * merge results of local queries - make MergingTupleIterator class; add isMergeable method to Schema class.
	 * execute local queries
	 */
	public ArrayList<QueryTree> getLocalQueries(){
		final ArrayList<QueryTree> parts = new ArrayList<QueryTree>(10);
		new QueryTreeVisitor(true) {		
			@Override
			void visit(QueryTree tree) {
				if (tree.isLocal())
					parts.add(tree);
			}
		}.visitAll(this);
		return parts;
	}
	
	public QueryTree getGlobalQuery(){
		//TODO
		return this;
	}
	
	
	void debug(String msg){
		QueryManager.getInstance().debug(msg);
	}
	
	/**
	 * Convenience method for building queries programmatically. The returned abstract
	 * query cannot be instantiated before the source is set, by calling the from(method) on the returned object.
	 * 
	 * I.e. QueryTree.select("field==constant").from(anotherquerytree);
	 * @param condition
	 * @return
	 * @throws QueryBuilderException
	 */
	
}

















