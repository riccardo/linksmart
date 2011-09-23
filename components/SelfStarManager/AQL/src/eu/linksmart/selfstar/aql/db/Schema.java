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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

@SuppressWarnings("rawtypes")
public class Schema {
	
	
	String name;
	ArrayList<SchemaField> fields;
	private Hashtable<Integer,String> int2fname=new Hashtable<Integer,String>(10);
	private Hashtable<String,Integer> fname2int=new Hashtable<String,Integer>(10);
	
	public static class SchemaField{
		public String name;
		public int index;
		public Class type;
		public int width;
		
		
		public SchemaField(String name, Class type, int index){
			this(name,type,index,-1);
		}
		public SchemaField(String name, Class type, int index, int width){
			this.name=name;
			this.type=type;
			this.index=index;
			if(width<0)
				this.width=1+(type.getSimpleName().length()>name.length()?type.getSimpleName().length():name.length());
			else
				this.width=width;
		}
	}
	
	int[] joinSchemaMap;
	
	int[] getJoinSchemaMap(){
		return joinSchemaMap;
	}
	
	/**
	 * 
	 * Also sets the joinSchemaMap for the returned schema. It's an int array, im, for the fields: if field i in the new schema corresponds to field j
	 * in the outer schema, then im[i]=j, if it corresponds to j in the inner schema, then 
	 * im[i]=1024+j; 
	 * @return the fieldmap
	 */

	Schema getJoinSchema(Schema inner, String[] joinnames) throws JoinException{
		Schema rval=new Schema("joinautoschema");
		ArrayList<Integer> jsm=new ArrayList<Integer>();
		//int fieldcount=0;
		for (SchemaField f:fields){
			rval.addField(f.name, f.type);
			jsm.add(getIntFromName(f.name));// what number: this is the outer, so just the field's index in this schema
		}
		outer:
		for (SchemaField f:inner.fields){
			for(String n:joinnames)
				if(n.equals(f.name)){ // cannot have same name and different types, since the name is used to retrieve the field
					if (!getFieldType(n).equals(inner.getFieldType(n)))
						throw new JoinException("Error creating join schema: incompatible types on field "+name);
					continue outer; // skip bc added before
				}
		rval.addField(f.name, f.type);
		jsm.add(1024+inner.getIntFromName(f.name));
		}
		rval.joinSchemaMap=new int[jsm.size()];
		for(int i=0;i<jsm.size();i++)
			rval.joinSchemaMap[i]=jsm.get(i);
		return rval;
	}


	public Schema getRenameSchema(Hashtable<String,String> map){
		Schema rval = new Schema("Renamed"+this.name);
		String name;
		for (SchemaField f:fields){
			name=f.name;
			if (map.containsKey(name))
				name=map.get(name);
			rval.addField(name, f.type,f.width);
		}
		return rval;
	}
	
	public Class getFieldType(String name){
		return getFieldType(getIntFromName(name));
	}
	
	private class FieldNameIterator implements Iterator<String>{

		Iterator<SchemaField> ii=fields.iterator();
		
		
		public boolean hasNext() {
			return ii.hasNext();
		}

		public String next() {
			return ii.next().name;
		}

		public void remove() {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	public Schema(String name){
		this.name=name;
		fields=new ArrayList<SchemaField>(5);
	}
	
	public int addField(String name, Class type){
		int index=fields.size();
		SchemaField f=new SchemaField(name,type,index);
		fields.add(f);
		int2fname.put(index, name);
		fname2int.put(name, index);
		return index;
	}
	
	public int addField(String name,Class type, int width){
		int f=addField(name,type);
		getField(f).width=width;
		return f;
	}
	
	protected SchemaField getField(int i){
		return fields.get(i);
	}
	
	public Schema getSubSchema(int[] fields2include) throws SchemaException{
		Schema s = new Schema("subschema");
		for(int i:fields2include)
			try{
				s.addField(getField(i).name, getField(i).type);
			} catch (ArrayIndexOutOfBoundsException a){
				throw new SchemaException("Invalid field index",a);
			}
		return s;
	}
	
	/**
	 * Get the number of fields in this schema.
	 * @return int representign the number of fields.
	 */
	public int size(){
		return fields.size();
	}
	
	public int getIntFromName(String fieldname){
		//System.out.println("name: >"+fieldname+"< looking in "+fname2int);
		return fname2int.get(fieldname);
	}
	
	public String getNameFromInt(int fieldindex){
		return int2fname.get(fieldindex);
	}
	
	public Class getFieldType(int index){
		return fields.get(index).type;
	}
	
	public int getFieldWidth(int index){
		return fields.get(index).width;
	}
	
	public int getFieldWidth(String name){
		return getFieldWidth(getIntFromName(name));
	}
	
	public Tuple newTuple(){
		return new Tuple(this);
	}
	
	public String toString(){
		StringWriter sw=new StringWriter();
		PrintWriter b = new PrintWriter(sw);
		
		//StringBuilder b=new StringBuilder("Schema:"+name+"{\n ");
		for(SchemaField f:fields){
			b.append("\t"+f.name+" : "+f.type.toString()+"\n");
			
		}
		b.append("}");
		b.flush();
		return sw.getBuffer().toString();
	}
	
	public Iterator<String> fieldNameIterator(){
		return new FieldNameIterator();
	}
	
	public Iterable<String> getFieldNameIterable(){
		return new Iterable<String>(){

			public Iterator<String> iterator() {
				return fieldNameIterator();
			}
			
		};
	}

	public String getName() {
		return name;
	}
	
	public boolean isMergeableWith(Schema other){
		boolean rval= this.fields.size()==other.fields.size();
		if (!rval)
			return rval; // false
		for (int i=0;i<fields.size();i++){
			SchemaField f1=fields.get(i),
				f2=other.fields.get(i);
			rval &= (f1.name.equals(f2.name));
			rval &= (f1.type.equals(f2.type));
			rval &= (f1.index==f2.index);
			if (!rval) // make it faster for non-matching schemas.
				return rval;//false
		}	
		return rval;
	}
}
