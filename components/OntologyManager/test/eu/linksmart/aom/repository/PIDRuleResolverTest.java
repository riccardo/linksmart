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

import static org.junit.Assert.*;
import static org.mockito.Mockito.spy;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.openrdf.model.vocabulary.XMLSchema;


import eu.linksmart.aom.ontology.Graph;
import eu.linksmart.aom.ontology.GraphData;
import eu.linksmart.aom.ontology.GraphLoader;
import eu.linksmart.aom.ontology.model.Device;
import eu.linksmart.aom.ontology.model.Namespace;
import eu.linksmart.aom.ontology.schema.Property;
import eu.linksmart.aom.ontology.util.ResourceUtil;
import eu.linksmart.aom.repository.AOMRepository;
import eu.linksmart.aom.repository.PIDRuleResolver;
import eu.linksmart.aom.repository.RepositoryFactory;
import eu.linksmart.aom.repository.RuleResult;
import eu.linksmart.aom.testutil.DataLoader;
import eu.linksmart.aom.testutil.RepositoryUtil;

public class PIDRuleResolverTest {

  @Test
  public void testRuleResults(){
    String location = "resolveRules";
    AOMRepository repo = spy(RepositoryFactory.local(location));
    DataLoader.loadModel(repo, "test/resources/setup/PIDResolver");
    String templateURI = "http://template.uri";
    Graph template = createTemplate(repo, templateURI);

    String pid1 = "pid1";
    String pid2 = "pid2";
    String pid3 = "pid3";
    Graph device1 = createRuntime(template, pid1, repo);
    Graph device2 = createRuntime(template, pid2, repo);
    Graph device3 = createRuntime(template, pid3, repo);

    PIDRuleResolver resolver = new PIDRuleResolver(repo);

    Set<RuleResult> expected1 = new HashSet<RuleResult>();
    RuleResult r1_1 = new RuleResult(Namespace.application + "containsDevice");
    r1_1.inverseProperties.add(Namespace.application + "locatedIn");
    r1_1.resources.add(Namespace.application + "Hall_1");
    r1_1.resources.add(Namespace.application + "BedRoom_1");
    r1_1.resources.add(Namespace.application + "LivingRoom_1");

    RuleResult r1_2 = new RuleResult(Namespace.application + "ownsDevice");
    r1_2.inverseProperties.add(Namespace.application + "ownedBy");
    r1_2.resources.add(Namespace.application + "Resident_2");

    expected1.add(r1_1);
    expected1.add(r1_2);


    Set<RuleResult> expected2 = new HashSet<RuleResult>();
    RuleResult r2_1 = new RuleResult(Namespace.application + "containsDevice");
    r2_1.inverseProperties.add(Namespace.application + "locatedIn");
    r2_1.resources.add(Namespace.application + "LivingRoom_1");

    RuleResult r2_2 = new RuleResult(Namespace.application + "ownsDevice");
    r2_2.inverseProperties.add(Namespace.application + "ownedBy");
    r2_2.resources.add(Namespace.application + "Resident_1");
    r2_2.resources.add(Namespace.application + "Resident_2");
    r2_2.resources.add(Namespace.application + "MaintananceCrew_1");

    expected2.add(r2_1);
    expected2.add(r2_2);


    Set<RuleResult> expected3 = new HashSet<RuleResult>();
    RuleResult r3_1 = new RuleResult(Namespace.application + "ownsDevice");
    r3_1.inverseProperties.add(Namespace.application + "ownedBy");
    r3_1.resources.add(Namespace.application + "Resident_2");

    expected3.add(r3_1);

    Set<RuleResult> results1 = resolver.ruleResults(pid1);
    Set<RuleResult> results2 = resolver.ruleResults(pid2);
    Set<RuleResult> results3 = resolver.ruleResults(pid3);

    assertEquals(expected1, results1);
    assertEquals(expected2, results2);
    assertEquals(expected3, results3);

    repo.close();
    RepositoryUtil.clean(location);
  }

  @Test
  public void testResolve(){
    String location = "resolve";
    AOMRepository repo = spy(RepositoryFactory.local(location));
    DataLoader.loadModel(repo, "test/resources/setup/PIDResolver");
    String templateURI = "http://template.uri";
    Graph template = createTemplate(repo, templateURI);

    String pid1 = "pid1";
    String pid2 = "pid2";
    String pid3 = "pid3";
    Graph device1 = createRuntime(template, pid1, repo);
    Graph device2 = createRuntime(template, pid2, repo);
    Graph device3 = createRuntime(template, pid3, repo);

    PIDRuleResolver resolver = new PIDRuleResolver(repo);
    resolver.resolve(pid1);
    resolver.resolve(pid2);
    resolver.resolve(pid3);

    device1 = repo.getResource(device1.getBaseURI());
    device2 = repo.getResource(device2.getBaseURI());
    device3 = repo.getResource(device3.getBaseURI());

    assertEquals(
        new HashSet<String>(Arrays.asList(
            Namespace.application + "Hall_1",
            Namespace.application + "BedRoom_1",
            Namespace.application + "LivingRoom_1")),
            device1.values(new Property(Namespace.application + "locatedIn")));
    assertEquals(
        new HashSet<String>(Arrays.asList(
            Namespace.application + "Resident_2")),
            device1.values(new Property(Namespace.application + "ownedBy")));

    assertEquals(
        new HashSet<String>(Arrays.asList(
            Namespace.application + "LivingRoom_1")),
            device2.values(new Property(Namespace.application + "locatedIn")));
    assertEquals(
        new HashSet<String>(Arrays.asList(
            Namespace.application + "Resident_1",
            Namespace.application + "Resident_2",
            Namespace.application + "MaintananceCrew_1")),
            device2.values(new Property(Namespace.application + "ownedBy")));

    assertEquals(
        new HashSet<String>(),
            device3.values(new Property(Namespace.application + "locatedIn")));
    assertEquals(
        new HashSet<String>(Arrays.asList(
            Namespace.application + "Resident_2")),
            device3.values(new Property(Namespace.application + "ownedBy")));

    repo.close();
    RepositoryUtil.clean(location);
  }
  private Graph createTemplate(AOMRepository repo, String templateURI){
    Graph template = GraphLoader.load(
        templateURI, 
        GraphData.physicalDevice(templateURI, true));
    repo.store(template);
    return template;
  }

  private Graph createRuntime(Graph template, String pid, AOMRepository repo){
    Graph runtime = template.clone(repo, template.getBaseURI());
    ResourceUtil.addStatement(
        runtime.getBaseURI(), 
        Device.PID, 
        pid, 
        XMLSchema.STRING, 
        repo.getValueFactory(), 
        runtime);
    repo.store(runtime);
    return runtime;
  }

}