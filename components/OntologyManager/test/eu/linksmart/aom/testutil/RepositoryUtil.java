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

import java.io.File;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryResult;

import eu.linksmart.aom.ontology.model.Device;
import eu.linksmart.aom.ontology.model.Rdf;
import eu.linksmart.aom.repository.AOMRepository;

public class RepositoryUtil {
  public static void listRepository(AOMRepository repo){
    RepositoryConnection conn = repo.getConnection();
    try{
      System.out.println("\n\nREPOSITORY CONTEXTS:: ");
      RepositoryResult<Resource> ctxs = conn.getContextIDs();
      while(ctxs.hasNext()){
        System.out.println("> "+ctxs.next());
      }
      System.out.println("\n\nREPOSITORY CONTENT:: ");
      RepositoryResult<Statement> result = conn.getStatements(null, null, null, false);
      while(result.hasNext()){
        Statement st = result.next();
        System.out.println("["+st.getSubject()+":"+st.getPredicate()+":"+st.getObject()+":"+st.getContext()+"]");
      }
    }
    catch(Exception e){
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

  private static void showIsTemplate(RepositoryConnection conn, Resource device) throws Exception {
    RepositoryResult<Statement> result = conn.getStatements(device, Device.isDeviceTemplate, null, false);
    while(result.hasNext()){
      Statement st = result.next();
      System.out.println("  > isTemplate: "+st.getObject());
    }
  }
  private static void showPID(RepositoryConnection conn, Resource device) throws Exception {
    RepositoryResult<Statement> result = conn.getStatements(device, Device.PID, null, false);
    if(result.hasNext()){
      while(result.hasNext()){
        Statement st = result.next();
        System.out.println("  > PID: "+st.getObject());
      }
    }
  }
  public static void listDevices(AOMRepository repo){
    RepositoryConnection conn = repo.getConnection();
    try{
      int count = 0;
      System.out.println("\n\nREPOSITORY PHYSICAL DEVICES:: ");
      RepositoryResult<Statement> result = conn.getStatements(null, Rdf.rdfType, Device.PhysicalDevice, true);
      while(result.hasNext()){
        Statement st = result.next();
        System.out.println("\n> "+st.getSubject());
        showIsTemplate(conn, st.getSubject());
        showPID(conn, st.getSubject());
        count++;
      }
      System.out.println("\nREPOSITORY PHYSICAL DEVICES:: "+count);
      
      count = 0;
      System.out.println("\n\nREPOSITORY SEMANTIC DEVICES:: ");
      result = conn.getStatements(null, Rdf.rdfType, Device.SemanticDevice, true);
      while(result.hasNext()){
        Statement st = result.next();
        System.out.println("> "+st.getSubject());
        showIsTemplate(conn, st.getSubject());
        showPID(conn, st.getSubject());
        RepositoryResult<Statement> d = conn.getStatements(st.getSubject(), Device.hasDiscoveryInfo, null, true);
        count++;
      }
      System.out.println("\nREPOSITORY SEMANTIC DEVICES:: "+count);
      
    }
    catch(Exception e){
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

  public static long repositorySize(AOMRepository repo){
    RepositoryConnection conn = repo.getConnection();
    try{
      return conn.size();
    }
    catch(Exception e){
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
    return -1;
  }

  public static void clean(String folder){
    clean(new File(folder));
  }

  public static void clean(File folder){ 
    if (folder.isDirectory()) { 
      String[] cnt = folder.list(); 
      for (int i = 0; i < cnt.length; i++) { 
        clean(new File(folder, cnt[i]));
      } 
    } 
    folder.delete();  
  }
  
  public static void query(AOMRepository repo, String query){
    RepositoryConnection conn = repo.getConnection();
    try{
      System.out.println("\n\nREPOSITORY QUERY:: ");
      TupleQueryResult results = repo.sparqlQuery(conn, query);
      while(results.hasNext()){
        System.out.println("> "+results.next());
      }
    }
    catch(Exception e){
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
