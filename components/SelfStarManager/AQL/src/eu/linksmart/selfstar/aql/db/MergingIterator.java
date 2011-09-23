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

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * This class realizes merging of iterators, ie given a list of iterators it presents an iterator
 * that iterates through all alements and all iterators, akin to a sequential concatenation of streams..
 * @author ingstrup
 *
 * TODO : fix this class so that when empty iterators appear before non-empty ones,
 *  the complete set of elements will still be returned ...
 */
public class MergingIterator implements TupleIterator {

	
	TupleIterator[] iterators;
	int current=0;
	
	public MergingIterator(TupleIterator[] elements){
		iterators=elements.clone();
		current=0;
		Schema s = elements[0].getSchema();
		for (TupleIterator t:elements)
			if (!s.isMergeableWith(t.getSchema()))
				throw new RuntimeException("Attempting to create MergingIterator from iterators with incompatible schemas");
	}
	
	public Schema getSchema() {
		return iterators[0].getSchema();
	}

	/**
	 * if iterators ~ [empty, non-empty], current=0; -> true
	 * if iterators ~ [empty, empty, non-empty], current=0 -> false : wrong !
	 * 
	 */
	public boolean hasNext() {
		if (iterators==null || iterators.length==0)
			return false;
		if (iterators[current].hasNext())
			return true;
		else
			return inc2next();
			/*{
			if ((current+1)<iterators.length && iterators[current+1]!=null){
				return iterators[++current].hasNext();
			}
			else
				return false;
		}*/
	}
	
	// advance current to point to next non-empty iterator (and return true), or else return false,
	// meaning all iterators have been exhausted.
	private boolean inc2next(){
		while (true){
			if ((current+1)<iterators.length && iterators[current+1]!=null){
				current++;
				if (iterators[current].hasNext())
					return true;
				else
					continue;
			}
			else
				return false;
		}
	}
	
	public Iterator<Tuple> iterator() {
		return this;
	}

	public Tuple next() {
		if (iterators[current].hasNext())
			return iterators[current].next();
		else{
			if (inc2next()) // sets 'current' to next non-empty iterator, or returns false
				return iterators[current].next();
			else
				throw new NoSuchElementException();
		}
	}

	public void remove() {
		iterators[current].remove();
	}

}
