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

package eu.linksmart.aom.performance;

import java.io.File;
import java.util.Set;

import org.junit.Test;
import org.openrdf.model.vocabulary.XMLSchema;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;

import eu.linksmart.aom.ontology.Graph;
import eu.linksmart.aom.ontology.model.Device;
import eu.linksmart.aom.ontology.model.Namespace;
import eu.linksmart.aom.ontology.util.ResourceUtil;
import eu.linksmart.aom.repository.AOMRepository;
import eu.linksmart.aom.repository.RepositoryFactory;
import eu.linksmart.aom.repository.SPARQLResult;

public class Update {
	public AOMRepository repo(){
		String location = "updateTimeOWLIM";
		return RepositoryFactory.local(location);		
//		return RepositoryFactory.owlim(location);		
	}

	//@Test
	public void init(){
		AOMRepository repo = repo();
		RepositoryConnection conn = repo.getConnection();
		try{
			conn.add(new File("/home/kostelni/out/uaar-test.owl"), "", RDFFormat.RDFXML);
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
		repo.close();
	}

	//@Test
	public void clean(){
		repo().removeRunTimeDevices();
	}

	//@Test
	public void populate(){
		AOMRepository repo = repo();
		String templateURI = "http://localhost/ontologies/Device.owl#TestingThermometer";
		Graph tmp = repo.getResource(templateURI);
		for(int i = 0; i < 90; i++){
			Graph runtime = tmp.clone(repo, templateURI);
			ResourceUtil.addStatement(
					runtime.getBaseURI(), 
					Device.isTesting, 
					"true", 
					XMLSchema.BOOLEAN,
					repo.getValueFactory(), 
					runtime);
			ResourceUtil.addStatement(
					runtime.getBaseURI(), 
					Device.PID, 
					"Thermometer_"+i, 
					XMLSchema.STRING, 
					repo.getValueFactory(),
					runtime);
			repo.store(runtime);
		}
		repo.close();
	}

	public double getMilis(long t){
		return ((double)(System.nanoTime() - t)/((double)1000000));
	}

	//@Test
	public void getDevicesByAOM(AOMRepository repo){
		try{
			long start = System.nanoTime();
			//String dQuery = "device:isDeviceTemplate;\"true\"^^xsd:boolean";
			String dQuery = "rdf:type;device:Thermometer";
			Set<Graph> devices = repo.getDevices(dQuery);
			//			System.out.println("DEVICES FOR: \n"+dQuery);
			//			for(Graph d: devices){
			//				System.out.println("> "+d.getBaseURI());
			//			}
			System.out.println("> getDevices ("+devices.size()+") : "+getMilis(start));
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	//@Test
	public void getDevicesBySPARQL(AOMRepository repo, boolean verbose){
		try{
			long start = System.nanoTime();
			//String dQuery = "device:isDeviceTemplate;\"true\"^^xsd:boolean";
			String dQuery = "" +
			"SELECT ?AOMDevice ?value WHERE {\n" +
			"?AOMDevice rdf:type device:Thermometer.\n" +
			"?AOMDevice device:hasHardware ?hardware.\n" +
			"?hardware hardware:hasBatteryLevel ?level.\n" +
			"?level hardware:batteryLevelValue ?value.\n" +
			"}";
			Set<SPARQLResult> devices = repo.sparql(dQuery);
			//			System.out.println("DEVICES FOR: \n"+dQuery);
			if(verbose){
				for(SPARQLResult d: devices){
					System.out.println("> "+d);
				}
			}
			System.out.println("> SPARQL ("+devices.size()+") : "+getMilis(start));
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	//@Test
	public void updateValue(AOMRepository repo, String uri){
		try{
			long start = System.nanoTime();
			repo.updateValue(
					uri,
					"device:hasHardware/hardware:hasBatteryLevel/hardware:batteryLevelValue;hardware:Low");
			System.out.println("UPDATE DURATION: "+getMilis(start)+".. updated: "+uri);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	public void updateValues(AOMRepository repo){
		try{
			long start = System.nanoTime();
			String dQuery = "" +
			"SELECT ?AOMDevice WHERE {\n" +
			"?AOMDevice rdf:type device:Thermometer.\n" +
			"}";
			Set<SPARQLResult> devices = repo.sparql(dQuery);
			for(SPARQLResult d: devices){
				updateValue(repo, d.value("AOMDevice"));
			}
		}
		catch(Exception e){
			e.printStackTrace();
		} 
	}

	//@Test
	public void doStuff(){
		AOMRepository repo = repo();
		for(int i = 0; i < 10; i++){
			System.out.println("\nRUN ["+i+"]:");
			getDevicesByAOM(repo);
			getDevicesBySPARQL(repo, false);
		}
//		getDevicesBySPARQL(repo, true);
//		updateValues(repo);
//		getDevicesBySPARQL(repo, true);
		repo.close();
	}
}
