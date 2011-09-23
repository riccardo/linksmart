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

public class CachingIterator implements ResettableTupleIterator {

	boolean firstrun;
	Schema schema;
	Table cache;
	TupleIterator source;
	boolean empty;
	
	public CachingIterator(TupleIterator source){
		schema=source.getSchema();
		firstrun=true;
		cache=new Table(schema);
		this.source=source;
		empty=(!source.hasNext());
	}
	
	public Schema getSchema() {
		return schema;
	}

	public boolean hasNext() {
		return source.hasNext();
	}

	public Iterator<Tuple> iterator() {
		return this;
	}

	public Tuple next() {
		if (!source.hasNext())
			throw new NoSuchElementException("This iterator has no more elements, but it's resettable;");
		Tuple rval=source.next();
		if(firstrun)
			cache.addTuple(rval);
		return rval;
	}

	public void remove() {
		source.remove();
	}

	
	public String toString(){
		if (!hasNext()){ // check if it's an iterator over the empty set of tuples
			reset();
			if (!hasNext()) // it is empty...
				return "CACHINGITERATOR { }";
		}
		// it's not empty..
		StringBuilder b=new StringBuilder();
		Tuple current=null,next,tmp=null;
		next = next();
		// find previous so we can iterate and go back to current state..
		reset();
		// set current to point at the element before the one held by next so we can go back to the same state
		while(tmp != next){
			current = tmp;
			tmp=next();
			if (!hasNext()) // start over if we reached the end...
				reset();
		}
		reset();
		while(hasNext())
			b.append(next().toString()+", ");
		reset();
		while(hasNext())
			if (next()==current)
				break;
		return "CACHINGITERATOR { "+b.toString()+" }";
	}
	
	public void reset() {
		// if empty, do nothing
		if (empty)
			return;
		/* if it's the first run, this method might be called before 
		 * the initial source iterator has been exhausted
		 * in that case we add all remaining elements to the cache before 
		 * continuing
		 */
		if(firstrun){
			while(hasNext())
				next();
			firstrun=false;
		}
		source=cache.iterator();
	}
}