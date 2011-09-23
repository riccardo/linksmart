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
import java.util.Iterator;
import java.util.NoSuchElementException;

public class RelationalOperators {

	Table t1, t2;
	
	public RelationalOperators(){
		
	}
	/**
	 * Computes the equijoin of the two tables on the indicated attributes.
	 * @param t1 the first table in the join operation
	 * @param t1 includedfields the fields from T1 that will be included in the table resulting from the join.
	 * @param t2  the second table in the join operation
	 * @param t2 indludedfields the fields from T2 that will be included in the table resulting from the join.
	 * @param t1 joinfield the attribute on T1 to compare with one on T2
	 * @param t2 joinfield the attribute on T2 to compare with one from T1
	 * @return
	 */
	Table equiJoin(Table t1, int[] t1includedfields, Table t2, int[] t2indludedfields, int t1joinfield,int t2joinfield){
		// compute schema for join result:
		return t1;
	}
	
	
	/*enum IType {projection, selection, global_equijoin,local_equijoin}
	class ITree {
		IType type;
		ITree src, optsrc;
		TupleFilter filter;
		Schema projectschema;
		String srctablename;
		String optsrctablename;
		String joinfieldname;
		
		/*ITree project(ITree src, String...fieldnames){
			return src;
		}
		ITree select(ITree src,TupleFilter filter){
			return src;
		}
		ITree global_equijoin(String joinfieldname, ITree src, ITree src2){
			return src;
		}
		ITree local_equijoin(String joinfieldname, ITree src, ITree src2){
			return src;
		}* /
		
		TupleIterator instantiate(){
			// fix me check if src is a table instead of an iterator...
			// always instantiate primary source
			TupleIterator srcI;
			if(srctablename!=null)
				srcI=TableRegistry.getInstance().getTable(srctablename).iterator();
			else
				srcI = src.instantiate();
			switch (type){
			case projection:
				return projectionIterator(projectschema, srcI);
			case selection:
				return selectionIterator(srcI, filter);
			case local_equijoin:
			case global_equijoin:
				TupleIterator src2;
				if(optsrctablename!=null)
					src2=TableRegistry.getInstance().getTable(optsrctablename).iterator();
				else
					src2=optsrc.instantiate();
				return equijoinIterator(joinfieldname, srcI,src2); 
			}
			return null;
		}
	}
	*/
	private static class EquiJoinIterator implements TupleIterator{
		
		TupleIterator outer;
		ResettableTupleIterator inner;
		@SuppressWarnings("unused")
		String fname;
		int ofindex,ifindex;
		Schema schema;
		Tuple currentOuter=null,currentInner=null;
		int[] fieldmap;
		boolean touched=false;
		Tuple next;
		
		public EquiJoinIterator(String fname, TupleIterator outer, TupleIterator inner){
			this.fname=fname;
			this.outer=outer;
			this.inner=new CachingIterator(inner);
			try{
				schema=outer.getSchema().getJoinSchema(inner.getSchema(), new String[]{fname});
			} catch (JoinException e){
				e.printStackTrace();
			}
			ofindex=outer.getSchema().getIntFromName(fname);
			ifindex=inner.getSchema().getIntFromName(fname);
			fieldmap=schema.joinSchemaMap;
		}

		public Schema getSchema() {
			return schema;
		}

		boolean matches(Tuple otuple, Tuple ituple){
			return (otuple.getValue(ofindex).equals(ituple.getValue(ifindex)));
		}
		
		private Tuple makeTuple(){
			Tuple rval=schema.newTuple();
			int tmp;
			loop:
			for(int i=0;i<fieldmap.length;i++){
				if((fieldmap[i]&1024)==0){ // its in the outer tuple
					rval.setUncheckedValue(i, currentOuter.getValue(fieldmap[i]));
					continue loop;
				}
				if ((fieldmap[i]&1024)>=1024){ // its in the inner tuple
					tmp=fieldmap[i]&1023;
					rval.setUncheckedValue(i, currentInner.getValue(tmp));
					continue loop;
				}
			}
			return rval;	
		}
		
		private Tuple searchNext(){
			/* if we didn't just initialize, the currentO and currentI must have the value
			 * from which the previously returned tuple was made
			 *
			 * if current tuple in outer loop has a value and the inner isn't exhausted, we need to test
			 * the remaining elements in the inner iterator/loop...
			 */
			if(currentOuter!=null && inner.hasNext()){
				while (inner.hasNext()){
					currentInner=inner.next();
					if(matches(currentOuter,currentInner))
						return makeTuple();
				}
				// inner exhausted->reset and increment outer
				inner.reset();
			}
			/* 
			 * also, if not just initialized we know currentOuter !=null &&
			 * the inner has been reset, cf above; 
			 * in that case the outer must be incremented by one, and the search continued
			 * 
			 * if we just initialized, the outer must be initialized and search continued
			 * The code for these two cases are identical:
			 */
			//if(currentOuter==null && currentInner==null)
			while(outer.hasNext()){
				currentOuter=outer.next();
				while(inner.hasNext()){
					currentInner=inner.next();
					if(matches(currentOuter,currentInner))
						return makeTuple();
				}
				// inner loop exhausted, but no match found-> reset inner iterator and continue
				inner.reset();
			}
			// both the inner and outer loops must be exhausted->no more elements
			// thus we return null
			return null;
		}
		
		public boolean hasNext() {
			if(!touched){
				touched=true;
				next=searchNext();
			}
			return (next!=null);
		}

		public Tuple next() {
			if(!hasNext())
				throw new NoSuchElementException("No more elements in iterator");
			Tuple rval=next;
			next=searchNext();
			return rval;
		}

		public Iterator<Tuple> iterator() {
			return this;
		}

		public void remove() {
			// undefined	
		}
	}

	private static class NaturalJoinIterator implements TupleIterator{
		
		TupleIterator outer;
		ResettableTupleIterator inner;
		ArrayList<Twoint> indices;
		Schema schema;
		Tuple currentOuter=null,currentInner=null;
		int[] fieldmap;
		boolean touched=false;
		Tuple next;
		/*
		public String toString(){
			return "NATURALJOIN( {"+inner.toString()+"} {"+outer.toString()+"} )";
		}*/
		
		class Twoint { 
			int iout; 
			int iin;
			Twoint(int o, int i){ iin=i; iout=o;}
		}
	
		public NaturalJoinIterator(TupleIterator outer, TupleIterator inner) {
			this.outer=new CachingIterator(outer); // not necessary except for debugging since it's only iterated once...
			this.inner=new CachingIterator(inner);
			// record indices in equivalent fields for quick comparison
			indices = new ArrayList<Twoint>();
			ArrayList<String> joinfields = new ArrayList<String>();
			try{
				for (String fout:outer.getSchema().getFieldNameIterable()){
					for (String fin:inner.getSchema().getFieldNameIterable()){
						if(fin.equals(fout)){
							if (!outer.getSchema().getFieldType(fin).equals(inner.getSchema().getFieldType(fin)))
								throw new JoinException("Error creating join schema: incompatible types on field "+fin);
							else{
								joinfields.add(fin);
								indices.add(new Twoint(outer.getSchema().getIntFromName(fin),
														inner.getSchema().getIntFromName(fin)));
							}
						}
					}
				}
				schema=outer.getSchema().getJoinSchema(inner.getSchema(), joinfields.toArray(new String[]{}));
			} catch (JoinException e){
				e.printStackTrace(System.out);
			}
			fieldmap=schema.joinSchemaMap;
		}

		public Schema getSchema() {
			return schema;
		}

		boolean matches(Tuple otuple, Tuple ituple){
			for(Twoint v:indices){
				if (!otuple.getValue(v.iout).equals(ituple.getValue(v.iin)))
					return false;
			}
			return true;
		}
		
		private Tuple makeTuple(){
			Tuple rval=schema.newTuple();
			int tmp;
			loop:
			for(int i=0;i<fieldmap.length;i++){
				if((fieldmap[i]&1024)==0){ // its in the outer tuple
					rval.setUncheckedValue(i, currentOuter.getValue(fieldmap[i]));
					continue loop;
				}
				if ((fieldmap[i]&1024)>=1024){ // its in the inner tuple
					tmp=fieldmap[i]&1023;
					rval.setUncheckedValue(i, currentInner.getValue(tmp));
					continue loop;
				}
			}
			return rval;	
		}
		
		private Tuple searchNext(){
			/* if we didn't just initialize, the currentO and currentI must have the value
			 * from which the previously returned tuple was made
			 *
			 * if current tuple in outer loop has a value and the inner isn't exhausted, we need to test
			 * the remaining elements in the inner iterator/loop...
			 */
			if(currentOuter!=null && inner.hasNext()){
				while (inner.hasNext()){
					currentInner=inner.next();
					if(matches(currentOuter,currentInner))
						return makeTuple();
				}
				// inner exhausted->reset and increment outer
				inner.reset();
			}
			/* 
			 * also, if not just initialized we know currentOuter !=null &&
			 * the inner has been reset, cf above; 
			 * in that case the outer must be incremented by one, and the search continued
			 * 
			 * if we just initialized, the outer must be initialized and search continued
			 * The code for these two cases are identical:
			 */
			//if(currentOuter==null && currentInner==null)
			while(outer.hasNext()){
				currentOuter=outer.next();
				while(inner.hasNext()){
					currentInner=inner.next();
					if(matches(currentOuter,currentInner))
						return makeTuple();
				}
				// inner loop exhausted, but no match found-> reset inner iterator and continue
				inner.reset();
			}
			// both the inner and outer loops must be exhausted->no more elements
			// thus we return null
			return null;
		}
		
		public boolean hasNext() {
			if(!touched){
				touched=true;
				next=searchNext();
			}
			return (next!=null);
		}

		public Tuple next() {
			if(!hasNext())
				throw new NoSuchElementException("No more elements in iterator");
			Tuple rval=next;
			next=searchNext();
			return rval;
		}

		public Iterator<Tuple> iterator() {
			return this;
		}

		public void remove() {
			// undefined	
		}
	}

	
	private static class SelectionIterator implements TupleIterator{
		
		TupleIterator source;
		TupleFilter filter;
		Tuple next=null;
		boolean touched=false;
		//boolean hasnextcalled=false;
		
		SelectionIterator(TupleIterator source, TupleFilter filter){
			this.source=source;
			this.filter=filter;
		}

		public Schema getSchema() {
			return source.getSchema();
		}
		
		Tuple searchNext(){
			Tuple rval=null, tmp;
			loop:
			while(source.hasNext()){
				tmp=source.next();
				if (filter.accepts(tmp)){
					rval=tmp;
					break loop;
				}
			}
			return rval;
						
		}
		
		public boolean hasNext() {
			if(!touched){
				touched=true;
				next=searchNext();
			}
			return (next!=null);
		}

		public Iterator<Tuple> iterator() {
			return this;
		}

		public Tuple next() {
			if(!hasNext())
				throw new NoSuchElementException("No more elements in iterator");
			Tuple rval=next;
			next=searchNext();
			return rval;
		}

		public void remove() {
			source.remove();
		}
	}
	
	private static class RenameIterator implements TupleIterator {

		TupleIterator source;
		Schema targetschema,sourceschema;
		
		public RenameIterator(TupleIterator source, Hashtable<String,String> map){
			this.source=source;
			sourceschema=source.getSchema();
			targetschema=sourceschema.getRenameSchema(map);	
		}
		
		public void remove() {
			source.remove();
		}

		public boolean hasNext() {
			return source.hasNext();
		}

		public Tuple next() {
			Tuple src=source.next();
			// we know they match because only names have changed and they're stored in the Schema
			// the values are the same, and they haven't been rearranged.
			src.setSchema(targetschema); 
			return src;
		}

		public Iterator<Tuple> iterator() {
			return this;
		}

		public Schema getSchema() {
			return targetschema;
		}
		
	}
	
	private static class ProjectionIterator implements TupleIterator{

		TupleIterator delegate;
		Schema targetschema;
		int[] mapping;
	
		
		ProjectionIterator(Schema newschema, TupleIterator source){
			this.delegate=source;
			targetschema=newschema;
			// find the mapping from each field in target to corresponding field in source
			mapping=new int[targetschema.size()];
			for(int i=0;i<mapping.length;i++){
				mapping[i]=source.getSchema().getIntFromName(targetschema.getNameFromInt(i));
			}
		}
		
		public boolean hasNext() {
			return delegate.hasNext();
		}

		public Tuple next() {
			Tuple src=delegate.next();
			Tuple target=targetschema.newTuple();
			for(int i=0;i<mapping.length;i++)
				try {
					target.setValue(i, src.getValue(mapping[i]));
				} catch (SchemaException e) {
					e.printStackTrace();
				}
			return target;
		}
		
		public String toString(){
			return super.toString()+"\n"+targetschema.toString();
		}

		public Schema getSchema() {
			return targetschema;
		}

		public Iterator<Tuple> iterator() {
			return this;
		}

		public void remove() {
			throw new NullPointerException("Operation not supported");
		}
		
	}
	/**
	 * Returns an iterator for the projection of tuples from the source iterator onto the given target schema.
	 * For each tuple t, its projection is a new tuple in the target schema, with each field in the target
	 * having the value from the field with the same name in the source tuple. The target schema has to be a subset
	 * of the source schema.
	 * @param newSchema the target schema
	 * @param source an iterator to retrieve source tuples from.
	 * @return
	 */
	 public static TupleIterator projectionIterator(Schema newschema, TupleIterator source){
		return new ProjectionIterator(newschema, source);
	 }
	 
	 public static TupleIterator renameIterator(Hashtable<String,String> map, TupleIterator source){
		 return new RenameIterator(source, map);
	 }
	 
	 public static TupleIterator projectionIterator(String[] fieldnames, TupleIterator source){
		 Schema s=new Schema("projection of "+source.getSchema().getName());
		 for (String name:fieldnames){
			 s.addField(name, source.getSchema().getFieldType(name));
		 }
		 return projectionIterator(s,source);
	 }
	 
	 public static TupleIterator selectionIterator(TupleIterator source, TupleFilter filter){
		 return new SelectionIterator(source, filter);
	 }
	 
	 public static TupleIterator equijoinIterator(String joinfieldname, TupleIterator outer, TupleIterator inner){
		 return new EquiJoinIterator(joinfieldname, outer, inner);
	 }
	public static TupleIterator naturaljoinIterator(TupleIterator tout,
			TupleIterator tin) {
		return new NaturalJoinIterator(tout,tin);
	}
}
