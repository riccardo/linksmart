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
package eu.linksmart.qos.computation;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.apache.log4j.Logger;

import eu.linksmart.aom.ApplicationOntologyManager;
import eu.linksmart.qos.client.request.Requirement;
import eu.linksmart.qos.client.request.Standard;
import eu.linksmart.qos.client.request.TraverseXMLRequest;
import eu.linksmart.qos.client.response.CreateXMLResponse;
import eu.linksmart.qos.client.response.Detail;
import eu.linksmart.qos.client.response.RankElement;
import eu.linksmart.qos.ontology.request.CreateOntologyXMLRequest;
import eu.linksmart.qos.ontology.request.Query4Ontology;
import eu.linksmart.qos.ontology.response.Device;
import eu.linksmart.qos.ontology.response.DeviceSortByPropertyValue;
import eu.linksmart.qos.ontology.response.OntologyResponseProperty;
import eu.linksmart.qos.ontology.response.TraverseOntologyXMLResponse;

/**
 * This class serves for performing QoS algorithm computation.
 * 
 * @author Amro Al-Akkad
 * 
 */
public class Computation {

	/**
	 * Logger.
	 */
	private static final Logger LOG =
			Logger.getLogger(Computation.class.getName());

	/**
	 * Used for logging.
	 */
	private static final String LOG_STARS = "****";

	/**
	 * Collection for mean requirements.
	 */
	private Vector<Requirement> meanRequirements;
	/**
	 * Collection for extreme requirements.
	 */
	private Vector<Requirement> extremeRequirements;

	/**
	 * Reason result.
	 */
	private ReasonResult reasonResult;

	/**
	 * Reference to ontology manager instance.
	 */
	private final ApplicationOntologyManager ontologyManager;

	/**
	 * Constructs a <b>Computation</b> instance.
	 * 
	 * @param ontologyManager
	 *            Ontology Manager.
	 */
	public Computation(ApplicationOntologyManager ontologyManager) {
		this.ontologyManager = ontologyManager;
	}

	/**
	 * @param messageInput
	 *            Message input, e.g.: <?xml version="1.0" encoding="UTF-8"
	 *            standalone="no"?> <request
	 *            xmlns="http://qos.linksmart.eu"> <serviceQualities>
	 *            <quality
	 *            >service:hasCapability;service:measuresTemperature</quality>
	 *            </serviceQualities> <requirements> <requirement>
	 *            <property>device
	 *            :hasEnergyProfile/energy:lifeTime/energy:startCost</property>
	 *            <standard>less</standard> </requirement> <requirement>
	 *            <property>device:hasHardware/hardware:hasSpeaker/hardware:
	 *            maxVolumeLevel</property> <standard>more</standard>
	 *            </requirement> <requirement>
	 *            <property>service:serviceCost</property>
	 *            <standard>less</standard> </requirement> <requirement>
	 *            <property>service:hasOutput/service:parameterUnit</property>
	 *            <standard>notNumeric</standard> <value>unit:Celsius</value>
	 *            </requirement> </requirements> </request>
	 * 
	 * @param onlyTopResult
	 *            Flag that indicates if only the best service (relevant for
	 *            <b>QoSManager.getBestSuitableService</b>) method should be
	 *            contained in QoS response or the whole ranking list (relevant
	 *            for <b>QoSManager.getRankingList</b>).
	 * 
	 * 
	 * @return Returns a XML based response string, e.g.: <?xml version="1.0"
	 *         encoding="UTF-8" standalone="no"?> <ResultList
	 *         xmlns="http://qos.linksmart.eu"> <Rank> <Position
	 *         Validness="qualified">1</Position> <DevicePID>Siemens
	 *         44</DevicePID> <DeviceURI>http://localhost/ontologies/Device.owl#
	 *         Thermometer_47151208_96f7_4f50_ad76_fcc76a2d8f5b
	 *         /RUNTIME_f1c308bf_1a9f_416b_bb64_ed65b9aed0c6</DeviceURI>
	 *         <ServiceOperation>GetIndoorTemperature,Siemens
	 *         44.</ServiceOperation> <Rate>5</Rate>
	 *         <AveragePercentage>50.0%</AveragePercentage> <Details> <Detail>
	 *         <Property
	 *         >device:hasEnergyProfile/energy:lifeTime/energy:startCost
	 *         </Property> <Value>99.0</Value> <Unit>Not_Specified</Unit>
	 *         </Detail> <Detail>
	 *         <Property>device:hasHardware/hardware:hasSpeaker
	 *         /hardware:maxVolumeLevel</Property> <Value>80.0</Value>
	 *         <Unit>Not_Specified</Unit> </Detail> <Detail>
	 *         <Property>service:serviceCost</Property> <Value>7.0</Value>
	 *         <Unit>unit:EUR</Unit> </Detail> <Detail>
	 *         <Property>service:hasOutput/service:parameterUnit</Property>
	 *         <Value>unit:Celsius</Value> <Unit>Not_Specified</Unit> </Detail>
	 *         </Details> </Rank> <Rank> <Position
	 *         Validness="qualified">2</Position> <DevicePID>TX 123</DevicePID>
	 *         <DeviceURI>http://localhost/ontologies/Device.owl#
	 *         Thermometer_6a77f56e_c3ab_409d_8d0f_b8fd27416a1f
	 *         /RUNTIME_084abb1f_e2d3_4c25_b24f_07ff9e6592fc</DeviceURI>
	 *         <ServiceOperation>GetIndoorTemperature,TX 123.</ServiceOperation>
	 *         <Rate>4</Rate> <AveragePercentage>25.0%</AveragePercentage>
	 *         <Details> <Detail>
	 *         <Property>device:hasEnergyProfile/energy:lifeTime
	 *         /energy:startCost</Property> <Value>34.0</Value>
	 *         <Unit>Not_Specified</Unit> </Detail> <Detail>
	 *         <Property>device:hasHardware
	 *         /hardware:hasSpeaker/hardware:maxVolumeLevel</Property>
	 *         <Value>30.0</Value> <Unit>Not_Specified</Unit> </Detail> <Detail>
	 *         <Property>service:serviceCost</Property> <Value>20.0</Value>
	 *         <Unit>Not_Specified</Unit> </Detail> <Detail>
	 *         <Property>service:hasOutput/service:parameterUnit</Property>
	 *         <Value>unit:Celsius</Value> <Unit>Not_Specified</Unit> </Detail>
	 *         </Details> </Rank> </ResultList>
	 * 
	 * 
	 */
	public String performAlgorithm(String messageInput, boolean onlyTopResult)
			throws RemoteException {

		String result = null;

		/* 1st traverse XML request input. */

		String[] serviceQualities;

		Requirement[] requirements;

		TraverseXMLRequest traverse = new TraverseXMLRequest();
		traverse.loadRequest(messageInput);

		serviceQualities = traverse.getServiceQualities();
		requirements = traverse.getRequirements();

		/* 2nd Create ReasonResult */
		this.reasonResult = new ReasonResult();

		/* 3rd Add mean and extreme requirements. */
		fillMeanAndExtremeRequirements(requirements);

		/*
		 * 4th Form query for each rule and get the result set for reasoning.
		 * store the answer in database for mean rules, store in table named
		 * parameter_mean, and extreme rules in table named parameter_extreme
		 * all tables are under database named userIDReasonResult.
		 */

		/* check if extremeRequirement */
		if (extremeRequirements.size() > 1) {
			LOG
					.error("Maximum one extreme requirement can be set in the request");
		} else {

			String xmlResponseFromOntology;
			xmlResponseFromOntology =
					processRetrievalFromOntology(serviceQualities, requirements);

			if (xmlResponseFromOntology != null) {

				TraverseOntologyXMLResponse traverseOntologyXMLResponse =
						new TraverseOntologyXMLResponse();

				traverseOntologyXMLResponse
						.loadRequest(xmlResponseFromOntology);

				List<Device> deviceList =
						traverseOntologyXMLResponse.getQueryResult();

				boolean eachDeviceHasProperty =
						checkIfEachDeviceHasProperty(deviceList);

				if (eachDeviceHasProperty) {

					boolean aux1 = processMeanRequirements(deviceList);

					if (aux1) {
						boolean aux2 = processExtremeRequirments(deviceList);

						if (aux2) {
							/* 5th perform algorithm. */
							Vector<String> serviceIDs = this.compute();

							// get the average percentage ranking according
							// to mean-rules
							this.average(serviceIDs);

							// take extremeRequirements into consideration
							this.decisionMaking();

							/* 6th Create resulting list. */
							Vector<RankElement> rankElementList =
									createResultList();

							int rangeOfResults;

							if (onlyTopResult) {
								rangeOfResults = 1;
							} else {
								rangeOfResults = rankElementList.size();
							}

							/* 7th create XML response */
							result =
							// createXmlResponse(rankElementList);
									createXmlResponse(rankElementList,
											rangeOfResults);
						}
					}
				}

			}
		}

		return result;

	}

	/**
	 * 
	 * @param messageInput
	 *            Represents specified service query and device/service
	 *            requirements.
	 * @return Returns specified QoS properties from LinkSmart Ontology.
	 */
	public String performGetQoSProperties(String messageInput) {

		String xmlResponseFromOntology = null;

		String[] serviceQualities;
		Requirement[] requirements;

		TraverseXMLRequest traverse = new TraverseXMLRequest();
		traverse.loadRequest(messageInput);
		serviceQualities = traverse.getServiceQualities();
		requirements = traverse.getRequirements();

		/* 2nd Create ReasonResult */
		this.reasonResult = new ReasonResult();

		/* 3rd Add mean and extreme requirements. */
		fillMeanAndExtremeRequirements(requirements);

		/*
		 * 4th Form query for each rule and get the result set for reasoning.
		 * store the answer in database for mean rules, store in table named
		 * parameter_mean, and extreme rules in table named parameter_extreme
		 * all tables are under database named userIDReasonResult.
		 */

		/* check if extremeRequirement */
		if (extremeRequirements.size() > 1) {
			LOG
					.error("Maximum one extreme requirement can be set in the request");
		} else {

			xmlResponseFromOntology =
					processRetrievalFromOntology(serviceQualities, requirements);
		}
		return xmlResponseFromOntology;
	}

	/**
	 * 
	 * @param serviceQualities
	 *            Specified service qualities.
	 * @param requirements
	 *            Specified service/device requirements.
	 * @return Returns retrieval from LinkSmart ontology.
	 */
	private String processRetrievalFromOntology(String[] serviceQualities,
			Requirement[] requirements) {
		Query4Ontology requestXMLForOntology =
				new CreateOntologyXMLRequest().buildRequest(serviceQualities,
						requirements);

		String response = null;

		try {
			response =
					ontologyManager.getDevicesWithServices(
							requestXMLForOntology.getServiceQuery(),
							requestXMLForOntology.getDeviceQuery(),
							requestXMLForOntology.getDeviceRequirements(),
							requestXMLForOntology.getServiceRequirements());

			if (response == null) {
				LOG.error("Response from ontology was 'null'.");
			} else {
				LOG.info(LOG_STARS);
				LOG.info("responseFromOntology:");
				LOG.info(response);
				LOG.info(LOG_STARS);
			}
		} catch (RemoteException e) {
			LOG.error(e.getMessage(), e.getCause());
		}
		return response;
	}

	/**
	 * Process final decision, i.e. taking into consideration extreme
	 * requirements for computation.
	 */
	private void decisionMaking() {

		// service names providing all requirements are collected here
		Vector<String> nn = new Vector<String>();
		Vector<String> enames = new Vector<String>();
		for (int i = 0; i < extremeRequirements.size(); i++) {

			List<Req> specificExtremeRequirementList =
					this.reasonResult
							.getSpecificExtremeRequirementList(extremeRequirements
									.elementAt(i).getNfParameter());

			for (Req req : specificExtremeRequirementList) {
				String en = req.getUniqueID();
				if (extremeRequirements.size() == 1) {
					nn.add(en);
				}
				if (i == 0) {
					enames.add(en);
				} else {
					if (enames.contains(en)) {
						nn.add(en);
					}

				}
			}

		}

		this.getDecisionResult(nn);
	}

	/**
	 * Process average calculation, i.e. how well a device and related service
	 * match the requirements.
	 * 
	 * @param serviceIDs
	 *            Service IDs.
	 */
	private void average(Vector<String> serviceIDs) {

		this.reasonResult.getAverageMeanPercentageList().clear();
		Float avp = new Float(0);
		for (int k = 0; k < serviceIDs.size(); k++) {

			String devicePID = null;
			String deviceURI = null;
			boolean isDisqualified = false;

			for (int i = 0; i < meanRequirements.size(); i++) {

				List<Req> specificMeanReqList =
						this.reasonResult
								.getSpecificMeanRequirementList(meanRequirements
										.elementAt(i).getNfParameter());

				boolean aux = false;
				int w = 0;
				while (w < specificMeanReqList.size() && !aux) {
					Req meanReq = specificMeanReqList.get(w);

					if (meanReq.getUniqueID().equals(serviceIDs.elementAt(k))) {
						Req rc = specificMeanReqList.get(w);

						avp =
								avp
										+ Float.valueOf(String.valueOf(rc
												.getPercentage()));
						devicePID = rc.getDevicePID();
						deviceURI = rc.getDeviceURI();
						isDisqualified = rc.isDisqualified();
						aux = true;
					}

					w++;
				}
			}

			avp = avp / meanRequirements.size();
			BigDecimal average = calculateAverage(avp);
			this.reasonResult.addAverageMeanPercentage((String) serviceIDs
					.elementAt(k), devicePID, deviceURI, average.doubleValue(),
					isDisqualified);

		}
		List<Req> auxEL = this.reasonResult.getAuxExtremeRequirementList();

		// massage adaption of percentages in averageMeanPercentage
		for (int i = 0; i < auxEL.size(); i++) {
			Req req = auxEL.get(i);
			double percentageExtremeReq = req.getPercentage();

			List<ResultElement> avmpList =
					this.reasonResult.getSpecificAverageMeanPercentageList(req
							.getUniqueID());

			double previousPercentageAggregated =
					avmpList.get(0).getAveragePercentage();
			int actualLength =
					this.meanRequirements.size()
							+ this.extremeRequirements.size();
			double calculatedValue =
					((previousPercentageAggregated * this.meanRequirements
							.size()) + percentageExtremeReq)
							/ actualLength;
			avmpList.get(0).setAveragePercentage(calculatedValue);
		}

		// Part 2

		List<ResultElement> originalList =
				this.reasonResult.getAverageMeanPercentageList();
		List<ResultElement> workList =
				new ArrayList<ResultElement>(originalList.size());
		// Collections.copy(workList,originalList);
		for (int p = 0; p < originalList.size(); p++) {
			workList.add(originalList.get(p));
		}
		Collections.sort(workList, new ResultElementSortByAveragePercentage());
		Vector<String> finalmeansn = new Vector<String>();
		Vector<String> finalmeansd = new Vector<String>();
		Vector<String> finalmeansdURI = new Vector<String>();
		Vector<String> finalmeansp = new Vector<String>();
		Vector<Boolean> finalIsDis = new Vector<Boolean>();

		for (ResultElement re : workList) {
			String n = re.getServiceName();
			String d = re.getDevicePID();
			String dURI = re.getDeviceURI();
			String p = String.valueOf(re.getAveragePercentage());
			boolean dis = re.isDisqualified();

			finalmeansn.add(n);
			finalmeansd.add(d);
			finalmeansdURI.add(dURI);
			finalmeansp.add(p);
			finalIsDis.add(dis);
		}

		this.reasonResult.getAverageMeanPercentageList().clear();

		for (int i = 0; i < finalmeansn.size(); i++) {
			this.reasonResult.addAverageMeanPercentage((String) finalmeansn
					.elementAt(i), (String) finalmeansd.elementAt(i),
					(String) finalmeansdURI.elementAt(i), Double
							.parseDouble((String) finalmeansp.elementAt(i)),
					(Boolean) finalIsDis.elementAt(i));
		}

	}

	/**
	 * Calculates the average.
	 * 
	 * @param avp
	 *            Average percentage.
	 * @return Returns the calculated average as <b>BigDecimal</b>.
	 */
	private BigDecimal calculateAverage(Float avp) {
		return new BigDecimal(avp).divide(BigDecimal.ONE, 2, 2);
	}

	/**
	 * Performs the actual computation.
	 * 
	 * @return Returns a collection of service IDs.
	 */
	private Vector<String> compute() {

		Vector<String> serviceIDs = new Vector<String>();

		try {

			BigDecimal bd0 = new BigDecimal(100);
			BigDecimal bd = BigDecimal.ZERO;
			BigDecimal bd1 = BigDecimal.ONE;
			BigDecimal bd50 = new BigDecimal(50);
			BigDecimal zero = BigDecimal.ZERO;

			Boolean flag50 = false;
			Vector<String> serviceNames = new Vector<String>();

			Vector<Double> values = new Vector<Double>();
			Vector<BigDecimal> percentages = new Vector<BigDecimal>();

			for (int i = 0; i < meanRequirements.size(); i++) {

				List<Req> specificList =
						this.reasonResult
								.getSpecificMeanRequirementList(meanRequirements
										.elementAt(i).getNfParameter());

				for (Req specificReq : specificList) {
					String fn = specificReq.getUniqueID();
					serviceNames.add(fn);
					values.add(specificReq.getValue());
					if (!serviceIDs.contains(fn)) {
						serviceIDs.add(fn);
					}
				}

				BigDecimal highestValue = getHighestValue(values);
				BigDecimal lowestValue = getLowestValue(values);
				BigDecimal difference =
						getDifference(highestValue, lowestValue);

				for (int j = 0; j < values.size(); j++) {
					BigDecimal v = getValueByIndex(values, j);

					BigDecimal percentage;
					if (difference.equals(zero)) {
						percentage = bd0;
					} else {
						BigDecimal vh = getDifference(v, highestValue);
						LOG.debug(difference + " " + vh);
						if (flag50) {
							percentage =
									bd0.subtract(
											(bd50.divide(difference, 2, 2)
													.multiply(vh))).divide(bd1,
											2, 2);
							percentages.add(percentage);
						} else {
							percentage =
									bd0.subtract(
											(bd0.divide(difference, 2, 2)
													.multiply(vh))).divide(bd1,
											2, 2);

							if (percentage.floatValue() < 0) {
								percentage = bd;
							}
							percentages.add(percentage);
						}
					}
				}

				for (int q = 0; q < percentages.size(); q++) {

					double pc =
							Double.parseDouble(percentages.elementAt(q)
									.toString());

					specificList.get(q).setPercentage(pc);
				}

				flag50 = false;
				serviceNames.clear();
				values.clear();
				percentages.clear();
			}

			/* */
			if (extremeRequirements.size() == 1) {
				List<Req> auxEL =
						this.reasonResult.getAuxExtremeRequirementList();

				for (Req req : auxEL) {

					String fn = req.getUniqueID();
					serviceNames.add(fn);

					values.add(req.getValue());

					if (!serviceIDs.contains(fn)) {
						serviceIDs.add(fn);
					}

				}

				BigDecimal h = getHighestValue(values);
				BigDecimal l = getLowestValue(values);
				BigDecimal hl = getDifference(h, l);

				for (int j = 0; j < values.size(); j++) {
					BigDecimal v = getValueByIndex(values, j);

					BigDecimal percentage;
					if (hl.equals(zero)) {
						percentage = bd0;
					} else {
						BigDecimal vh = getDifference(v, h);
						LOG.debug(hl + " " + vh);
						if (flag50) {
							percentage =
									bd0
											.subtract(
													(bd50.divide(hl, 2, 2)
															.multiply(vh)))
											.divide(bd1, 2, 2);
							LOG.debug("percentage" + percentage);
							percentages.add(percentage);
						} else {
							percentage =
									bd0
											.subtract(
													(bd0.divide(hl, 2, 2)
															.multiply(vh)))
											.divide(bd1, 2, 2);
							LOG.debug("percentage" + percentage);
							if (percentage.floatValue() < 0) {
								percentage = bd;
							}
							percentages.add(percentage);
						}
					}
				}

				for (int q = 0; q < percentages.size(); q++) {

					double pc =
							Double.parseDouble(percentages.elementAt(q)
									.toString());

					auxEL.get(q).setPercentage(pc);
				}
				// later do massage when meanAveragePercentage list is filled

				flag50 = false;
				serviceNames.clear();
				values.clear();
				percentages.clear();

			}

		} catch (ArrayIndexOutOfBoundsException e) {
			LOG.error(e.getMessage(), e.getCause());
		}

		return serviceIDs;

	}

	/**
	 * @param values
	 *            Collection of values as <b>Double</b>.
	 * @param index
	 *            Index.
	 * @return Returns the specified value as <b>BigDecimal</b>.
	 */
	private BigDecimal getValueByIndex(Vector<Double> values, int index) {
		return new BigDecimal(Float.valueOf((String) values.elementAt(index)
				.toString()));
	}

	/**
	 * @param highestValue
	 *            Highest retrieved value.
	 * @param lowestValue
	 *            Lowest retrieved value.
	 * @return Returns the difference between highest and lowest value.
	 */
	private BigDecimal getDifference(BigDecimal highestValue,
			BigDecimal lowestValue) {
		return highestValue.subtract(lowestValue).abs();
	}

	/**
	 * @param values
	 *            Collection of <b>Double</b> value.
	 * @return Returns the lowest value of the collection.
	 */
	private BigDecimal getLowestValue(Vector<Double> values) {
		return new BigDecimal(Float.valueOf((String) (values.elementAt(values
				.size() - 1).toString())));
	}

	/**
	 * @param values
	 *            Collection of <b>Double</b> value.
	 * @return Returns the highest value of the collection.
	 */
	private BigDecimal getHighestValue(Vector<Double> values) {
		return new BigDecimal(Float.valueOf((String) (values.elementAt(0))
				.toString()));
	}

	/**
	 * Processes final decision step.
	 * 
	 * @param namesOfBestSuitableServices
	 *            Collection containing the name of the best suitable services.
	 */
	private void getDecisionResult(Vector<String> namesOfBestSuitableServices) {

		Vector<String> finaln = new Vector<String>();
		Vector<String> finald = new Vector<String>();
		Vector<String> finaldURI = new Vector<String>();
		Vector<String> finalp = new Vector<String>();
		Vector<Boolean> finalIsDis = new Vector<Boolean>();

		if (namesOfBestSuitableServices.isEmpty()) {
			LOG
					.error("No extreme rule or can not find service fullfil multi extreme rules, suggest reduce one extreme rule and try again");
		} else {

			List<ResultElement> specificAMPList;

			for (int k = 0; k < namesOfBestSuitableServices.size(); k++) {

				specificAMPList =
						this.reasonResult
								.getSpecificAverageMeanPercentageList(namesOfBestSuitableServices
										.elementAt(k).toString());

				for (int h = 0; h < specificAMPList.size(); h++) {
					String n = specificAMPList.get(h).getServiceName();
					String d = specificAMPList.get(h).getDevicePID();
					String dURI = specificAMPList.get(h).getDeviceURI();
					boolean isDis = specificAMPList.get(h).isDisqualified();
					String p =
							String.valueOf(specificAMPList.get(h)
									.getAveragePercentage());
					finaln.add(n);
					finald.add(d);
					finaldURI.add(dURI);
					finalp.add(p);
					finalIsDis.add(isDis);
				}

			}
			for (int i = 0; i < finaln.size(); i++) {

				this.reasonResult.addMiddleResult(finaln.elementAt(i), finald
						.elementAt(i), finaldURI.elementAt(i), Double
						.parseDouble(((String) finalp.elementAt(i))),
						finalIsDis.elementAt(i));
			}
			finaln.clear();
			finald.clear();
			finaldURI.clear();
			finalp.clear();

			List<ResultElement> originalList =
					this.reasonResult.getMiddleResultList();
			List<ResultElement> workList =
					new ArrayList<ResultElement>(originalList.size());
			// Collections.copy(originalList, workList);
			for (int z = 0; z < originalList.size(); z++) {
				workList.add(originalList.get(z));
			}
			Collections.sort(workList,
					new ResultElementSortByAveragePercentage());

			for (ResultElement resultElement : workList) {
				finaln.add(resultElement.getServiceName());
				finald.add(resultElement.getDevicePID());
				finaldURI.add(resultElement.getDeviceURI());
				finalp
						.add(String.valueOf(resultElement
								.getAveragePercentage()));
			}

			for (int i = 0; i < finaln.size(); i++) {

				String serviceName = finaln.elementAt(i).toString();

				specificAMPList =
						this.reasonResult
								.getSpecificAverageMeanPercentageList(serviceName);

				for (int u = 0; u < specificAMPList.size(); u++) {
					this.reasonResult.getAverageMeanPercentageList().remove(
							specificAMPList.get(u));
				}

			}
		}

		// Part 2/2

		List<ResultElement> ampl =
				this.reasonResult.getAverageMeanPercentageList();

		for (ResultElement re : ampl) {
			finaln.addElement(re.getServiceName());
			finald.addElement(re.getDevicePID());
			finaldURI.addElement(re.getDeviceURI());
			finalp.addElement(String.valueOf(re.getAveragePercentage()));
			finalIsDis.addElement(re.isDisqualified());
		}

		for (int i = 0; i < finaln.size(); i++) {
			this.reasonResult.addSelectResult(finaln.elementAt(i), finald
					.elementAt(i), finaldURI.elementAt(i), Double
					.parseDouble(finalp.elementAt(i)), finalIsDis.elementAt(i));
		}

		// final recheck if more than one service have highest extreme property
		List<String> checkList = checkExtremeHighestPropertyValueIsOverloaded();

		if (checkList.size() > 1) {
			List<ResultElement> selectResultList =
					this.reasonResult.getSelectResultList();
			// do adaption
			for (int i = 1; i < checkList.size(); i++) {
				String serviceName = checkList.get(i);
				ResultElement oldResultElement =
						this.reasonResult
								.getSpecificSelectResultElement(serviceName);

				if (oldResultElement.getAveragePercentage() > selectResultList
						.get(0).getAveragePercentage()) {

					addNewResultElement(selectResultList, oldResultElement);

					LOG.info("Inserted to the pole position");
				}

			}
		}

	}

	/**
	 * @param selectResultList
	 *            Select result list.
	 * @param oldResultElement
	 *            Old result element.
	 */
	private void addNewResultElement(List<ResultElement> selectResultList,
			ResultElement oldResultElement) {
		ResultElement newResultElement = new ResultElement();
		ResultElement.copy(oldResultElement, newResultElement);
		selectResultList.remove(oldResultElement);

		selectResultList.add(0, newResultElement);
	}

	/**
	 * @return Returns a list of strings of service names.
	 */
	private List<String> checkExtremeHighestPropertyValueIsOverloaded() {

		List<String> serviceNames = new ArrayList<String>();

		List<Req> auxExRegList =
				this.reasonResult.getAuxExtremeRequirementList();

		List<Req> extremeReqList =
				this.reasonResult.getExtremeRequirementList();

		if (!extremeReqList.isEmpty()) {
			Req currentBestValElement = extremeReqList.get(0);

			double bestVal = currentBestValElement.getValue();

			serviceNames.add(currentBestValElement.getUniqueID());

			// Start at index 1, as 0 is already processed some lines before
			for (int i = 1; i < auxExRegList.size(); i++) {
				Req req = auxExRegList.get(i);
				double value = req.getValue();
				// value will be same, more or less is impossible
				if (value == bestVal) {
					serviceNames.add(req.getUniqueID());
				}
			}
		}

		return serviceNames;
	}

	/**
	 * @param servicename
	 *            Service name.
	 * @return Returns details elements in order to create XML response.
	 */
	private Vector<Detail> getDetails(String servicename) {
		Vector<String> mdetailnames = new Vector<String>();
		Vector<String> edetailnames = new Vector<String>();
		Vector<Detail> details = new Vector<Detail>();

		for (Requirement meanRequirement : this.meanRequirements) {
			mdetailnames.add(meanRequirement.getNfParameter());
		}

		for (Requirement extremeRequirement : this.extremeRequirements) {
			edetailnames.add(extremeRequirement.getNfParameter());
		}

		// **//

		// part 2/3 mean requirements
		for (int i = 0; i < mdetailnames.size(); i++) {

			List<Req> specificList =
					this.reasonResult
							.getSpecificMeanRequirementList(mdetailnames
									.elementAt(i));

			boolean aux = false;
			int y = 0;
			while (y < specificList.size() && !aux) {
				Req req = specificList.get(y);

				if (req.getUniqueID().equals(servicename)) {
					addDetailToList(mdetailnames, details, i, req);
					aux = true;
				}
				y++;
			}

		}

		// part 3/3 extreme requirements
		if (!edetailnames.isEmpty()) {
			Req req =
					this.reasonResult
							.getSpecificAuxExtremeMeanPercentageList(servicename);
			Detail detail = new Detail();
			detail.setParameter(edetailnames.elementAt(0));
			String notNumericValue = req.getNotNumericValue();
			if (notNumericValue == null)
				detail.setValue(String.valueOf(req.getValue()));
			else
				detail.setValue(notNumericValue);
			detail.setUnit(req.getUnit());
			details.add(detail);
		}

		return details;
	}

	/**
	 * @param meanRequirementsNames
	 *            Names of mean requirements.
	 * @param details
	 *            Collection for <b>Details</b>.
	 * @param i
	 *            Index.
	 * @param req
	 *            Requirement.
	 */
	private void addDetailToList(Vector<String> meanRequirementsNames,
			Vector<Detail> details, int i, Req req) {

		Detail detail = new Detail();
		detail.setParameter(meanRequirementsNames.elementAt(i));
		String notNumericValue = req.getNotNumericValue();
		if (notNumericValue == null)
			detail.setValue(String.valueOf(req.getValue()));
		else
			detail.setValue(notNumericValue);

		detail.setUnit(req.getUnit());
		details.add(detail);
	}

	/**
	 * @param deviceList
	 *            List of devices.
	 * @return Returns TRUE if each device has at least 1 property.
	 */
	private boolean checkIfEachDeviceHasProperty(List<Device> deviceList) {

		for (Device device : deviceList) {

			int sizeOfProperties =
					device.getProperties().length
							+ device.getServices()[0].getServiceProperties().length;

			if (sizeOfProperties < 1) {
				LOG.error("Device '" + device.getDeviceURI()
						+ "' has no properties.");
				return false;
			}
		}

		return true;

	}

	/**
	 * @param deviceList
	 *            Device list.
	 * @return Returns TRUE if processing is smoothly, i.e. the specified
	 *         extreme requirements can be mapped to <b>Req</b> in the QoS
	 *         model.
	 */
	private boolean processExtremeRequirments(List<Device> deviceList) {

		if (extremeRequirements.size() == 1) {
			String extremePropertyName =
					extremeRequirements.get(0).getNfParameter();
			Standard extremePropertyStandard =
					extremeRequirements.get(0).getStandard();
			String extremePropertyStandardValue =
					extremeRequirements.get(0).getStandardValue();

			// first copy list
			List<Device> sortedDeviceList = new ArrayList<Device>();

			for (int i = 0; i < deviceList.size(); i++) {
				sortedDeviceList.add(deviceList.get(i));
			}

			// sort list according to property standard
			if (extremePropertyStandard == Standard.least) {
				Collections
						.sort(sortedDeviceList, new DeviceSortByPropertyValue(
								extremePropertyName, true));
			} else if (extremePropertyStandard == Standard.most) {

				Collections.sort(sortedDeviceList,
						new DeviceSortByPropertyValue(extremePropertyName,
								false));
			} else if (extremePropertyStandard == Standard.notNumeric) {

				adaptSortedList4NotNumeric(sortedDeviceList,
						extremePropertyName, extremePropertyStandardValue);
			}

			// add all elements to aux extreme props
			for (int a = 0; a < sortedDeviceList.size(); a++) {
				Device device = sortedDeviceList.get(a);

				String[] requestedValues =
						retrieveDesiredValueAndUnit(device, extremePropertyName);

				String requestedPropertyValue = null, requestedPropertyUnit =
						null;

				if (requestedValues.length > 0) {
					requestedPropertyValue = requestedValues[0];
					requestedPropertyUnit = requestedValues[1];
				} else {
					LOG
							.error("Ontology response does not include requested requirement.");
					return false;
				}

				this.reasonResult.addAuxExtremeRequirement(extremePropertyName,
						device.getServices()[0].getOperation(),
						requestedPropertyValue, requestedPropertyUnit, device
								.getPid(), device.getDeviceURI(), device
								.isDisqualified());

			}

			// add only first element to reason result extreme properties

			Device device = sortedDeviceList.get(0);

			String[] requestedValues =
					retrieveDesiredValueAndUnit(device, extremePropertyName);

			String requestedPropertyValue = null, requestedPropertyUnit = null;

			if (requestedValues.length > 0) {
				requestedPropertyValue = requestedValues[0];
				requestedPropertyUnit = requestedValues[1];
			}

			this.reasonResult.addExtremeRequirement(extremePropertyName, device
					.getServices()[0].getOperation(), requestedPropertyValue,
					requestedPropertyUnit, device.getPid(), device
							.getDeviceURI(), device.isDisqualified());

			return true;
		}

		return false;
	}

	/**
	 * Process mean requirements, i.e. maps them to <b>Req</b> in the QoS model
	 * preparatively for performing the computation.
	 * 
	 * @param deviceList
	 *            List of devices.
	 * @return Returns TRUE if processing worked smoothly, and FALSE if not.
	 */
	private boolean processMeanRequirements(List<Device> deviceList) {

		List<Device> sortedDeviceList = new ArrayList<Device>();

		for (int i = 0; i < meanRequirements.size(); i++) {
			// In QoS request we call it requirement,
			// but in Ontology request it is called property.
			String propertyName = meanRequirements.get(i).getNfParameter();
			Standard propertyStandard = meanRequirements.get(i).getStandard();
			String propertyStandardValue =
					meanRequirements.get(i).getStandardValue();

			// first copy list
			for (int j1 = 0; j1 < deviceList.size(); j1++) {
				sortedDeviceList.add(deviceList.get(j1));
			}

			// sort list according to property standard
			if (propertyStandard == Standard.less) {
				Collections.sort(sortedDeviceList,
						getDeviceSortByPropertyValue(propertyName, true));
			} else if (propertyStandard == Standard.more) {

				Collections.sort(sortedDeviceList,
						getDeviceSortByPropertyValue(propertyName, false));
			} else if (propertyStandard == Standard.notNumeric) {
				if (propertyStandardValue != null
						&& propertyStandardValue.length() > 0) {
					adaptSortedList4NotNumeric(sortedDeviceList, propertyName,
							propertyStandardValue);
				} else {
					LOG.error("Not numeric value does not contain any value.");
					return false;
				}
			}

			// add elements to reasonResult meanReqs
			for (int j2 = 0; j2 < sortedDeviceList.size(); j2++) {

				Device device = sortedDeviceList.get(j2);

				String[] requestedValues =
						retrieveDesiredValueAndUnit(device, propertyName);

				String requestedPropertyValue = null, requestedPropertyUnit =
						null;

				if (requestedValues.length > 0) {
					requestedPropertyValue = requestedValues[0];
					requestedPropertyUnit = requestedValues[1];
				} else {
					LOG
							.error("Ontology response does not include requested requirement.");
					return false;
				}

				this.reasonResult.addMeanRequirement(propertyName, device
						.getServices()[0].getOperation(),
						requestedPropertyValue, requestedPropertyUnit, device
								.getPid(), device.getDeviceURI(), device
								.isDisqualified());
			}

		}
		return true;

	}

	/**
	 * @param propertyName
	 *            Property name.
	 * @param ascending
	 *            The order to sort on. If TRUE is passed the sorting will be
	 *            ascending, if FALSE it will be descending.
	 * @return Returns the comparator to sort device by a property value.
	 */
	private Comparator<? super Device> getDeviceSortByPropertyValue(
			String propertyName, boolean ascending) {
		return new DeviceSortByPropertyValue(propertyName, ascending);
	}

	/**
	 * Adapts the sorted list, i.e. devices/services that do not match a not
	 * numeric criteria are marked as disqualified.
	 * 
	 * @param sortedDeviceList
	 *            Sorted list of devices.
	 * @param propertyName
	 *            Name of property.
	 * @param propertyStandardValue
	 *            Standard value of property.
	 */
	private void adaptSortedList4NotNumeric(List<Device> sortedDeviceList,
			String propertyName, String propertyStandardValue) {

		for (Device device : sortedDeviceList) {
			String retrievedValueFromDevice =
					retrieveDesiredValueOfNonNumericProperty(device,
							propertyName);

			if (!propertyStandardValue.equals(retrievedValueFromDevice))
				device.setDisqualified(true);
		}

	}

	/**
	 * @param device
	 *            Device.
	 * @param propertyName
	 *            Property name.
	 * @return Returns a string array that contains the device/service property
	 *         name and its value. If such a device/service is not found, an
	 *         empty string array is returned.
	 */
	private String[] retrieveDesiredValueAndUnit(Device device,
			String propertyName) {

		OntologyResponseProperty[] deviceProperties = device.getProperties();

		for (OntologyResponseProperty deviceProperty : deviceProperties) {

			// Trick is needed here, as in response, all overhead description is
			// shortened, we just need to look up, if the main property name is
			// contained in the request,
			// e.g. we need to look if
			// "device:hasHardware/hardware:hasDisplay/hardware:screenWidth"
			// contains "hardware:screenWidth".
			if (propertyName.contains(deviceProperty.getName())) {
				return new String[] { deviceProperty.getValue(),
						deviceProperty.getUnit() };
			}
		}

		// If not found in device properties, look up service properties

		OntologyResponseProperty[] serviceProperties =
				device.getServices()[0].getServiceProperties();

		for (OntologyResponseProperty serviceProperty : serviceProperties) {

			if (propertyName.contains(serviceProperty.getName())) {
				return new String[] { serviceProperty.getValue(),
						serviceProperty.getUnit() };
			}
		}

		return new String[] {};

	}

	/**
	 * @param device
	 *            Device.
	 * @param desiredPropertyName
	 *            Desired property name.
	 * @return Returns a specific value of a non numeric property, and NULL if
	 *         the property is not found.
	 */
	private String retrieveDesiredValueOfNonNumericProperty(Device device,
			String desiredPropertyName) {

		OntologyResponseProperty[] deviceProperties = device.getProperties();

		for (OntologyResponseProperty deviceProperty : deviceProperties) {
			if (desiredPropertyName.contains(deviceProperty.getName())) {
				return deviceProperty.getValue();
			}
		}

		// if not found check also service related properties

		OntologyResponseProperty[] serviceProperties =
				device.getServices()[0].getServiceProperties();

		for (OntologyResponseProperty serviceProperty : serviceProperties) {
			if (desiredPropertyName.contains(serviceProperty.getName())) {
				return serviceProperty.getValue();
			}
		}

		// if not found at all return NULL
		return null;

	}

	/**
	 * Fills preparatively the mean and extreme requirements for computation.
	 * 
	 * @param requirements
	 *            Requirements.
	 */
	private void fillMeanAndExtremeRequirements(Requirement[] requirements) {
		meanRequirements = new Vector<Requirement>();
		extremeRequirements = new Vector<Requirement>();
		// collect mean rules and extreme rules
		for (int i = 0; i < requirements.length; i++) {
			if (requirements[i].getStandard().equals(Standard.most)
					|| requirements[i].getStandard().equals(Standard.least)
					|| requirements[i].getStandard().equals(Standard.sameAs)) {
				extremeRequirements.add(requirements[i]);
			} else {
				/* i.e. more, less, more than, less than */
				meanRequirements.add(requirements[i]);
			}
		}
	}

	/**
	 * @return Returns a result list.
	 */
	private Vector<RankElement> createResultList() {
		Vector<RankElement> rankElementList = new Vector<RankElement>();

		List<ResultElement> selectResultList =
				this.reasonResult.getSelectResultList();

		for (int i = 0; i < selectResultList.size(); i++) {
			ResultElement selectResult = selectResultList.get(i);
			addRankElementToList(rankElementList, selectResult);
		}

		return rankElementList;
	}

	/**
	 * @param rankElementList
	 *            RankElement list.
	 * @param selectResult
	 *            Select result.
	 */
	private void addRankElementToList(Vector<RankElement> rankElementList,
			ResultElement selectResult) {
		RankElement rankElement = new RankElement();
		rankElement.setServicename(selectResult.getServiceName());
		rankElement.setDevicePID(selectResult.getDevicePID());
		rankElement.setAverage(String.valueOf(selectResult
				.getAveragePercentage()));
		rankElement.setDeviceURI(selectResult.getDeviceURI());

		if (selectResult.isDisqualified())
			rankElement.setDisqualified(true);

		rankElementList.add(rankElement);
	}

	/**
	 * @param rankElementList
	 *            A collection of rank element.
	 * @param range
	 *            Range to execute rank, i.e. between 1 and lists.size.
	 * @return Returns the final XML based result string.
	 */
	private String createXmlResponse(Vector<RankElement> rankElementList,
			int range) {
		Vector<Vector<Detail>> rankRecordDetails = new Vector<Vector<Detail>>();
		for (int i = 0; i < rankElementList.size(); i++) {
			rankRecordDetails.add(this.getDetails(rankElementList.elementAt(i)
					.getServicename()));
		}
		CreateXMLResponse xmlResponseCreator = new CreateXMLResponse();

		String xmlResultString = null;
		try {
			xmlResultString =
					xmlResponseCreator.executeRank(rankElementList,
							rankRecordDetails, range);
		} catch (ParserConfigurationException e) {
			LOG.error(e.getMessage());
		} catch (TransformerFactoryConfigurationError e) {
			LOG.error(e.getMessage());
		} catch (TransformerException e) {
			LOG.error(e.getMessage());
		}

		return xmlResultString;
	}

}
