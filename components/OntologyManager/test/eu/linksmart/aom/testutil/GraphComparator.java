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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.URIImpl;

import eu.linksmart.aom.ontology.Graph;
import eu.linksmart.aom.ontology.model.Namespace;
import eu.linksmart.aom.repository.AOMRepository;

public class GraphComparator {
  Graph g1;
  Graph g2;
  AOMRepository repo;
  
  private GraphComparator(Graph g1, Graph g2, AOMRepository repo){
    this.g1 = g1;
    this.g2 = g2;
    this.repo = repo;
  }
  
  private <T> List<T> i2l(Iterator<T> i){
    List<T> l = new ArrayList<T>();
    while(i.hasNext()){
      l.add(i.next());
    }
    return l;
  }

  private boolean match(Statement stmt1, Statement stmt2, Set<String> f1, Set<String> f2){
    if (stmt1.getPredicate().equals(stmt2.getPredicate())){
      if ((stmt1.getObject().equals(stmt2.getObject()) && (stmt1.getObject() instanceof Literal)) ||
          (stmt1.getObject().equals(stmt2.getObject()) && repo.isInStaticModel(stmt1.getObject())) ||
          (stmt1.getObject().equals(stmt2.getObject()) && Namespace.isStaticProperty(stmt1.getPredicate()))
          )
        return true; 
      else {
        if (Namespace.isStaticProperty(stmt1.getPredicate())) return false;
        if (repo.isInStaticModel(stmt1.getObject()) || repo.isInStaticModel(stmt2.getObject())) return false;
        if ((!(stmt1.getObject() instanceof Literal)) &&
            (!(stmt2.getObject() instanceof Literal)))
        {
          String obj1 = stmt1.getObject().stringValue();
          String obj2 = stmt2.getObject().stringValue();
          
          if (f1.contains(obj1) || f2.contains(obj2)) return false;
          else {
            Set<String> cf1 = clone(f1);
            Set<String> cf2 = clone(f2);
            cf1.add(obj1);
            cf2.add(obj2);
            if (sameAs(new URIImpl(obj1), new URIImpl(obj2), cf1, cf2)){
              f1.addAll(cf1);
              f2.addAll(cf2);
              return true;
            } else return false;
          }
        } else return false;
      }
    } else return false;
  }
  
  public boolean sameAs(Statement stmt1, List<Statement> stmts1, Statement stmt2, List<Statement> stmts2, Set<String> f1, Set<String> f2) {
    if (match(stmt1, stmt2, f1, f2)) {
      if (sameAs(stmts1, stmts2, f1, f2)){
        return true;
      } else return false;
    } else {
      return false;
    }
  }
  
  public <T> List<T> clone(List<T> list){
    List<T> result = new ArrayList<T>();
    for(T item: list){
      result.add(item);
    }
    return result;
  }
  
  public <T> Set<T> clone(Set<T> list){
    Set<T> result = new HashSet<T>();
    for(T item: list){
      result.add(item);
    }
    return result;
  }
  
  public boolean sameAs(List<Statement> stmts1, List<Statement> stmts2, Set<String> f1, Set<String> f2) {
    if ((stmts1.size() == 0) && (stmts2.size() == 0)) return true;
    for(int i = 0; i < stmts1.size(); i++){
      for(int j = 0; j < stmts2.size(); j++){
        List<Statement> cstmts1 = clone(stmts1); 
        List<Statement> cstmts2 = clone(stmts2);
        if (sameAs(cstmts1.remove(i), cstmts1, cstmts2.remove(j), cstmts2, f1, f2)) return true;
      }
    }
    return false;
  }
  
  public boolean sameAs(Resource subj1, Resource subj2, Set<String> f1, Set<String> f2) {
    Iterator<Statement> stmts1 = g1.match(subj1, null, null);
    Iterator<Statement> stmts2 = g2.match(subj2, null, null);
    return sameAs(i2l(stmts1), i2l(stmts2), f1, f2);
  }

  public boolean areSame() {
    Set<String> f1 = new HashSet<String>();
    Set<String> f2 = new HashSet<String>();
    f1.add(g1.getBaseURI());
    f2.add(g2.getBaseURI());
    return sameAs(new URIImpl(g1.getBaseURI()), new URIImpl(g2.getBaseURI()), f1, f2);
  }
  
  public static boolean sameAs(Graph g1, Graph g2, AOMRepository repo) {
    return (new GraphComparator(g1, g2, repo)).areSame();
  }
}