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

import org.junit.Test;


import eu.linksmart.aom.ontology.Graph;
import eu.linksmart.aom.processor.ConfigurationProcessor;
import eu.linksmart.aom.processor.DiscoveryProcessor;
import eu.linksmart.aom.processor.EnergyProfileProcessor;
import eu.linksmart.aom.processor.EventProcessor;
import eu.linksmart.aom.processor.PreconditionProcessor;
import eu.linksmart.aom.processor.SCPDProcessor;
import eu.linksmart.aom.repository.AOMRepository;
import eu.linksmart.aom.repository.RepositoryFactory;
import eu.linksmart.aom.repository.Serializer;
import eu.linksmart.aom.testutil.DataLoader;
import eu.linksmart.aom.testutil.RepositoryUtil;

public class Performance {

  //@Test
  public void testMany(){
    String location = "performance";
    AOMRepository repo = RepositoryFactory.local(location);
    
//    storeMany(repo);
    
    RepositoryUtil.listDevices(repo);
    System.out.println("SIZE:: "+RepositoryUtil.repositorySize(repo));
    repo.close();
//    RepositoryUtil.clean(location);
  }

//  @Test
  public void testSerialization(){
    String location = "serialize";
    AOMRepository repo = RepositoryFactory.local(location);
    
    String deviceURI1 = storeOne(repo, "bluetooth.xml");
    String deviceURI2 = storeOne(repo, "tellstick.xml");
    String deviceURI3 = storeOne(repo, "rfidtag.xml");
    String deviceURI4 = storeOne(repo, "sensor.xml");
    Graph device1 = repo.getResource(deviceURI1);
    Graph device2 = repo.getResource(deviceURI2);
    Graph device3 = repo.getResource(deviceURI3);
    Graph device4 = repo.getResource(deviceURI4);
    
    String rdf1 = Serializer.serialize(device1);
    String rdf2 = Serializer.serialize(device1);
    String rdf3 = Serializer.serialize(device1);
    String rdf4 = Serializer.serialize(device1);
    
    System.out.println("DEVICE 1: \n"+device1.describe());
    System.out.println("DEVICE 2: \n"+device2.describe());
    System.out.println("DEVICE 3: \n"+device3.describe());
    System.out.println("DEVICE 4: \n"+device4.describe());

    System.out.println("RDF 1: \n"+rdf1);
    System.out.println("RDF 2: \n"+rdf2);
    System.out.println("RDF 3: \n"+rdf3);
    System.out.println("RDF 4: \n"+rdf4);

    
    RepositoryUtil.listDevices(repo);
    repo.close();
    RepositoryUtil.clean(location);
  }

  private void storeMany(AOMRepository repo){
    int dType = 0;
    for(int i = 0; i < 500; i++){
      SCPDProcessor sp = new SCPDProcessor(repo);
      EventProcessor evp = new EventProcessor(repo);
      EnergyProfileProcessor enp = new EnergyProfileProcessor(repo);
      DiscoveryProcessor dp = new DiscoveryProcessor(repo);

      Graph device = sp.process(DataLoader.xmlToString("test/resources/scpd/scpd.xml"));
      String deviceURI = device.getBaseURI();
      Graph events = evp.process(deviceURI, DataLoader.xmlToString("test/resources/event/events1.xml"));
      Graph energy = enp.process(deviceURI, DataLoader.xmlToString("test/resources/energy/energy.xml"));
      String discoXML = "";
      if(dType == 0){
        discoXML = DataLoader.xmlToString("test/resources/discovery/bluetooth.xml");        
      }
      else if(dType == 1){
        discoXML = DataLoader.xmlToString("test/resources/discovery/tellstick.xml");        
      }
      else if(dType == 2){
        discoXML = DataLoader.xmlToString("test/resources/discovery/rfidtag.xml");        
      }
      else if(dType == 3){
        discoXML = DataLoader.xmlToString("test/resources/discovery/sensor.xml");        
      }
      dType++;
      if(dType == 4) dType = 0;
      Graph disco = dp.process(deviceURI, discoXML);
      
      device.add(events);
      device.add(energy);
      device.add(disco);
      System.out.println("STORING: ["+i+"]: ["+device.getBaseURI()+"]");
      repo.store(device);
    }
  }
  
  private String storeOne(AOMRepository repo, String discoFile){
    SCPDProcessor sp = new SCPDProcessor(repo);
    EventProcessor evp = new EventProcessor(repo);
    EnergyProfileProcessor enp = new EnergyProfileProcessor(repo);
    DiscoveryProcessor dp = new DiscoveryProcessor(repo);

    Graph device = sp.process(DataLoader.xmlToString("test/resources/scpd/scpd.xml"));
    String deviceURI = device.getBaseURI();
    Graph events = evp.process(deviceURI, DataLoader.xmlToString("test/resources/event/events1.xml"));
    Graph energy = enp.process(deviceURI, DataLoader.xmlToString("test/resources/energy/energy.xml"));
    String discoXML = "";
    discoXML = DataLoader.xmlToString("test/resources/discovery/"+discoFile);        
    Graph disco = dp.process(deviceURI, discoXML);
    
    device.add(events);
    device.add(energy);
    device.add(disco);
    repo.store(device);
    return deviceURI;
  }

//  @Test
  public void testFullSCPD(){
    String location = "fullSCPD";
    AOMRepository repo = RepositoryFactory.local(location);
    
    String deviceURI1 = storeFull(repo, "bluetooth.xml");
//    String deviceURI2 = storeOne(repo, "tellstick.xml");
//    String deviceURI3 = storeOne(repo, "rfidtag.xml");
//    String deviceURI4 = storeOne(repo, "sensor.xml");
    Graph device1 = repo.getResource(deviceURI1);
//    Graph device2 = repo.getResource(deviceURI2);
//    Graph device3 = repo.getResource(deviceURI3);
//    Graph device4 = repo.getResource(deviceURI4);
//    
//    String rdf1 = Serializer.serialize(device1);
//    String rdf2 = Serializer.serialize(device1);
//    String rdf3 = Serializer.serialize(device1);
//    String rdf4 = Serializer.serialize(device1);
    
    System.out.println("DEVICE 1: \n"+device1.describe());
//    System.out.println("DEVICE 2: \n"+device2.describe());
//    System.out.println("DEVICE 3: \n"+device3.describe());
//    System.out.println("DEVICE 4: \n"+device4.describe());

//    System.out.println("RDF 1: \n"+rdf1);
//    System.out.println("RDF 2: \n"+rdf2);
//    System.out.println("RDF 3: \n"+rdf3);
//    System.out.println("RDF 4: \n"+rdf4);

    
//    RepositoryUtil.listDevices(repo);
    repo.close();
    RepositoryUtil.clean(location);
  }

  
  private String storeFull(AOMRepository repo, String discoFile){
    SCPDProcessor sp = new SCPDProcessor(repo);
    ConfigurationProcessor cp = new ConfigurationProcessor(repo);
    DiscoveryProcessor dp = new DiscoveryProcessor(repo);
    EnergyProfileProcessor enp = new EnergyProfileProcessor(repo);
    EventProcessor evp = new EventProcessor(repo);
    PreconditionProcessor pp = new PreconditionProcessor(repo);

    Graph device = sp.process(DataLoader.xmlToString("test/resources/scpd/semantic-scpd.xml"));
    String deviceURI = device.getBaseURI();

    Graph config = cp.process(deviceURI, DataLoader.xmlToString("test/resources/configuration/config.xml"));
    Graph disco = dp.process(deviceURI, DataLoader.xmlToString("test/resources/discovery/"+discoFile));
    Graph energy = enp.process(deviceURI, DataLoader.xmlToString("test/resources/energy/energy.xml"));
    Graph events = evp.process(deviceURI, DataLoader.xmlToString("test/resources/event/events1.xml"));
    
    device.add(config);
    device.add(disco);
    device.add(energy);
    device.add(events);
    repo.store(device);
    return deviceURI;
  }
}
