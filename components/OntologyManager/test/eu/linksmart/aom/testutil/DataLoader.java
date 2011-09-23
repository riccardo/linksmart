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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;

import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.rio.RDFFormat;
import org.xml.sax.InputSource;

import eu.linksmart.aom.repository.AOMRepository;

public class DataLoader {
  
  public static Element parse(String file){
    try{
      SAXBuilder builder = new SAXBuilder(false);
      return builder.build(new InputSource(new FileReader(file))).getRootElement();
    }
    catch(Exception e){
      e.printStackTrace();
    }
    return null;
  }
  
  public static Element parseString(String xml){
    try{
      SAXBuilder builder = new SAXBuilder(false);
      return builder.build(new InputSource(new StringReader(xml))).getRootElement();
    }
    catch(Exception e){
      e.printStackTrace();
    }
    return null;
  }
  
  public static String xmlToString(String file) {
    try{
      Reader in = new BufferedReader(new FileReader(new File(file)));

      StringBuilder sb = new StringBuilder();
      char[] chars = new char[1 << 16];
      int length;

      while ((length = in.read(chars)) > 0) {
        sb.append(chars, 0, length);
      }

      return sb.toString();
    }
    catch(Exception e){
      e.printStackTrace();
    }
    return "";
  }

  public static void loadModel(AOMRepository repo, String directory) {
    File file = new File(directory);
    File[] files = file.listFiles();
    RepositoryConnection conn = repo.getConnection();
    try {
      for(File f: files){
        if(!f.getName().equals(".svn")){
          conn.add(f, "", RDFFormat.RDFXML);
        }
      }
    } 
    catch (Exception e) {
      e.printStackTrace();
    }
    finally{
      try{
        conn.close();
      }
      catch(Exception ex){
        ex.printStackTrace();
      }
    }
  }
}
