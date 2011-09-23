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
 * Copyright (C) 2006-2010 [University of Paderborn]
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

package eu.linksmart.storage.helper;

import java.io.IOException;

import org.jdom.Element;
import org.jdom.JDOMException;

/**
 * @author Felix Dickehage <skyfox@mail.uni-paderborn.de> as SkyfoxM on 14.05.2009
 *
 */
public class LongResponse extends Response {
	
	public final static String ROOT_TYPE = "longResponse"; 

	private Long result;

	public LongResponse(int errorCode, String errorMessage, Long result) {
		super(ROOT_TYPE, errorCode, errorMessage);
		this.result = result;
	}
	
	public LongResponse(String xmlData) throws JDOMException, IOException {
		super(ROOT_TYPE, xmlData);
	}

	/**
     * Gets the value value for this IntegerResponse.
     * 
     * @return value
     */
    public long getResult() {
        return result;
    }

	@Override
	protected void readResult(Element e) {
		String value = e.getAttributeValue("value");
		if (value != null)
			result = new Long(value);
	}

	@Override
	protected void writeResult(Element e) {
		if (result != null)
			e.setAttribute("value", result.toString());
	}

    
}
