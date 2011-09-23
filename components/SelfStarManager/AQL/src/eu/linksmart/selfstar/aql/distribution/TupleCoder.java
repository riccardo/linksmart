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
package eu.linksmart.selfstar.aql.distribution;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;

import eu.linksmart.selfstar.aql.db.QueryTree;
import eu.linksmart.selfstar.aql.db.Schema;
import eu.linksmart.selfstar.aql.db.SchemaException;
import eu.linksmart.selfstar.aql.db.Table;
import eu.linksmart.selfstar.aql.db.Tuple;
import eu.linksmart.selfstar.aql.db.TupleIterator;

/**
 * This class encodes all tuples from an iterator into a byte array, and allows 
 * decoding into an iterator again.
 * 
 * @author ingstrup
 *
 */
public class TupleCoder {
		
	private static abstract class FieldReaderWriter { 

		DataOutputStream dout; 
		DataInputStream din;
		abstract void write(Object o) throws IOException;
		abstract Object read() throws IOException;
		void setOutputStream(DataOutputStream dos){ 
			 this.dout=dos;
		}
		
		void setInputStream(DataInputStream dis){
			this.din=dis;
		}
	}

	private static Hashtable<String,FieldReaderWriter> fieldreaderwriters;
	static {
		fieldreaderwriters = new Hashtable<String,FieldReaderWriter>(8);
		// strings
		fieldreaderwriters.put(String.class.getName(), new FieldReaderWriter() {
			void write(Object o) throws IOException{
				dout.writeUTF((String) o);
			}
			Object read() throws IOException {
				return din.readUTF();
			}
		});
		// integers
		fieldreaderwriters.put(Integer.class.getName(), new FieldReaderWriter() {
			void write(Object o) throws IOException{
				dout.writeInt((Integer) o);
			}
			Object read() throws IOException {
				return new Integer(din.readInt());
			}
		});
		// longs
		fieldreaderwriters.put(Long.class.getName(), new FieldReaderWriter() {
			void write(Object o) throws IOException{
				dout.writeLong((Long) o);
			}
			Object read() throws IOException {
				return new Long(din.readLong());
			}
		});
		// doubles
		fieldreaderwriters.put(Double.class.getName(), new FieldReaderWriter() {
			void write(Object o) throws IOException{
				dout.writeDouble((Double) o);
			}
			Object read() throws IOException {
				return new Double(din.readDouble());
			}
		});
		// floats
		fieldreaderwriters.put(Float.class.getName(), new FieldReaderWriter() {
			void write(Object o) throws IOException{
				dout.writeFloat((Float) o);
			}
			Object read() throws IOException {
				return new Float(din.readFloat());
			}
		});
		// bytes
		fieldreaderwriters.put(Byte.class.getName(), new FieldReaderWriter() {
			void write(Object o) throws IOException{
				dout.writeByte((Byte) o);
			}
			Object read() throws IOException {
				return new Byte(din.readByte());
			}
		});
		// chars
		fieldreaderwriters.put(Character.class.getName(), new FieldReaderWriter() {
			void write(Object o) throws IOException{
				dout.writeChar(((Character) o));
			}
			Object read() throws IOException {
				return new Character(din.readChar());
			}
		});
		// booleans
		fieldreaderwriters.put(Boolean.class.getName(), new FieldReaderWriter() {
			void write(Object o) throws IOException{
				dout.writeBoolean((Boolean) o);
			}
			Object read() throws IOException {
				return new Boolean(din.readBoolean());
			}
		});
		// shorts
		fieldreaderwriters.put(Short.class.getName(), new FieldReaderWriter() {
			void write(Object o) throws IOException{
				dout.writeShort((Short) o);
			}
			Object read() throws IOException {
				return new Short(din.readShort());
			}
		});
		
	}
	
	/**
	 * Encodes the given table to a byte array. The fields in the table must be of one of the 
	 * types String, Integer, Byte, Boolean, Character, Long, Short, Double or Float. 
	 * @param t the table to be encoded
	 * @return a byte array with the encoded data
	 * @throws IOException
	 */
	
	public static byte[] encode(Table t) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos=new DataOutputStream(bos);
		// write schema: field names and class names
		Schema s = t.getSchema();
		//schema name, fieldcount as int, tuplecount as int
		dos.writeUTF(s.getName());
		dos.writeInt(s.size());
		dos.writeInt(t.getRawTable().size());
		// for each column, write name, type
		Iterator<String> fn = s.fieldNameIterator();
		while(fn.hasNext()){
			String classname,name=fn.next();
			classname=s.getFieldType(name).getName();		
			dos.writeUTF(name);
			dos.writeUTF(classname);
			dos.writeInt(s.getFieldWidth(name));
		}
		fn = s.fieldNameIterator();
		while(fn.hasNext()){
			String classname,name=fn.next();
			classname=s.getFieldType(name).getName();		
			// write data for this column, using the FWriter that can handle the column class
			FieldReaderWriter fw= fieldreaderwriters.get(classname);
			fw.setOutputStream(dos);
			int fieldno=s.getIntFromName(name);
			for (Tuple tu:t.getRawTable()){
				fw.write(tu.getValue(fieldno));
			}
		}		
		dos.close();// flushes dos recursively, closes recursively
		return bos.toByteArray();	
	}
	
	/**
	 * Decodes the data in a byte array into a table, using the decodeTable(...) method.
	 * @param buffer the byte array containing the encoded data from a flamenco.aql.db.Table instance
	 * @return An iterator over the elements in the table encoded in the byte array.
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws SchemaException
	 * @see {@link TupleCoder#decodeTable(byte[])}
	 */
	public static TupleIterator decode(byte buffer[]) throws IOException, ClassNotFoundException, SchemaException{
		return decodeTable(buffer).iterator();
	}
	
	static int decodedcount=0;
	
	/**
	 * Decodes table data from a byte array into a table.
	 * @param buffer
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws SchemaException
	 * @see {@link TupleCoder#decode(byte[])}
	 */
	@SuppressWarnings({ "static-access", "rawtypes" })
	public static Table decodeTable(byte buffer[]) throws IOException, ClassNotFoundException, SchemaException{
		//ByteArrayInputStream bis= new ByteArrayInputStream(buffer);
		DataInputStream dis=new DataInputStream(new ByteArrayInputStream(buffer));
		String schemaname=dis.readUTF();
		int fieldcount=dis.readInt();
		int tuplecount=dis.readInt();
		Schema s = new Schema(schemaname+"decoded"+decodedcount++);
		// read fields for the schema
		String fieldname,classname;
		Class type;
		for (int i=0;i<fieldcount;i++){
			fieldname=dis.readUTF();
			classname=dis.readUTF();
			int width=dis.readInt();
			type=classname.getClass().forName(classname);
			s.addField(fieldname, type, width);
		}
		// make the tuple objects we need in a table - values are filled in next, one column at a time.
		Table tbuffer=new Table(s,tuplecount);
		for (int i=0;i<tuplecount;i++)
			tbuffer.addTuple();
		// now read the data, one column at a time in the order given by the schema.
		for (int f=0;f<fieldcount;f++){
			FieldReaderWriter reader=fieldreaderwriters.get(s.getFieldType(f).getName());
			reader.setInputStream(dis);
			for(int t=0;t<tuplecount;t++){
				tbuffer.getTupleAt(t).setValue(f, reader.read());
			}
		}
		dis.close();
		return tbuffer;
	}

	public static QueryTree decode_query(byte[] buffer) throws Exception {
		DataInputStream dis=new DataInputStream(new ByteArrayInputStream(buffer));
		String qstring = dis.readUTF();
		return QueryTree.parseQuery(qstring);
	}
	
	/**
	 * Encodes a query into a byte array.
	 * @param q the query to encode
	 * @return the byte array containing the encoded query
	 * @throws Exception
	 */
	public static byte[] encode_query(QueryTree q) throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos=new DataOutputStream(bos);
		// write schema: field names and class names
		String qstring = q.toString();
		//schema name, fieldcount as int, tuplecount as int
		dos.writeUTF(qstring);
		dos.close();// flushes dos recursively, closes recursively
		return bos.toByteArray();
	}
	
	/**
	 * Encodes all tuples from an iterator into a buffer. All tuples are retrieved
	 * from the iterator and stored in a table, which is encoded with the 
	 * encode(Table t) method.
	 * 
	 * @param t
	 * @return
	 * @throws Exception
	 * 
	 * @see {@link TupleCoder#encode(Table)}
	 */
	public static byte[] encode(TupleIterator t) throws Exception{
		Table buffer = new Table(null,t.getSchema(),10);
		while (t.hasNext())
			buffer.addTuple(t.next());
		return encode(buffer);
	}

}






