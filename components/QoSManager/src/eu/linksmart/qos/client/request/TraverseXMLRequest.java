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
package eu.linksmart.qos.client.request;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Class is used to traverse the XML-based request of the QoS client.
 * 
 * @author Amro Al-Akkad
 * 
 */
public class TraverseXMLRequest {

	/**
	 * Logger.
	 */
	private static final Logger LOG =
			Logger.getLogger(TraverseXMLRequest.class.getName());

	/**
	 * Keeps service qualities, i.e. service query the QoS client specified.
	 */
	private String[] serviceQualities;

	/**
	 * Keeps requirements the QoS client specified.
	 */
	private Requirement[] requirements;

	/**
	 * Creates a DOM of the QoS client request in order to traverse content.
	 * 
	 * @param xmlString
	 *            XML based request string
	 * @return Returns a <b>Document</b> element
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public Document createDomOfRequest(String xmlString)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();

		byte[] bytes = xmlString.getBytes();

		InputStream inputStream = new ByteArrayInputStream(bytes);
		Document document = db.parse(inputStream);

		return document;

	}

	/**
	 * Gets the root node of the document
	 * 
	 * @param doc
	 *            Document.
	 * @return Returns the root node of the document.
	 */
	public Node getDocumentRootNode(Document doc) {

		NodeList children = doc.getChildNodes();

		if (children.getLength() >= 1) {
			Node root;

			if (children.getLength() == 1)
				root = children.item(0);
			else
				root = children.item(1);

			return root;
		} else
			return null;
	}

	/**
	 * Getter for requirements.
	 * 
	 * @return Returns the requirements the QoS client specified.
	 */
	public Requirement[] getRequirements() {
		return Arrays.copyOfRange(requirements, 0, requirements.length);
	}

	/**
	 * Getter for service qualities, i.e. service query the QoS Manager client
	 * specified.
	 * 
	 * @return Returns service qualities.
	 */
	public String[] getServiceQualities() {
		return Arrays.copyOfRange(serviceQualities, 0, serviceQualities.length);
	}

	/**
	 * This parses the request of the QoS Manager client
	 * 
	 * @param xmlString
	 *            A XML based request string, e.g.: <?xml version="1.0"
	 *            encoding="UTF-8" standalone="no"?> <request
	 *            xmlns="http://qos.linksmart.eu"> <serviceQualities>
	 *            <quality>service:hasCapability;service:playsVideo</quality>
	 *            </serviceQualities> <requirements> <requirement>
	 *            <property>device
	 *            :hasHardware/hardware:hasDisplay/hardware:screenWidth
	 *            </property> <standard>more</standard> </requirement>
	 *            <requirement>
	 *            <property>device:hasHardware/hardware:hasDisplay/
	 *            hardware:screenHeight</property> <standard>more</standard>
	 *            </requirement> <requirement>
	 *            <property>device:hasEnergyProfile/
	 *            energy:consumption/energy:modeAverage</property>
	 *            <standard>least</standard> </requirement> <requirement>
	 *            <property>service:serviceCost</property>
	 *            <standard>less</standard> </requirement> <requirement>
	 *            <property>service:hasInput/service:parameterUnit</property>
	 *            <standard>notNumeric</standard> <value>unit:VideoAvi</value>
	 *            </requirement> </requirements> </request>
	 */
	public void loadRequest(String xmlString) {

		try {
			Document doc = createDomOfRequest(xmlString);

			extractRequestedData(doc);

		} catch (ParserConfigurationException e) {
			LOG.error(e.getMessage(), e.getCause());
		} catch (SAXException e) {
			LOG.error(e.getMessage(), e.getCause());
		} catch (IOException e) {
			LOG.error(e.getMessage(), e.getCause());
		}

	}

	/**
	 * Adds a requirement to a list.
	 * 
	 * @param propertyValue
	 *            Value of a property.
	 * @param mappedStandard
	 *            The mapped standard.
	 * @param valueValue
	 *            The value of a related standard.
	 * @param requirementList
	 *            List of requirements.
	 */
	private void addRequirementToList(String propertyValue,
			Standard mappedStandard, String valueValue,
			List<Requirement> requirementList) {
		Requirement requirement =
				new Requirement(propertyValue, mappedStandard, valueValue);

		requirementList.add(requirement);

	}

	/**
	 * Copies the service qualities to an array
	 * 
	 * @param serviceQualityList
	 *            Service Quality List.
	 */
	private void copyServiceQualittyListToArray(List<String> serviceQualityList) {
		serviceQualities = new String[serviceQualityList.size()];
		serviceQualityList.toArray(serviceQualities);
	}

	/**
	 * Copies a list to an array.
	 * 
	 * @param requirementList
	 *            The requirement list.
	 * @return Returns an array of requirements.
	 */
	private Requirement[] copyListToArray(List<Requirement> requirementList) {

		Requirement[] requirements = new Requirement[requirementList.size()];

		requirementList.toArray(requirements);

		return requirements;
	}

	/**
	 * Extracts the requested data of DOM document.
	 * 
	 * @param doc
	 *            DOM document.
	 */
	private void extractRequestedData(Document doc) {

		Element docElement = doc.getDocumentElement();

		NodeList children = docElement.getChildNodes();

		List<String> serviceQualityList = new ArrayList<String>();

		List<Requirement> requirementList = new ArrayList<Requirement>();

		for (int i = 0; i < children.getLength(); i++) {
			Node childNode = children.item(i);

			if (childNode.getNodeName().equals("serviceQualities")) {

				NodeList internalChildren = childNode.getChildNodes();

				for (int j = 0; j < internalChildren.getLength(); j++) {

					Node internalNode = internalChildren.item(j);

					String textContent = internalNode.getTextContent();

					if (!textContent.equals("\n") && textContent != null
							&& !textContent.isEmpty())
						serviceQualityList.add(textContent);

				}

				copyServiceQualittyListToArray(serviceQualityList);

			} else if (childNode.getNodeName().equals("requirements")) {

				NodeList internalChildren = childNode.getChildNodes();

				for (int j = 0; j < internalChildren.getLength(); j++) {

					// Requirement
					Node internalNode = internalChildren.item(j);

					if (internalNode.getNodeName().equals("requirement")) {
						String propertyValue =
								getChildElement(internalNode, "property")
										.getTextContent();

						Node valueValueNode =
								getChildElement(internalNode, "value");

						String valueValue = "";

						if (valueValueNode != null) {
							valueValue = valueValueNode.getTextContent();
						}

						String standardValue =
								getChildElement(internalNode, "standard")
										.getTextContent();

						Standard mappedStandard = null;

						mappedStandard = mapToStandard(standardValue);

						if (mappedStandard == Standard.notNumeric
								&& valueValue.length() < 1) {
							LOG
									.error("QoS Client request cannot contain a not numeric property standard without specifying a standard value.");
						}

						addRequirementToList(propertyValue, mappedStandard,
								valueValue, requirementList);
					}

					requirements = copyListToArray(requirementList);

				}
			}

		}
	}

	/**
	 * Get the child element of a node.
	 * 
	 * @param root
	 *            Root node.
	 * @param tagName
	 *            Tag name of the child node.
	 * @return Returns the child node.
	 */
	private Node getChildElement(Node root, String tagName) {

		NodeList children = root.getChildNodes();

		for (int i = 0; i < children.getLength(); i++) {
			if (children.item(i).getNodeName().equals(tagName)) {
				return children.item(i);
			}
		}

		return null;
	}

	/**
	 * Maps a standard value to a standard.
	 * 
	 * @param standardValue
	 *            Value of a standard.
	 * @return Returns an instance of <b>Standard</b> mapped to the
	 *         standardValue.
	 * @throws IllegalArgumentException
	 */
	private Standard mapToStandard(String standardValue){

		Standard standard = null;

		try {
			standard = Standard.valueOf(standardValue);
		} catch (IllegalArgumentException e) {
			LOG.error("'" + standardValue
					+ "' is an irregular value for a standard.");
			LOG.error(e.getMessage(), e.getCause());
		}

		return standard;
	}

}
