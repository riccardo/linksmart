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
 * Copyright (C) 2006-2010 Technical University of Kosice
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

package eu.linksmart.aom.repository;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.openrdf.query.Binding;
import org.openrdf.query.BindingSet;

/**
 * Class responsible for holding the SPARQL results and access to the retrieved data.
 * 
 * @author Peter Kostelnik
 *
 */
public class SPARQLResult {
	Map<String, String> binding = new HashMap<String, String>();
	
	public SPARQLResult(BindingSet binding){
		Iterator<Binding> i = binding.iterator();
		while(i.hasNext()){
			Binding b = i.next();
			this.binding.put(b.getName(), b.getValue().stringValue());
		}
	}

	/**
	 * Returns the value of the result binding associated to the variable.
	 * 
	 * @param var Variable name.
	 * @return Value.
	 */
	public String value(String var){
		return this.binding.get(var);
	}

	@Override 
	public String toString(){
		String out = "\n[SPARQLResult: \n";
		Iterator<String> i = this.binding.keySet().iterator();
		while(i.hasNext()){
			String key = i.next();
			out += "  ["+key+":"+value(key)+"]\n";
		}
		return out+"]";
	}
}
