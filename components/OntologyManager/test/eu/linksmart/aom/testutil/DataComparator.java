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

package eu.linksmart.aom.testutil;

import java.io.StringReader;
import java.util.Iterator;
import java.util.List;

import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.openrdf.model.Statement;

public class DataComparator {

  public Element readXML(String xml){
    try{
      return new SAXBuilder().build(new StringReader(xml)).getRootElement();
    }
    catch(Exception e){
      e.printStackTrace();
    }
    return null;
  }

  public boolean exists(Attribute a, List<Attribute> as){
    Iterator<Attribute> i = as.iterator();
    while(i.hasNext()){
      Attribute a1 = i.next();
      if(a1.getName().equals(a.getName()) &&
          a1.getValue().equals(a.getValue())) return true;
    }
    return false;
  }
  public boolean sameAttributes(List<Attribute> as1, List<Attribute> as2){
    if(as1.size() == as2.size()){
      Iterator<Attribute> i1 = as1.iterator();
      while(i1.hasNext()){
        Attribute a1 = i1.next();
        if(!exists(a1, as2)) return false;
      }
      return true;
    }
    return false;
  }

  public boolean exists(Element e, List<Element> es){
    Iterator<Element> i = es.iterator();
    while(i.hasNext()){
      Element e1 = i.next();
      if(sameAs(e1, e)) return true;
    }
    return false;
  }

  public boolean sameChildren(List<Element> es1, List<Element> es2){
    if(es1.size() == es2.size()){
      Iterator<Element> i1 = es1.iterator();
      while(i1.hasNext()){
        Element e1 = i1.next();
        if(!exists(e1, es2)) return false;
      }
      return true;
    }
    return false;
  }

  public boolean sameAs(Element e1, Element e2){
    if(e1.getName().equals(e2.getName()) && 
        e1.getNamespace().equals(e2.getNamespace()) &&
        e1.getTextTrim().equals(e2.getTextTrim())){
      if( sameAttributes(e1.getAttributes(), e2.getAttributes())) 
        return sameChildren(e1.getChildren(), e2.getChildren());
    }
    return false;
  }

  public boolean sameAs(String xml1, String xml2){
    return sameAs(readXML(xml1), readXML(xml2));
  }

  public void show(Iterator<Statement> stmts){
    while(stmts.hasNext()){
      System.out.println("STMT :: " + stmts.next());
    }
  }
}
