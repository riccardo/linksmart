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
import java.util.Collections;
import java.util.Comparator;

import eu.linksmart.selfstar.aql.db.Schema.SchemaField;

@SuppressWarnings("rawtypes") // the raw type Comparator is used extensively
public class Table implements TableRegistry.IteratorFactory{
	
	ArrayList<Tuple> tuples;
	Schema schema;
	String name;
	
	public Table(Schema s){
		this(null,s);
	}
	
	public Table(String name, Schema s){
		this(name,s,10);
	}
	
	public Table(Schema s, int initialcapacity){
		this(null, s,initialcapacity);
	}
	
	public Table(String name, Schema schema, int initialcapacity){
		tuples=new ArrayList<Tuple>(initialcapacity);
		this.schema=schema;
		this.name=name;
		if (name!=null)
			TableRegistry.getInstance().registerTable(this);
	}
	
	public ArrayList<Tuple> getRawTable(){
		return tuples;
	}
	
	public String getName(){
		return name;
	}
	
	private class TupleComparator implements Comparator<Tuple>{

		Comparator delegate;
		int fieldindex;

		
		public TupleComparator(Comparator t, int fieldindex){
			delegate=t;
			this.fieldindex=fieldindex;
		}
		
		public void setDelegate(Comparator delegate){
			this.delegate=delegate;
		}
		
		public void setFieldIndex(int index){
			this.fieldindex=index;
		}
		
		@SuppressWarnings("unchecked")
		public int compare(Tuple t1, Tuple t2) {
			return delegate.compare(t1.getValue(fieldindex), t2.getValue(fieldindex));
		}
		
	}
	
	boolean isSorted=false;
	TupleComparator sortcomparator;
	int sortkey;
	
	public void setSorted(boolean sorted, int keyindex, Comparator fieldComparator){
		if (sorted){ // if it's sorted but wasn't before, or if it was but needs to be on a new key or comparator...
			sortkey=keyindex;
			if (sortcomparator==null)
				sortcomparator=new TupleComparator(fieldComparator,keyindex);
			else {
				sortcomparator.setDelegate(fieldComparator);
				sortcomparator.setFieldIndex(keyindex);
			}
			sort();
			isSorted=true;
		} else
			isSorted=false;
		
	}
	/*
	private int getInsertionPoint(Tuple t){
		//TODO
		//FIXME
		if(!isSorted)
			return tuples.size();
		/* four cases: 
		 * 1. the tuple (the same object) is already in the table,
		 * 2. a tuple with the same values is already in the table (impossible if we use unique keys...)
		 * 3. a tuple with the same value for the given field, but otherwise different exists
		 * 4. no tuples with the same value in the given field exists: i is negative 
		 * /
		int i= Collections.binarySearch(tuples, t, sortcomparator);
		if (i<0) // case 4:	
			return -i;
		else
			return i; // 1,2,3
	}
	*/
	
	private void sort(){
		Collections.sort(tuples, sortcomparator);
	}
	
	public Tuple addTuple(){
		Tuple t=schema.newTuple();
		tuples.add(t);
		return t;
	}
	
	public boolean addTuple(Tuple t){
		if(t.getSchema()!=this.schema)
			return false;
		tuples.add(t);
		return true;
	}
	
	public Table selectElements(int[] fields2include, int field2compare, Comparable value){
		Table result=null;
		// first compute schema for new table:
		Schema newschema=null;
		try {
			newschema = schema.getSubSchema(fields2include);
		} catch (SchemaException e) {
			e.printStackTrace();
		}
		result=new Table(newschema);
		for(Tuple t:tuples){
			if (value.equals(t.getValue(field2compare))){
				Tuple rt= new Tuple(newschema);
				for(int f=0;f<fields2include.length;f++)
					try {
						rt.setValue(f, t.getValue(fields2include[f]));
					} catch (SchemaException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				result.tuples.add(rt);
			}
		}
		return result;
	}
	
	public Tuple getTupleAt(int index){
		return tuples.get(index);
	}
	
	public int size(){
		return tuples.size();
	}
	public String toTabularString(){
		StringBuffer b=new StringBuffer("\n");
		int totalwidth=0;
		for(SchemaField f:schema.fields){
			b.append(String.format("%1$-"+f.width+"."+f.width+"s",f.name));
			totalwidth+=f.width;
		}
		b.append("\n");
		for(SchemaField f:schema.fields)
			b.append(String.format("%1$-"+f.width+"."+f.width+"s",f.type.getSimpleName()));
		b.append("\n");
		while(totalwidth-->0)
			b.append("-");
		b.append("\n");
		for(Tuple t:tuples){
			for(int i=0;i<t.getSize();i++){
				int w=t.getSchema().getField(i).width;
				b.append(String.format("%1$-"+w+"."+w+"s",t.getValue(i)));
			}
			b.append("\n");
		}
		return b.toString();
	}
	
	public static void printIteratorAsTable(TupleIterator ti){
			StringBuffer b=new StringBuffer("\n");
			int totalwidth=0;
			for(SchemaField f:ti.getSchema().fields){
				b.append(String.format("%1$-"+f.width+"."+f.width+"s",f.name));
				totalwidth+=f.width;
			}
			b.append("\n");
			for(SchemaField f:ti.getSchema().fields)
				b.append(String.format("%1$-"+f.width+"."+f.width+"s",f.type.getSimpleName()));
			b.append("\n");
			while(totalwidth-->0)
				b.append("-");
			b.append("\n");
			for(Tuple t:ti){
				for(int i=0;i<t.getSize();i++){
					int w=t.getSchema().getField(i).width;
					b.append(String.format("%1$-"+w+"."+w+"s",t.getValue(i)));
				}
				b.append("\n");
			}
			System.out.println("Warning: "+new Throwable().getStackTrace()[0].getClassName()+".printIteratorAsTable(ti) consumes tuples from the iterator, so it can't be used again (unless it's resettable and is reset).");
			System.out.println(b.toString());
	}
	
	public TupleIterator iterator(){
		return new TupleIteratorWrapper(tuples.iterator(),schema);
	}
	
	public Schema getSchema(){
		return schema;
	}
	
	public boolean removeTuple(Tuple t){
		return tuples.remove(t);
	}
	
	public static void main(String args[]){
		System.out.println("file:/Users/ingstrup/Documents/workspaces/LinkSmart2/flamenco_testconfiguration/aql_configuration/bundle/org.apache.felix.http.jetty-0.9.0-SNAPSHOT.jar".length());
		System.out.println(String.format("%1$-30.30s<end", "org.apache.felix.configadmin"));
	}

	public void reset() {
		tuples.clear();
	}
	
}
