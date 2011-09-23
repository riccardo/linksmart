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

package eu.linksmart.aom.processor;

import java.util.List;

import org.jdom.Element;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;

import eu.linksmart.aom.ontology.Graph;
import eu.linksmart.aom.ontology.model.Namespace;
import eu.linksmart.aom.ontology.model.Rdf;
import eu.linksmart.aom.ontology.util.ResourceUtil;
import eu.linksmart.aom.repository.AOMRepository;

public class FormDataProcessor extends Processor{

  public FormDataProcessor(AOMRepository repository) {
    super(repository);
  }

  public String store(String xml, boolean append) {
    Element formData = parse(xml);
    String rdftype = formData.getChildTextTrim("rdftype");
    String newInstanceURI = repository.getURI(rdftype);
    
    Element parent = formData.getChild("parent");

    ValueFactory f = repository.getValueFactory();
    Graph g;
    if (parent != null){

      String parentURI = parent.getChildTextTrim("uri");
      String parentRelation = parent.getChildTextTrim("relation");
      if (!append){
        try {
          RepositoryConnection conn = repository.getConnection();
          RepositoryResult<Statement> result = conn.getStatements(new URIImpl(parentURI), new URIImpl(parentRelation), (Value)null, false);
          while(result.hasNext()){
            Statement stmt = result.next();
            Value obj = stmt.getObject();
            if (obj instanceof URI){
              URI oURI = (URI)obj;
              repository.remove(parentURI, parentRelation, oURI.stringValue());
            }
          }
        } catch (RepositoryException e) {
          e.printStackTrace();
        }
      }
      
      g = new Graph(parentURI);

      g.add(parentURI, parentRelation, newInstanceURI, f);
    } else {
      g = new Graph(newInstanceURI);
    }

    g.add(newInstanceURI, Rdf.rdfType.stringValue(), rdftype, f);

    Element properties = formData.getChild("properties");
    if (properties != null){
      for(Element property: ((List<Element>)properties.getChildren("property"))){
        String name = property.getChildTextTrim("name");
        String value = property.getChildTextTrim("value");
        String dataType = property.getChildTextTrim("dataType");
        g.add(ResourceUtil.statement(newInstanceURI, name, value, Namespace.dataTypeURI(dataType), f));
      }
    }
    repository.store(g);
    return newInstanceURI;
  }
}
