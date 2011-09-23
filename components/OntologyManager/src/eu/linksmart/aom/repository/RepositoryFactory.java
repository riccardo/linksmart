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

import java.io.File;

import org.openrdf.repository.Repository;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.inferencer.fc.ForwardChainingRDFSInferencer;
import org.openrdf.sail.memory.MemoryStore;
import org.openrdf.sail.nativerdf.NativeStore;

//import com.ontotext.trree.OwlimSchemaRepository;

/**
 * Class responsible for creation and initialization of repositories implementing the 
 * Sesame Repository interface. In current implementation, the local native Sesame repository is used.
 * If needed, the repository can be replaced by any other more powerful triplestore implementing the 
 * Sesame Repository interface (such as BigOWLIM or Allegro Graph).
 * 
 * @author Peter Kostelnik
 *
 */
public class RepositoryFactory {

	/**
	 * Creates and initializes the native Sesame local repository.
	 * 
	 * @param location Filesystem location, where to hold the repository files.
	 * @return Initialized AOMRepository.
	 */
	public static AOMRepository local(String location) {
		try{
			Repository myRepository = new SailRepository(
					new ForwardChainingRDFSInferencer(
							new NativeStore(new File(location))));
			myRepository.initialize();
			return new AOMRepository(myRepository);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Creates and initializes the native Sesame in memory repository.
	 * In memory repository is currently used as helper for triplestore RDF/XML serialization.
	 * 
	 * @return Initialized AOMRepository.
	 */
	public static AOMRepository memory() {
		try{
			Repository myRepository = new SailRepository(new MemoryStore());
			myRepository.initialize();
			return new AOMRepository(myRepository);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	
//	public static AOMRepository owlim(String location) {
//		OwlimSchemaRepository sail = new OwlimSchemaRepository();
//		Repository repository = new SailRepository(sail);
//
//		sail.setParameter("build-ptsoc", "true");
//		sail.setParameter("enable-optimization", "true");
//		sail.setParameter("fts-memory", "0M");
//		sail.setParameter("storage-folder", "bigowlim-store");
//		sail.setParameter("repository-type", "file-repository");
//		sail.setParameter("console-thread", "false");
//		System.out.println("> params set");
//
//		repository.setDataDir(new File(location));
//
//		try{
//			repository.initialize();
//			return new AOMRepository(repository);		
//		}
//		catch(Exception e){
//			e.printStackTrace();
//		}
//		return null;
//	}
}
