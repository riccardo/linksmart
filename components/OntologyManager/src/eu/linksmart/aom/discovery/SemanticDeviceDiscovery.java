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

package eu.linksmart.aom.discovery;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.vocabulary.XMLSchema;

import eu.linksmart.aom.ontology.Graph;
import eu.linksmart.aom.ontology.model.Device;
import eu.linksmart.aom.ontology.model.Namespace;
import eu.linksmart.aom.ontology.model.Rdf;
import eu.linksmart.aom.ontology.util.ResourceUtil;
import eu.linksmart.aom.repository.AOMRepository;
import eu.linksmart.aom.repository.PIDRuleResolver;
import eu.linksmart.aom.repository.SPARQLResult;

/**
 * Class responsible for discovery of semantic devices taking into account also the hierarchic 
 * structure of semantic and physical device dependencies. Each time the new physical device
 * enters the network or some device leaves the network, 
 * semantic devices should be rediscovered.   
 * @author Peter Kostelnik
 *
 */
public class SemanticDeviceDiscovery {
  AOMRepository repository;

  public SemanticDeviceDiscovery(AOMRepository repository) {
    this.repository = repository;
  }

  /**
   * Generates graph specifying semantic device templates or run-time instances 
   * to be transformed to SPARQL query.
   * @param Flag, if query generated from graph should retrieve templates or 
   * run-time instances.
   * @return Graph to be transformed to required SPARQL query.
   */
  public Graph semanticDevicesQuery(boolean isTemplate){
    String queryURI = "http://semantic.device.uri";
    Graph query = new Graph(queryURI);
    ResourceUtil.addStatement(
        queryURI, 
        Device.isDeviceTemplate, 
        isTemplate + "", 
        XMLSchema.BOOLEAN, 
        repository.getValueFactory(), 
        query);
    ResourceUtil.addStatement(
        queryURI, 
        Rdf.rdfType, 
        Device.SemanticDevice.stringValue(), 
        repository.getValueFactory(), 
        query);
    return query;
  }

  /** 
   * Retrieves all templates of semantic devices. When new physical device
   * enters the network, each semantic device has to be rediscovered
   * to check if it is enabled or not.
   * @return Collection of semantic device templates.
   */
  public Set<Graph> getSemanticDeviceTemplates(){
    return repository.getDevices(semanticDevicesQuery(true));
  }

  /**
   * Removes all run-time semantic devices before all semantic
   * device templates are rediscovered again.
   */
  public void removeRunTimeSemanticDevices(){
    Set<Graph> runtime = repository.getDevices(semanticDevicesQuery(false));
    for(Graph device : runtime){
      repository.remove(device);
    }
  }

  /**
   * Returns runtime instance from collection, if runtime instance was 
   * cloned from template. 
   * @param Template from which runtime instance is cloned;
   * @param Collection of runtime instances.
   * @return Identified runtime instance if found, <code>null</code> otherwise; 
   */
  public Graph getRediscoveredTemplate(Graph template, Set<Graph> rediscovered){
    for(Graph runtime: rediscovered) {
      if(runtime.value(Device.clonedFromTemplate).equals(template.getBaseURI())){
        return runtime;
      }
    }
    return null;
  }

  /**
   * Check if device type is sub class of SemanticDevice.
   * @param Device type.
   * @return Check result.
   */
  public boolean isSemanticDevice(String deviceType){
    if(deviceType.equals(Device.SemanticDevice.stringValue()) || 
    		(Namespace.device + deviceType).equals(Device.SemanticDevice.stringValue())) return true;

    String deviceTypeURI = Namespace.device + className(deviceType);
   if(repository.getSuperClassesOf(deviceTypeURI).contains(Device.SemanticDevice.stringValue())) return true;
    return false;
  }

  /**
   * Check if device type is sub class of PhysicalDevice.
   * @param Device type.
   * @return Check result.
   */
  public boolean isPhysicalDevice(String deviceType){
    if(deviceType.equals(Device.PhysicalDevice.stringValue()) || 
    		(Namespace.device + deviceType).equals(Device.PhysicalDevice.stringValue())) return true;

    String deviceTypeURI = Namespace.device + className(deviceType);
    if(repository.getSuperClassesOf(deviceTypeURI).contains(Device.PhysicalDevice.stringValue())) return true;
    return false;
  }

  private void assignSatisfyingDevices(Graph device, Graph precondition, Set<String> satisfyingDevices){
    ValueFactory f = repository.getValueFactory();
    for(String satisfyingDevice: satisfyingDevices){
      ResourceUtil.addStatement(
          precondition.getBaseURI(),
          Device.isSatisfiedBy, 
          satisfyingDevice, 
          f, 
          device);
    }
  }

  private String className(String deviceType){
	  try{
		  URI uri = new URIImpl(deviceType);
		  return uri.getLocalName();
	  }
	  catch(Exception e){}
	  return deviceType;
  }

  /**
   * Retrieves all physical run-time devices with PIDs defined in precondition. 
   * If all devices with PIDs specified exist in runtime, then the 
   * precondition is satisfied and the collection of this devices is returned.  
   * @param PIDs precondition.
   * @return Collection of satisfying devices or <code>null</code>.
   */
  public Set<String> satisfyPhysicalPID(Graph precondition){
    Set<String> satisfyingDevices = new HashSet<String>();
    Set<String> pids = precondition.values(Device.preconditionPID);
    for(String pid : pids){
      String query = 
        "SELECT DISTINCT ?AOMDevice WHERE { \n" +
        "  ?AOMDevice rdf:type device:PhysicalDevice . \n" +
        "  ?AOMDevice device:isDeviceTemplate \"false\"^^xsd:boolean . \n" +
        "  ?AOMDevice device:PID \""+pid+"\"^^xsd:string. " +
        "}";
      Set<SPARQLResult> results = repository.sparql(query);
      if(!(results.size() == 1 && 
          results.iterator().next().value("AOMDevice") != null)){
        return null;
      }
      else{
        satisfyingDevices.add(results.iterator().next().value("AOMDevice"));
      }
    }
    return satisfyingDevices;
  }

  /**
   * Retrieves all physical run-time devices matching query and cardinality
   * defined in precondition. 
   * If number of matching devices satisfies the cardinality, then the 
   * precondition is satisfied and the collection of this devices is returned.  
   * @param Query precondition.
   * @return Collection of satisfying devices or <code>null</code>.
   */

  public Set<String> satisfyPhysicalQuery(Graph precondition){
    Set<String> satisfyingDevices = new HashSet<String>();
    String query = precondition.value(Device.preconditionQuery);
    PreconditionCardinality cardinality = 
      new PreconditionCardinality(
          precondition.value(Device.preconditionCardinality));
    query = 
      "device:isDeviceTemplate;\"false\"^^xsd:boolean, " +
      "rdf:type;device:"+className(precondition.value(Device.preconditionDeviceType))+"," + query;
    Set<Graph> devices = new HashSet<Graph>();
    try{
      devices = repository.getDevices(query);
    }
    catch(Exception e){}
    if(cardinality.isSatisfied(devices.size())){
      for(Graph device : devices){
        satisfyingDevices.add(device.getBaseURI());
      }
      return satisfyingDevices;
    }
    return null;
  }


  private Graph deviceWithPID(Set<Graph> rediscovered, String pid){
    for(Graph device: rediscovered){
      if(device.value(Device.PID).equals(pid))
        return device;
    }
    return null;
  }

  private boolean getEvaluation(String value){
    if(value.equals("true")) return true;
    else return false;
  }

  /**
   * Looks for satisfied rediscovered semantic devices and retrieves 
   * those with required PIDs. If semantic device with PID was not
   * evaluated yet, it is recursively evaluated before this 
   * precondition is satisfied.
   * @param device Device graph.
   * @param precondition PIDs precondition.
   * @param rediscovered Actual set of semantic devices to be rediscovered.
   * @param dependency Actual set of dependency chain generated by recursive resolution.
   * @return Precondition satisfaction result.
   */
  public Set<String> satisfySemanticPID(Graph device, Graph precondition, Set<Graph> rediscovered, Set<String> dependency) throws Exception {
    Set<String> satisfyingDevices = new HashSet<String>();
    Set<String> pids = precondition.values(Device.preconditionPID);
    boolean satisfied = true;
    Iterator<String> i = pids.iterator();
    while(i.hasNext() && satisfied){
      String pid = i.next();
      Graph matching = deviceWithPID(rediscovered, pid);
      if(matching == null){
        satisfied = false;
      }
      else{
        String evaluation = matching.value(Device.evaluation);
        if(evaluation != null) {
          satisfied = getEvaluation(evaluation);
        }
        else{
          Graph result = rediscover(matching, rediscovered, dependency);

          String newEvaluation = result.value(Device.evaluation);
          if(newEvaluation != null){
            satisfied = getEvaluation(newEvaluation);
          }
        }
        if(satisfied){
          satisfyingDevices.add(matching.getBaseURI());
        }
      }


    }
    return satisfyingDevices;
  }

  /**
   * Looks for satisfied rediscovered semantic devices and retrieves 
   * those which match the query precondition. If retrieved matching semantic device was not
   * evaluated yet, it is recursively evaluated before this 
   * precondition is satisfied.
   * @param device Device graph.
   * @param precondition Query precondition.
   * @param rediscovered Actual set of semantic devices to be rediscovered.
   * @param dependency Actual set of dependency chain generated by recursive resolution.
   * @return Precondition satisfaction result.
   */
  public Set<String> satisfySemanticQuery(Graph device, Graph precondition, Set<Graph> rediscovered, Set<String> dependency) throws Exception {
    Set<String> satisfyingDevices = new HashSet<String>();

    String query = precondition.value(Device.preconditionQuery);
    PreconditionCardinality cardinality = 
      new PreconditionCardinality(
          precondition.value(Device.preconditionCardinality));
    query = 
      "device:isDeviceTemplate;\"true\"^^xsd:boolean, " +
      "rdf:type;device:"+className(precondition.value(Device.preconditionDeviceType))+"," + query;
    Set<Graph> matching = repository.getDevices(query);

    Set<Graph> candidates = new HashSet<Graph>();
    for(Graph match: matching){
      Graph runtime = getRediscoveredTemplate(match, rediscovered);
      if(runtime != null){
        if(!runtime.getBaseURI().equals(device.getBaseURI())){
          String evaluation = runtime.value(Device.evaluation);
          if(evaluation != null) {
            if(getEvaluation(evaluation)){
              candidates.add(runtime);
            }
          }
          else{
            Graph result = rediscover(runtime, rediscovered, dependency);

            String newEvaluation = result.value(Device.evaluation);
            if(newEvaluation != null){
              if(getEvaluation(newEvaluation)){
                candidates.add(result);
              }
            }
          }
        }
      }
    }


    if(cardinality.isSatisfied(candidates.size())){
      for(Graph candidate : candidates){
        satisfyingDevices.add(candidate.getBaseURI());
      }
      return satisfyingDevices;
    }


    return satisfyingDevices;
  }


  /**
   * Satisfies PID based precondition according to precondition device type.
   * @param device Device graph.
   * @param precondition PIDs precondition.
   * @param rediscovered Actual set of semantic devices to be rediscovered.
   * @param dependency Actual set of dependency chain generated by recursive resolution.
   * @return Collection of satisfying devices.
   */
  public boolean satisfyPID(Graph device, Graph precondition, Set<Graph> rediscovered, Set<String> dependency) throws Exception {
    String deviceType = precondition.value(Device.preconditionDeviceType);
    if(isSemanticDevice(deviceType)){
      Set<String> satisfyingDevices = satisfySemanticPID(device, precondition, rediscovered, dependency);
      if(satisfyingDevices != null && satisfyingDevices.size() > 0){
        assignSatisfyingDevices(device, precondition, satisfyingDevices);
        return true;
      }
      else{
        return false;
      }
    }
    else if(isPhysicalDevice(deviceType)){
      Set<String> satisfyingDevices = satisfyPhysicalPID(precondition);
      if(satisfyingDevices != null && satisfyingDevices.size() > 0){
        assignSatisfyingDevices(device, precondition, satisfyingDevices);
        return true;
      }
      else{
        return false;
      }
    }

    return false;
  }

  /**
   * Satisfies query based precondition according to precondition device type.
   * @param device Device graph.
   * @param precondition PIDs precondition.
   * @param rediscovered Actual set of semantic devices to be rediscovered.
   * @param dependency Actual set of dependency chain generated by recursive resolution.
   * @return Collection of satisfying devices.
   */
  public boolean satisfyQuery(Graph device, Graph precondition, Set<Graph> rediscovered, Set<String> dependency) throws Exception {
    String deviceType = precondition.value(Device.preconditionDeviceType);
    if(isSemanticDevice(deviceType)){
      Set<String> satisfyingDevices = satisfySemanticQuery(device, precondition, rediscovered, dependency);
      if(satisfyingDevices != null && satisfyingDevices.size() > 0){
        assignSatisfyingDevices(device, precondition, satisfyingDevices);
        return true;
      }
    }
    else if(isPhysicalDevice(deviceType)){
      Set<String> satisfyingDevices = satisfyPhysicalQuery(precondition);
      if(satisfyingDevices != null && satisfyingDevices.size() > 0){
        assignSatisfyingDevices(device, precondition, satisfyingDevices);
        return true;
      }
    }

    return false;
  }

  /**
   * Satisfies the precondition according to its type.
   * @param Precondition to be satisfied.
   * @return Collection of satisfying devices.
   */
  public boolean satisfy(Graph device, Graph precondition, Set<Graph> rediscovered, Set<String> dependency) throws Exception {
    String preconditionType = precondition.value(Rdf.rdfType);
    if(preconditionType.equals(Device.SemanticDevicePIDPrecondition.stringValue())){
      return satisfyPID(device, precondition, rediscovered, dependency);
    }
    else if(preconditionType.equals(Device.SemanticDeviceQueryPrecondition.stringValue())){
      return satisfyQuery(device, precondition, rediscovered, dependency);
    }
    return false;
  }

  private void addEvaluation(Graph device, boolean evaluation){
    ResourceUtil.addStatement(
        device.getBaseURI(), 
        Device.evaluation, 
        evaluation + "", 
        XMLSchema.BOOLEAN,
        repository.getValueFactory(), 
        device);
  }
  /** 
   * Rediscovers  semantic device template by trying to satisfy its 
   * preconditions. If precondition generates other semantic devices, 
   * those will be rediscovered firstly to ensure the rediscovery
   * information is actual. Each rediscovered template will get the 
   * flag if it is enabled or not. This flag will be used to ensure, that
   * each semantic device was rediscovered only once; and to back-propagate
   * the logical value of enabling the template to the higher semantic device
   * templates structures. Algorithm behaves as the expert system with
   * only AND nodes and backward chaining rules.
   * @param Semantic device template to be rediscovered.
   * @param Collection of semantic device templates to optimize the 
   * process behavior avoiding unnecessary repository access.
   * @return Collection of semantic device templates affected by rediscovery. 
   * Each template has the flag indicating if template is enabled or not. 
   */
  public Graph rediscover(Graph template, Set<Graph> rediscovered, Set<String> dependency) throws Exception {

    Graph runtime = template;
    if(template.value(Device.clonedFromTemplate) != null){
    }
    else{
      runtime = getRediscoveredTemplate(template, rediscovered);
      if(runtime == null) {
        return template;
      }
    }

    if(dependency.contains(runtime.getBaseURI())){
      addEvaluation(runtime, false);
      return runtime;
    }

  
    String evaluation = runtime.value(Device.evaluation);
    if(evaluation != null) {
      return runtime;
    }
  
    Set<Graph> preconditions = runtime.subGraphs(Device.hasPrecondition);
    boolean satisfied = true;
    Iterator<Graph> i = preconditions.iterator();
    while(i.hasNext() && satisfied){
      Set<String> dependencyFork = new HashSet<String>();
      dependencyFork.addAll(dependency);
      dependencyFork.add(runtime.getBaseURI());
      satisfied = satisfy(runtime, i.next(), rediscovered, dependencyFork); 
    }
    addEvaluation(runtime, satisfied);

    return runtime;
  }

  /**
   * Clones all graphs.
   * @param Graphs to clone.
   * @return Cloned graphs.
   */
  public Set<Graph> clone(Set<Graph> templates){
    ValueFactory f = repository.getValueFactory();
    Set<Graph> cloned = new HashSet<Graph>();
    for(Graph template: templates){
      cloned.add(template.clone(repository, template.getBaseURI()));
    }
    return cloned;
  }

  /**
   * When new device enters the network, all 
   * semantic devices must be rediscovered to keep relations
   * of embedded devices actual. Each semantic device template 
   * is rediscovered and the collection of embedded devices
   * (including embedded semantic devices) is refreshed
   * according to actual presence of devices in the run-time.
   * @param Device entering the network.
   * @return Discovered or rediscovered semantic devices.
   */
  public Set<Graph> resolveDevices(){
    removeRunTimeSemanticDevices();
    Set<Graph> templates = getSemanticDeviceTemplates();
    Set<Graph> rediscovered = clone(templates);
    try{
      for(Graph template: templates){
        rediscover(template, rediscovered, new HashSet<String>());
      }

      Set<Graph> success = new HashSet<Graph>();
      ValueFactory f = repository.getValueFactory();
      for(Graph d : rediscovered){
        String eval = d.value(Device.evaluation);
        if(getEvaluation(eval)){
          ResourceUtil.removeStatement(
              d.getBaseURI(), 
              Device.evaluation, 
              eval, 
              XMLSchema.BOOLEAN,
              f, 
              d);
          repository.store(d);
          success.add(d);
        }
      }

      PIDRuleResolver resolver = new PIDRuleResolver(repository);
      for(Graph discovered : success){
    	  String pid = discovered.value(Device.PID);
    	  if(pid != null)
    		  resolver.resolve(pid);
      }
      return success;
    }
    catch(Exception e){
      e.printStackTrace();
      return new HashSet<Graph>();
    }
  }

}
