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

package eu.linksmart.aom.ontology;

import eu.linksmart.aom.ontology.model.Configuration;
import eu.linksmart.aom.ontology.model.Device;
import eu.linksmart.aom.ontology.model.Discovery;
import eu.linksmart.aom.ontology.model.Energy;
import eu.linksmart.aom.ontology.model.Event;
import eu.linksmart.aom.ontology.model.Model;
import eu.linksmart.aom.ontology.model.Namespace;
import eu.linksmart.aom.ontology.model.Rdf;
import eu.linksmart.aom.ontology.model.Rdfs;
import eu.linksmart.aom.ontology.model.Service;
import eu.linksmart.aom.ontology.model.Unit;

public class GraphData {
  public final static String prefixes = "" +
  "@prefix rdf: <"+Namespace.rdf+">. \n" +
  "@prefix rdfs: <"+Namespace.rdfs+">. \n" +
  "@prefix xsd: <"+Namespace.xsd+">. " +
  "@prefix device: <"+Namespace.device+">. \n" + 
  "@prefix service: <"+Namespace.service+">. \n" + 
  "@prefix discovery: <"+Namespace.service+">. \n";

  public final static String deviceURI = "http://my.device/physicaldevice/1";

  public final static String service1URI = "http://my.service/physicalservice/1";
  public final static String service1 = prefixes + 
  "<" +deviceURI+ "> <"+Device.hasService.stringValue()+"> <" +service1URI+ ">. \n" +
  "<"+ service1URI +"> <"+Rdf.rdfType.stringValue()+"> <"+Service.PhysicalService.stringValue()+">; \n" +
  "<"+Service.serviceOperation.stringValue()+"> \"some operation 1\"^^xsd:string.";

  public final static String service2URI = "http://my.service/physicalservice/2";
  public final static String service2 = prefixes + 
  "<" +deviceURI+ "> <"+Device.hasService.stringValue()+"> <" +service2URI+ ">. \n" +
  "<"+ service2URI +"> <"+Rdf.rdfType.stringValue()+"> <"+Service.PhysicalService.stringValue()+">; \n" +
  "<"+Service.serviceOperation.stringValue()+"> \"some operation 2\"^^xsd:string.";

  public final static String device = prefixes + 
  "<" + deviceURI + "> <"+Rdf.rdfType.stringValue()+"> <"+Device.PhysicalDevice.stringValue()+">. \n" +
  "" + service1 + service2;

  public final static String scpdManufacturer(String deviceURI, String infoURI){  
    return prefixes + "" +
    "<"+deviceURI+"> <"+Device.info.stringValue()+"> <"+infoURI+">. " +
    "<"+infoURI+"> <"+Rdf.rdfType.stringValue()+"> <"+Device.InfoDescription.stringValue()+">. " +
    "<"+infoURI+"> <"+Device.friendlyName.stringValue()+"> \"Basic Phone\"^^xsd:string; " +
    "<"+Device.manufacturer.stringValue()+"> \"CNet\"^^xsd:string;" +
    "<"+Device.manufacturerURL.stringValue()+"> \"http://www.cnet.se\"^^xsd:string;" + 
    "<"+Device.modelDescription.stringValue()+"> \"Basic Phone\"^^xsd:string;" + 
    "<"+Device.modelName.stringValue()+"> \"Z600\"^^xsd:string;" + 
    "<"+Device.modelNumber.stringValue()+"> \"1\"^^xsd:string."; 
  }

  public static String scpdServiceBase(String deviceURI, String serviceURI) {
    return prefixes + "" +
    "<"+deviceURI+"> <"+Device.hasService.stringValue()+"> <"+serviceURI+">. " +
    "<"+serviceURI+"> <"+Rdf.rdfType.stringValue()+"> <"+Service.PhysicalService+">; " +
    "<"+Service.serviceOperation.stringValue()+"> \"ReadMessage\"^^xsd:string. ";
  }
  
  public static String scpdServiceParamterIn(String serviceURI, String paramInURI) {
    return prefixes + "" +
    "<"+serviceURI+"> <"+Service.hasInput.stringValue()+"> <"+paramInURI+">. " +
    "<"+paramInURI+"> <"+Rdf.rdfType.stringValue()+"> <"+Service.ServiceInput+">; " +
    "<"+Service.parameterName.stringValue()+"> \"status\"^^xsd:string; " +
    "<"+Service.parameterType.stringValue()+"> \"string\"^^xsd:string; " +
    "<"+Service.relatedStateVariable.stringValue()+"> \"A_ARG_TYPE_ReadMessage_status\"^^xsd:string; " +
    "<"+Service.sendEvents.stringValue()+"> \"false\"^^xsd:boolean. ";
  }

  public static String scpdServiceParamterOut(String serviceURI, String paramOutURI) {
    return prefixes + "" +
    "<"+serviceURI+"> <"+Service.hasOutput.stringValue()+"> <"+paramOutURI+">. " +
    "<"+paramOutURI+"> <"+Rdf.rdfType.stringValue()+"> <"+Service.ServiceOutput+">; " +
    "<"+Service.parameterName.stringValue()+"> \"Message\"^^xsd:string; " +
    "<"+Service.parameterType.stringValue()+"> \"string\"^^xsd:string; " +
    "<"+Service.relatedStateVariable.stringValue()+"> \"Message\"^^xsd:string; " +
    "<"+Service.sendEvents.stringValue()+"> \"false\"^^xsd:boolean. ";
  }

  public static String scpdServiceWithParams(String deviceURI, String serviceURI, String paramInURI, String paramOutURI) {
    return scpdServiceBase(deviceURI, serviceURI) +
    scpdServiceParamterIn(serviceURI, paramInURI) + 
    scpdServiceParamterOut(serviceURI, paramOutURI);
  }
  
  public static String tellstick(String deviceURI, String discoURI) {
    return prefixes + "" +
    "<"+deviceURI+"> <"+Device.hasDiscoveryInfo.stringValue()+"> <"+discoURI+">. " + 
    "<"+discoURI+"> <"+Rdf.rdfType.stringValue()+"> <"+Discovery.TellStick+">; " + 
    "<"+Discovery.tsName.stringValue()+"> \"DiscoBall\"^^xsd:string; " +
    "<"+Discovery.tsVendor.stringValue()+"> \"Nexa\"^^xsd:string. ";
  }

  public static String rfid(String deviceURI, String discoURI) {
    return prefixes + "" +
    "<"+deviceURI+"> <"+Device.hasDiscoveryInfo.stringValue()+"> <"+discoURI+">. " + 
    "<"+discoURI+"> <"+Rdf.rdfType.stringValue()+"> <"+Discovery.RFIDTag+">; " + 
    "<"+Discovery.rfidVendor.stringValue()+"> \"RFIDSec\"^^xsd:string. ";
  }

  public static String sensor(String deviceURI, String discoURI) {
    return prefixes + "" +
    "<"+deviceURI+"> <"+Device.hasDiscoveryInfo.stringValue()+"> <"+discoURI+">. " + 
    "<"+discoURI+"> <"+Rdf.rdfType.stringValue()+"> <"+Discovery.Sensor+">; " + 
    "<"+Discovery.sType.stringValue()+"> \"Light\"^^xsd:string; " + 
    "<"+Discovery.sVendor.stringValue()+"> \"Phidget\"^^xsd:string. ";
  }

  public static String bluetooth(String deviceURI, String discoURI) {
    return prefixes + "" +
    "<"+deviceURI+"> <"+Device.hasDiscoveryInfo.stringValue()+"> <"+discoURI+">. " + 
    "<"+discoURI+"> <"+Rdf.rdfType.stringValue()+"> <"+Discovery.BlueTooth+">; " + 
    "<"+Discovery.btName.stringValue()+"> \"Z600\"^^xsd:string; " + 
    "<"+Discovery.btType.stringValue()+"> \"Phone\"^^xsd:string; " + 
    "<"+Discovery.btClass.stringValue()+"> \"CellPhonePhone\"^^xsd:string. ";
  }

  public static String bluetoothService(String discoURI, String serviceURI) {
    return prefixes + "" +
    "<"+discoURI+"> <"+Discovery.btService.stringValue()+"> <"+serviceURI+">. " + 
    "<"+serviceURI+"> <"+Rdf.rdfType.stringValue()+"> <"+Discovery.BlueToothService+">; " + 
    "<"+Discovery.btServiceName.stringValue()+"> \"Dial-up Networking\"^^xsd:string; " + 
    "<"+Discovery.btServiceType.stringValue()+"> \"Dialup Networking\"^^xsd:string; " + 
    "<"+Discovery.btServiceType.stringValue()+"> \"Generic Networking\"^^xsd:string. "; 
  }

  public static String physicalDeviceSource(String deviceURI, String discoURI) {
	    return prefixes + "" +
	    "<"+deviceURI+"> <"+Device.hasDiscoveryInfo.stringValue()+"> <"+discoURI+">. " + 
	    "<"+discoURI+"> <"+Rdf.rdfType.stringValue()+"> <"+Discovery.PhysicalDeviceSource+">; " + 
	    "<"+Discovery.pdsDeviceType.stringValue()+"> \"Tellstick\"^^xsd:string; " + 
	    "<"+Discovery.pdsVendor.stringValue()+"> \"Nexa\"^^xsd:string. ";
	  }

  public static String device(String deviceURI, String infoURI) {
    return prefixes + 
    "<" + deviceURI + "> <"+Rdf.rdfType.stringValue()+"> <"+Device.PhysicalDevice.stringValue()+">. \n" +
    scpdManufacturer(deviceURI, infoURI);
  }

  public static String unitValue(String uvURI, String unitURI, String value) {
    return prefixes + 
    "<" + uvURI + "> <"+Rdf.rdfType.stringValue()+"> <"+Unit.UnitValue.stringValue()+">; \n" +
    "<"+Unit.inUnit.stringValue()+"> <"+unitURI+">; \n" +
    "<"+Unit.value.stringValue()+"> \""+value+"\"^^xsd:string.";
  }

  public static String unitValue(String uvURI, String value) {
    return prefixes + 
    "<" + uvURI + "> <"+Rdf.rdfType.stringValue()+"> <"+Unit.UnitValue.stringValue()+">; \n" +
    "<"+Unit.value.stringValue()+"> \""+value+"\"^^xsd:string.";
  }

  public static String event(String deviceURI, String eventURI, String eventClass) {
    return prefixes + 
    "<" + deviceURI + "> <"+Device.hasEvent.stringValue()+"> <"+eventURI+">. \n" +
    "<" + eventURI + "> <"+Rdf.rdfType.stringValue()+"> <"+eventClass+">.";
  }
  public static String eventWithKey(String deviceURI, String eventURI, String eventClass, String eventTopic) {
	    return event(deviceURI, eventURI, eventClass) + 
	    "<" + eventURI + "> <"+Event.hasKey.stringValue()+"> <http://eventKey.uri>." + 
	    "<" + eventURI+"> <"+Event.hasTopic.stringValue()+"> \""+eventTopic+"\"^^xsd:string. \n";
	  }

  public static String uv(String uri) {
  return prefixes + 
  "<" + uri +"> <" + Rdf.rdfType + "> <" + Unit.UnitValue.stringValue() + ">; \n" +
  "<" + Unit.inUnit.stringValue() + "> <" + Namespace.unit + "Seconds" + ">; \n" +
  "<" + Unit.value.stringValue() + "> \"10\"^^xsd:string. \n";
  }

  public static String metaInformation(String eventURI, String metaInformationURI, String frequencyURI) {
    return  prefixes + 
    "<" + eventURI + "> <"+Event.hasMetaInformation.stringValue()+"> <"+metaInformationURI+">. \n" +
    "<" + metaInformationURI + "> <"+Rdf.rdfType.stringValue()+"> <"+Event.MetaInformation.stringValue()+">; \n" + 
    "<" + Event.trigger.stringValue()+"> \"TemperatureChange\"^^xsd:string; \n" + 
    "<" + Event.description.stringValue()+"> \"Reports change on temperature\"^^xsd:string; \n" + 
    "<" + Event.frequency.stringValue()+"> <" + frequencyURI + ">. \n" + 
    "<" + frequencyURI+"> <" + Rdf.rdfType + "> <" + Unit.UnitValue.stringValue() + ">; \n" +
    "<" + Unit.inUnit.stringValue() + "> <" + Namespace.unit + "Seconds" + ">; \n" +
    "<" + Unit.value.stringValue() + "> \"10\"^^xsd:string. \n";
  }
  public static String eventKey1(String eventURI, String eventKeyURI) {
    return  prefixes + 
    "<" + eventURI + "> <"+Event.hasKey.stringValue()+"> <"+eventKeyURI+">. \n" +
    "<" + eventKeyURI + "> <"+Rdf.rdfType.stringValue()+"> <"+Event.EventKey.stringValue()+">; \n" + 
    "<" + Event.keyName.stringValue()+"> \"HID\"^^xsd:string; \n" + 
    "<" + Event.dataType.stringValue()+"> \"String\"^^xsd:string. \n"; 
  }
  public static String eventKey2(String eventURI, String eventKeyURI) {
    return  prefixes + 
    "<" + eventURI + "> <"+Event.hasKey.stringValue()+"> <"+eventKeyURI+">. \n" +
    "<" + eventKeyURI + "> <"+Rdf.rdfType.stringValue()+"> <"+Event.EventKey.stringValue()+">; \n" + 
    "<" + Event.keyName.stringValue()+"> \"TemperatureValue\"^^xsd:string; \n" + 
    "<" + Event.dataType.stringValue()+"> \"Integer\"^^xsd:string; \n" + 
    "<" + Event.relatedStateVariable.stringValue()+"> \"Temperature\"^^xsd:string; \n" + 
    "<" + Event.minValue.stringValue()+"> \"-30\"^^xsd:string; \n" + 
    "<" + Event.maxValue.stringValue()+"> \"50\"^^xsd:string; \n" + 
    "<" + Event.valueUnit.stringValue()+"> <" + Namespace.unit + "Celsius" + ">. \n"; 
  }

  public static String energyProfile(String deviceURI, String epURI, String eclURI,
      String ecoURI, String egURI, String eltURI, String eoURI) {
    return prefixes + 
    "<" + deviceURI + "> <"+Device.hasEnergyProfile.stringValue()+"> <"+epURI+">. \n" +
    "<" + epURI + "> <"+Rdf.rdfType.stringValue()+"> <"+Energy.EnergyProfile.stringValue()+">; \n" +
    "<"+Energy.classification.stringValue()+"> <"+ eclURI +">; \n" + 
    "<"+Energy.consumption.stringValue()+"> <"+ ecoURI +">; \n" + 
    "<"+Energy.generation.stringValue()+"> <"+ egURI +">; \n" + 
    "<"+Energy.lifeTime.stringValue()+"> <"+ eltURI +">; \n" + 
    "<"+Energy.operation.stringValue()+"> <"+ eoURI +">.";
  }
  
  public static String energyClassification(String epURI, String ecURI) {
    return prefixes + 
    "<" + epURI + "> <"+Energy.classification.stringValue()+"> <"+ecURI+">. \n" +
    "<" + ecURI + "> <"+Rdf.rdfType.stringValue()+"> <"+Energy.EnergyClassification.stringValue()+">; \n" +
    "<"+Energy.classificationSystem.stringValue()+"> \"SIS\"^^xsd:string;" + 
    "<"+Energy.classificationValue.stringValue()+"> \"B\"^^xsd:string.";
  }

  public static String energyOperationWithValue(String epURI, String eoURI, String uvURI, 
      String unitURI, String value) {
    return energyOperation(epURI, eoURI, uvURI) + unitValue(uvURI, unitURI, value);
  }
  
  public static String energyOperation(String epURI, String eoURI, String uvURI) {
    return prefixes + 
    "<" + epURI + "> <"+Energy.operation.stringValue()+"> <"+eoURI+">. \n" +
    "<" + eoURI + "> <"+Rdf.rdfType.stringValue()+"> <"+Energy.EnergyOperation.stringValue()+">; \n" +
    "<"+Energy.minimumRunTime.stringValue()+"> <"+uvURI+">." ; 
  }

  public static String energyLifeTimeWithValues(
      String epURI, 
      String ltURI, 
      String expURI,
      String scURI,
      String sdURI) {
    return energyLifeTime(epURI, ltURI, expURI, scURI, sdURI) + 
    "<" + ltURI + "> <"+Energy.expected.stringValue()+"> <"+expURI+">; \n" +
    "<"+Energy.startCost.stringValue()+"> <"+scURI+">;" + 
    "<"+Energy.shutDownCost.stringValue()+"> <"+sdURI+">." + 
    unitValue(expURI, Namespace.unit + "minutes", "1") +
    unitValue(scURI, Namespace.unit + "minutes", "2") +
    unitValue(sdURI, Namespace.unit + "minutes", "3");
  }
        
  public static String energyLifeTimeWithValues2(
      String epURI, 
      String ltURI, 
      String expURI,
      String scURI,
      String sdURI) {
    return energyLifeTime(epURI, ltURI, expURI, scURI, sdURI) + 
    "<" + ltURI + "> <"+Energy.expected.stringValue()+"> <"+expURI+">; \n" +
    "<"+Energy.startCost.stringValue()+"> <"+scURI+">;" + 
    "<"+Energy.shutDownCost.stringValue()+"> <"+sdURI+">." + 
    unitValue(expURI, Namespace.unit + "seconds", "1") +
    unitValue(scURI, Namespace.unit + "minutes", "2") +
    unitValue(sdURI, Namespace.unit + "minutes", "3");
  }
        
  public static String energyLifeTime(
      String epURI, 
      String ltURI, 
      String expURI,
      String scURI,
      String sdURI) {
    return prefixes + 
    "<" + epURI + "> <"+Energy.lifeTime.stringValue()+"> <"+ltURI+">. \n" +
    "<" + ltURI + "> <"+Rdf.rdfType.stringValue()+"> <"+Energy.EnergyLifeTime.stringValue()+">; \n" +
    "<"+Energy.expected.stringValue()+"> <"+expURI+">;" + 
    "<"+Energy.startCost.stringValue()+"> <"+scURI+">;" + 
    "<"+Energy.shutDownCost.stringValue()+"> <"+sdURI+">."; 
  }

  public static String energyMode(String epURI, String relationURI, String modeURI) {
    return prefixes + 
    "<" + epURI + "> <"+relationURI+"> <"+modeURI+">. \n" +
    "<" + modeURI + "> <"+Rdf.rdfType.stringValue()+"> <"+Energy.EnergyMode.stringValue()+">; \n" +
    "<"+Energy.modeName.stringValue()+"> \"running\"^^xsd:string;"  + 
    "<"+Energy.modeMax.stringValue()+"> \"1\"^^xsd:string;"  + 
    "<"+Energy.modeAverage.stringValue()+"> \"2\"^^xsd:string;"  + 
    "<"+Energy.modeStart.stringValue()+"> \"3\"^^xsd:string;"  + 
    "<"+Energy.modeShutdown.stringValue()+"> \"4\"^^xsd:string."; 
  }

  public static String config(String deviceURI, String configURI) {
    return prefixes + 
    "<" + deviceURI + "> <"+Device.hasConfiguration+"> <"+configURI+">. \n" +
    "<" + configURI + "> <"+Rdf.rdfType.stringValue()+"> <"+Configuration.Configuration.stringValue()+">; \n" +
    "<"+Configuration.name.stringValue()+"> \"config name\"^^xsd:string;"  + 
    "<"+Configuration.implementationClass.stringValue()+"> \"some.impl.Class\"^^xsd:string.";
  }

  public static String configXML(String deviceURI, String configURI) {
    return config(deviceURI, configURI) + 
    "<" + configURI + "> <"+Configuration.configurationFile.stringValue()+"> <http://config.file/1>; \n" +
    "<"+Configuration.configurationFile.stringValue()+"> <http://config.file/2>. \n";
  }

  public static String configFile(String configURI, String fileURI) {
    return prefixes + 
    "<" + configURI + "> <"+Configuration.configurationFile+"> <"+fileURI+">. \n" +
    "<" + fileURI + "> <"+Rdf.rdfType.stringValue()+"> <"+Configuration.ConfigurationFile.stringValue()+">; \n" +
    "<"+Configuration.downloadURL.stringValue()+"> \"http://to.download/1\"^^xsd:string;"  + 
    "<"+Configuration.storageFolder.stringValue()+"> \"file://to.store/1\"^^xsd:string.";
  }

  public final static String device(String deviceType, int id) {
    return prefixes + 
    "<http://device.uri/"+id+"> <"+Rdf.rdfType.stringValue()+"> <"+deviceType+">. \n";
  }

  public final static String device(String deviceURI) {
    return prefixes + 
    "<"+deviceURI+"> <"+Rdf.rdfType.stringValue()+"> <"+Namespace.device+"Device>. \n";
  }

  public final static String physicalDevice(String deviceURI, boolean isTemplate) {
    return prefixes + 
    "<"+deviceURI+"> <"+Rdf.rdfType.stringValue()+"> <"+Namespace.device+"PhysicalDevice>. \n"+
    "<"+deviceURI+"> <"+Device.isDeviceTemplate.stringValue()+"> \""+isTemplate+"\"^^<"+Namespace.xsd+"boolean>. \n";
  }

  public final static String physicalDevice(String deviceURI, boolean isTemplate, String templateURI) {
    return physicalDevice(deviceURI, isTemplate) + 
    "<"+deviceURI+"> <"+Device.clonedFromTemplate.stringValue()+"> <"+templateURI+">.";
  }

  public final static String semanticDevice(String deviceURI, boolean isTemplate) {
    return prefixes + 
    "<"+deviceURI+"> <"+Rdf.rdfType.stringValue()+"> <"+Namespace.device+"SemanticDevice>. \n"+
    "<"+deviceURI+"> <"+Device.isDeviceTemplate.stringValue()+"> \""+isTemplate+"\"^^<"+Namespace.xsd+"boolean>. \n";
  }

  public final static String semanticDevice(String deviceURI, boolean isTemplate, String templateURI) {
    return semanticDevice(deviceURI, isTemplate) + 
    "<"+deviceURI+"> <"+Device.clonedFromTemplate.stringValue()+"> <"+templateURI+">.";
  }

  public static String service(String deviceURI, String serviceURI, String paramUnit) {
    String paramURI = serviceURI + "/out";
    return prefixes + "" +
    "<"+deviceURI+"> <"+Device.hasService.stringValue()+"> <"+serviceURI+">. " +
    "<"+serviceURI+"> <"+Service.hasOutput.stringValue()+"> <"+paramURI+">. " +
    "<"+serviceURI+"> <"+Service.serviceOperation.stringValue()+"> \""+serviceURI+"/operation\"^^<"+Namespace.xsd+"string>. " +
    "<"+paramURI+"> <"+Rdf.rdfType.stringValue()+"> <"+Service.ServiceOutput+">; " +
    "  <"+Service.parameterUnit.stringValue()+"> <"+paramUnit+">. ";
  }

  public static String serviceExt(String serviceURI, String cost, String operation) {
    return prefixes + "" +
    "<"+serviceURI+"> <"+Service.serviceCost.stringValue()+"> \""+cost+"\"^^xsd:String. " +
    "<"+serviceURI+"> <"+Service.serviceOperation.stringValue()+"> \""+operation+"\"^^xsd:String. ";
  }


  public final static String pid(String deviceURI, String pid) {
    return prefixes + 
    "<"+deviceURI+"> <"+Device.PID.stringValue()+"> \""+pid+"\"^^xsd:string. \n";
  }

  public static String configContent(String deviceURI, String configURI, String name, String impl) {
    return prefixes + 
    "<" + deviceURI + "> <"+Device.hasConfiguration+"> <"+configURI+">. \n" +
    "<" + configURI + "> <"+Rdf.rdfType.stringValue()+"> <"+Configuration.Configuration.stringValue()+">; \n" +
    "<"+Configuration.name.stringValue()+"> \""+name+"\"^^xsd:string;"  + 
    "<"+Configuration.implementationClass.stringValue()+"> \""+impl+"\"^^xsd:string.";
  }
  public static String configFileContent(String configURI, String fileURI, String down, String store) {
    return prefixes + 
    "<" + configURI + "> <"+Configuration.configurationFile+"> <"+fileURI+">. \n" +
    "<" + fileURI + "> <"+Rdf.rdfType.stringValue()+"> <"+Configuration.ConfigurationFile.stringValue()+">; \n" +
    "<"+Configuration.downloadURL.stringValue()+"> \""+down+"\"^^xsd:string;"  + 
    "<"+Configuration.storageFolder.stringValue()+"> \""+store+"\"^^xsd:string.";
  }
  public static String configFileContent(String configURI, String down, String store) {
    String fileURI = "http://some.file.uri";
    return configFileContent(configURI, fileURI, down, store);
  }
  public static String model() {
    return prefixes + 
    "<" + Namespace.unit + "Unit> <"+Rdfs.subClassOf.stringValue()+"> <"+Model.StaticTaxonomy.stringValue()+">. \n" ;
  }
  public static String unit(String unitURI) {
    return prefixes + 
    "<" + Namespace.unit + "SomeUnit> <"+Rdfs.subClassOf.stringValue()+"> <"+Namespace.unit+"Unit>. \n" +
    "<" + Namespace.unit + "AnotherUnit> <"+Rdfs.subClassOf.stringValue()+"> <"+Namespace.unit+"SomeUnit>. \n" +
    "<" + unitURI +"> <"+Rdf.rdfType.stringValue()+"> <"+Namespace.unit+"AnotherUnit>. \n" ;
  }

  public final static String preconditionPID(String deviceURI, String preconditionURI, String preconditionId, String deviceType, String pid) {
    return prefixes + 
    "<"+deviceURI+"> <"+Device.hasPrecondition.stringValue()+"> <"+preconditionURI+">. \n" + 
    "<"+preconditionURI+"> <"+Rdf.rdfType.stringValue()+"> <"+Device.SemanticDevicePIDPrecondition.stringValue()+">. \n" +
    "<"+preconditionURI+"> <"+Device.preconditionDeviceType.stringValue()+"> \""+deviceType+"\"^^xsd:string. \n" + 
    "<"+preconditionURI+"> <"+Device.preconditionId.stringValue()+"> \""+preconditionId+"\"^^xsd:string. \n"+
    "<"+preconditionURI+"> <"+Device.preconditionPID.stringValue()+"> \""+pid+"\"^^xsd:string. \n"; 
  }

  public final static String preconditionPID(String preconditionURI, String pid) {
    return prefixes + 
    "<"+preconditionURI+"> <"+Device.preconditionPID.stringValue()+"> \""+pid+"\"^^xsd:string. \n"; 
  }

  public final static String preconditionQuery(String deviceURI, String preconditionURI, String preconditionId, String deviceType, String cardinality, String query) {
    return prefixes + "<"+deviceURI+"> <"+Device.hasPrecondition.stringValue()+"> <"+preconditionURI+">. \n" + 
    "<"+preconditionURI+"> <"+Rdf.rdfType.stringValue()+"> <"+Device.SemanticDeviceQueryPrecondition.stringValue()+">. \n" +
    "<"+preconditionURI+"> <"+Device.preconditionId.stringValue()+"> \""+preconditionId+"\"^^xsd:string. \n" + 
    "<"+preconditionURI+"> <"+Device.preconditionDeviceType.stringValue()+"> \""+deviceType+"\"^^xsd:string. \n" + 
    "<"+preconditionURI+"> <"+Device.preconditionQuery.stringValue()+"> \""+query+"\"^^xsd:string. \n"+
    "<"+preconditionURI+"> <"+Device.preconditionCardinality.stringValue()+"> \""+cardinality+"\"^^xsd:string. \n";
  }
  public final static String satisfiedBy(String preconditionURI, String byURI) {
    return prefixes + 
    "<"+preconditionURI+"> <"+Device.isSatisfiedBy.stringValue()+"> <"+byURI+">. \n";
  }
  public final static String discoveryEvaluation(String deviceURI, String evaluation) {
    return prefixes + 
    "<"+deviceURI+"> <"+Device.evaluation.stringValue()+"> \""+evaluation+"\"^^xsd:string. \n";
  }

  public static String taxonomy() {
    return prefixes + 
    "<" + Device.PhysicalDevice + "> <"+Rdfs.subClassOf.stringValue()+"> <"+Device.Device+">. \n" +
    "<" + Device.SemanticDevice + "> <"+Rdfs.subClassOf.stringValue()+"> <"+Device.Device+">. \n" +
    "<" + Namespace.device + "SomeDevice> <"+Rdfs.subClassOf.stringValue()+"> <"+Device.PhysicalDevice+">. \n" +
    "<" + Namespace.device + "AnotherDevice> <"+Rdfs.subClassOf.stringValue()+"> <" + Namespace.device + "SomeDevice>. \n" + 
    "<" + Namespace.device + "SomeSemanticDevice> <"+Rdfs.subClassOf.stringValue()+"> <"+Device.SemanticDevice+">. \n";
  }

  public final static String hardware(
      String deviceURI, 
      String screenHeight,
      String screenHeightUnit,
      String screenWidth,
      String screenWidthUnit) {
    String hwURI = deviceURI + "/hw";
    String displayURI = hwURI + "/display";
    String hUnitURI = displayURI + "/display/hUnit";
    String wUnitURI = displayURI + "/display/wUnit";
    return prefixes + 
    "<"+deviceURI+"> <"+Device.hasHardware.stringValue()+"> <"+hwURI+">. \n" +
    "<"+hwURI+"> <"+Namespace.hardware+"hasDisplay> <"+displayURI+">. \n" +
    "<"+displayURI+"> <"+Rdf.rdfType+"> <"+Namespace.hardware+"Display>. \n" +
    "<"+displayURI+"> <"+Namespace.hardware+"screenHeight> <"+hUnitURI+">. \n" +
    "<"+hUnitURI+"> <"+Rdf.rdfType+"> <"+Namespace.unit+"UnitValue>. \n" +
    "<"+hUnitURI+"> <"+Namespace.unit+"inUnit> <"+Namespace.unit+screenHeightUnit+">. \n" +
    "<"+hUnitURI+"> <"+Namespace.unit+"value> \""+screenHeight+"\"^^<"+Namespace.xsd+"string>. \n" +
    "<"+displayURI+"> <"+Namespace.hardware+"screenWidth> <"+wUnitURI+">. \n" +
    "<"+wUnitURI+"> <"+Rdf.rdfType+"> <"+Namespace.unit+"UnitValue>. \n" +
    "<"+wUnitURI+"> <"+Namespace.unit+"inUnit> <"+Namespace.unit+screenWidthUnit+">. \n" +
    "<"+wUnitURI+"> <"+Namespace.unit+"value> \""+screenWidth+"\"^^<"+Namespace.xsd+"string>. \n";
  }

  public final static String hardware(
      String deviceURI, 
      String screenWidth,
      String screenWidthUnit,
      String resolution) {
    String hwURI = deviceURI + "/hw";
    String displayURI = hwURI + "/display";
    String hUnitURI = displayURI + "/display/hUnit";
    String wUnitURI = displayURI + "/display/wUnit";
    String rUnitURI = displayURI + "/display/rUnit";
    return prefixes + 
    "<"+deviceURI+"> <"+Device.hasHardware.stringValue()+"> <"+hwURI+">. \n" +
    "<"+hwURI+"> <"+Namespace.hardware+"hasDisplay> <"+displayURI+">. \n" +
    "<"+displayURI+"> <"+Rdf.rdfType+"> <"+Namespace.hardware+"Display>. \n" +
    "<"+displayURI+"> <"+Namespace.hardware+"screenHeight> <"+hUnitURI+">. \n" +
    "<"+hUnitURI+"> <"+Rdf.rdfType+"> <"+Namespace.unit+"UnitValue>. \n" +
    "<"+displayURI+"> <"+Namespace.hardware+"screenWidth> <"+wUnitURI+">. \n" +
    "<"+wUnitURI+"> <"+Rdf.rdfType+"> <"+Namespace.unit+"UnitValue>. \n" +
    "<"+wUnitURI+"> <"+Namespace.unit+"inUnit> <"+Namespace.unit+screenWidthUnit+">. \n" +
    "<"+wUnitURI+"> <"+Namespace.unit+"value> \""+screenWidth+"\"^^<"+Namespace.xsd+"string>. \n"+
    "<"+displayURI+"> <"+Namespace.hardware+"resolution> <"+rUnitURI+">. \n" +
    "<"+rUnitURI+"> <"+Rdf.rdfType+"> <"+Namespace.unit+"UnitValue>. \n" +
    "<"+rUnitURI+"> <"+Namespace.unit+"value> \""+resolution+"\"^^<"+Namespace.xsd+"string>. \n";
  }
  
  public final static String propertyTaxonomy(){
    return prefixes + "" +
    "<"+Namespace.device+"PhysicalDevice> <"+Rdfs.subClassOf.stringValue()+"> <"+Namespace.device+"Device>. \n" + 
    "<"+Namespace.device+"PhysicalSubDevice> <"+Rdfs.subClassOf.stringValue()+"> <"+Namespace.device+"PhysicalDevice>. \n" + 
    "<"+Namespace.device+"SemanticDevice> <"+Rdfs.subClassOf.stringValue()+"> <"+Namespace.device+"Device>. \n" +
    "<"+Namespace.device+"SemanticSubDevice> <"+Rdfs.subClassOf.stringValue()+"> <"+Namespace.device+"SemanticDevice>. \n";
  }
  public final static String instances(){
    return prefixes + "" +
    "<"+Namespace.device+"i1> <"+Rdf.rdfType.stringValue()+"> <"+Namespace.device+"PhysicalDevice>. \n" +
    "<"+Namespace.device+"i2> <"+Rdf.rdfType.stringValue()+"> <"+Namespace.device+"SemanticDevice>. \n" +
    "<"+Namespace.device+"i3> <"+Rdf.rdfType.stringValue()+"> <"+Namespace.device+"PhysicalSubDevice>. \n" +
    "";
  }

  public final static String deviceClass(){
    return prefixes + "" +
    "<"+Namespace.device+"Device> <"+Rdfs.subClassOf.stringValue()+"> <"+Namespace.rdfs+"Class>. \n";
  }

  public final static String property(String propBase, String uri, String domain, String range){
    String property = uri+"property"+propBase;
    String rdfType = Namespace.property+propBase;
    return prefixes + "" +
    "<"+property+"> <"+Rdf.rdfType.stringValue()+"> <"+rdfType+">. \n" +
    "<"+property+"> <"+Rdfs.domain.stringValue()+"> <"+Namespace.device+domain+">. \n" +
    "<"+property+"> <"+Rdfs.range.stringValue()+"> <"+Namespace.device+range+">. \n" +
    "";
  }

  public final static String propertyAnnotationModel(){
    String out = prefixes;
    
    out += propertyTaxonomy();
    out += property("SingleValueFormFieldProperty", Namespace.device, "Device", "Range1");
    out += property("MultiValueFormFieldProperty", Namespace.service, "Device", "Range2");
    out += property("SingleValueFormProperty", Namespace.event, "PhysicalDevice", "Range3");
    out += property("MultiValueFormProperty", Namespace.energy, "SemanticDevice", "Range4");
    out += property("SingleValueAnnotationProperty", Namespace.application, "PhysicalSubDevice", "Range5");
    out += property("MultiValueAnnotationProperty", Namespace.unit, "SemanticSubDevice", "Range6");
    
    return out;
    
  }
  
  public final static String propertyModel(String pURI, String dURI, String rURI){
    return prefixes + 
    "<"+pURI+"> <"+Rdfs.domain.stringValue()+"> <"+dURI+">. \n" +
    "<"+pURI+"> <"+Rdfs.range.stringValue()+"> <"+rURI+">. \n";
  }

  public final static String formData(String dURI){
    String p1URI = "http://property.uri/1"; 
    String hwURI = "http://hardware.uri/1"; 
    String classURI = "http://class.uri/1";
    String p2URI = "http://property.uri/2"; 
    String p3URI = "http://property.uri/3"; 
    String value1 = "value1"; 
    String value2 = "true"; 
    String type1 = Namespace.xsd + "string"; 
    String type2 = Namespace.xsd + "boolean"; 
    return prefixes + 
    "<"+dURI+"> <"+p1URI+"> <"+hwURI+">. \n" +
    "<"+hwURI+"> <"+Rdf.rdfType.stringValue()+"> <"+classURI+">. \n" + 
    "<"+hwURI+"> <"+p2URI+"> \""+value1+"\"^^<"+type1+">. \n" + 
    "<"+hwURI+"> <"+p3URI+"> \""+value2+"\"^^<"+type2+">. \n";
  }
  public final static String formDataNoParent(){
    String hwURI = "http://hardware.uri/1"; 
    String classURI = "http://class.uri/1";
    String p2URI = "http://property.uri/2"; 
    String p3URI = "http://property.uri/3"; 
    String value1 = "value1"; 
    String value2 = "true"; 
    String type1 = Namespace.xsd + "string"; 
    String type2 = Namespace.xsd + "boolean"; 
    return prefixes + 
    "<"+hwURI+"> <"+Rdf.rdfType.stringValue()+"> <"+classURI+">. \n" + 
    "<"+hwURI+"> <"+p2URI+"> \""+value1+"\"^^<"+type1+">. \n" + 
    "<"+hwURI+"> <"+p3URI+"> \""+value2+"\"^^<"+type2+">. \n";
  }
}
