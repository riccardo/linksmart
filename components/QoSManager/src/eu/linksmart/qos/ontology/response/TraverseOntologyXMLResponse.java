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
package eu.linksmart.qos.ontology.response;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * This class is intended to traverse the XML query response.
 * 
 * @author Amro Al-Akkad 
 */
public class TraverseOntologyXMLResponse {

	/** Logger. */
	private final static Logger LOG =
			Logger.getLogger(TraverseOntologyXMLResponse.class.getName());
	
	/**
	 * Assigned to a unit for a property value has not been specified.
	 */
	private final static String NOT_SPECIFIED = "Not_Specified";

	/** Field to keep the quary result retrieved from ontology. **/
	private final List<Device> queryResult;	

	/**
	 * Constructor initializing query result as Array List containing instances of class Device.
	 */
	public TraverseOntologyXMLResponse() {
		this.queryResult = new ArrayList<Device>();
	}

	/**
	 * Getter for query result.
	 * @return the query result
	 */
	public List<Device> getQueryResult() {
		return queryResult;
	}

	/**
	 * Performs the whole process of creating a DOM and extracting the relevant information of the query result.
	 * @param xmlString The XML based string retrieved from ontology.
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
		} catch (ParseException e) {
			LOG.error(e.getMessage(), e.getCause());
		}

	}

	/**
	 * 
	 * @param xmlString The XML based to create DOM of. 
	 * @return a XML document
	 * @throws ParserConfigurationException If failure occur during parsing.
	 * @throws SAXException If parsing failure occurs.
	 * @throws IOException If input or output failure occur.
	 */
	private Document createDomOfRequest(String xmlString)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();

		byte[] bytes = xmlString.getBytes();

		InputStream inputStream = new ByteArrayInputStream(bytes);
		Document document = db.parse(inputStream);

		return document;

	}

	/**
	 * Extracts the relevant data from the result of the query.
	 * @param doc Document to extract data of it.
	 * @throws ParseException Thrown if exception is thrown.
	 */
	private void extractRequestedData(Document doc) throws ParseException {

		Element docElement = doc.getDocumentElement();

		if (docElement.getNodeName().equals("response")) {
			NodeList children = docElement.getChildNodes();

			if (children.getLength() > 0) {
				for (int i = 0; i < children.getLength(); i++) {

					if (children.item(i).getNodeName().equals("device")) {

						// device element
						Node deviceElementNode = children.item(i);

						String devicePID =
								getAttribute("pid", deviceElementNode
										.getAttributes());

						String deviceURI =
								getAttribute("uri", deviceElementNode
										.getAttributes());

						Service[] services = extractServices(deviceElementNode);

						Node devicePropertiesElement =
								getChildElement(deviceElementNode,
										"deviceProperties");
						OntologyResponseProperty[] deviceProperties =
								extractProperties(devicePropertiesElement);

						addDeviceToQueryResult(devicePID, deviceURI, services,
								deviceProperties);
					}

				}

				LOG.debug("end of traverse");
			} else
				throw new ParseException(
						"Ontology returned an empty response: <response/>. Please check if your ontology respository is available, and that your query and requirements are formulated properly.",
						0);

		} else
			throw new ParseException("Wrong naming: "
					+ docElement.getNodeName(), 0);

	}

	/**
	 * Adds a device to the query result.
	 * @param devicePID PID of a device.
	 * @param deviceURI URI of a device.
	 * @param services Services that the device run.
	 * @param deviceProperties Properties that describe the device.
	 */
	private void addDeviceToQueryResult(String devicePID, String deviceURI,
			Service[] services, OntologyResponseProperty[] deviceProperties) {
		Device device =
				new Device(devicePID, deviceURI, services, deviceProperties);

		this.queryResult.add(device);

	}

	/**
	 * Retrieves a specific attribute value.
	 * @param attributeName Name of the attribute.
	 * @param namedNodeMap NodeMap that contains the attribute.
	 * @return Returns the attribute value if the node map does contain the specified attribute, and otherwise NULL.
	 */
	private String getAttribute(String attributeName, NamedNodeMap namedNodeMap) {

		for (int i = 0; i < namedNodeMap.getLength(); i++) {

			Node node = namedNodeMap.item(i);

			if (node.getNodeName().equals(attributeName))
				return node.getTextContent();
		}

		return null;
	}

	/**
	 * Extracts services from a device node of the query result.
	 * @param deviceNode Device node to extract data from.
	 * @return Returns the services a device runs as a array of <b>Service</b> class.
	 */
	private Service[] extractServices(Node deviceNode) {

		Service[] services = null;

		NodeList children = deviceNode.getChildNodes();

		List<Service> serviceList = new ArrayList<Service>();

		for (int i = 0; i < children.getLength(); i++) {
			Node node = children.item(i);

			if (node.getNodeName().equals("service")) {

				String operation =
						getAttribute("operation", node.getAttributes());

				Node servicePropertiesElement =
						getChildElement(node, "serviceProperties");

				OntologyResponseProperty[] servicePropertiesProps =
						extractProperties(servicePropertiesElement);

				addServiceToList(serviceList, operation, servicePropertiesProps);
			}
		}

		services = serviceList.toArray(new Service[serviceList.size()]);

		return services;
	}

	/**
	 * Adds service to the service list.
	 * @param serviceList Service list to collect services.
	 * @param operation Name of the service operation.
	 * @param servicePropertiesProps ServiceProperties of a specific service.
	 */
	private void addServiceToList(List<Service> serviceList, String operation,
			OntologyResponseProperty[] servicePropertiesProps) {
		Service service = new Service(operation, servicePropertiesProps);

		serviceList.add(service);
	}

	/**
	 * Extracts properties from a device or service node.
	 * @param propertiesElement Properties element to extract properties from.
	 * @return Returns an array containing instances of class <b>OntologyResponseProperty</b>.
	 */
	private OntologyResponseProperty[] extractProperties(Node propertiesElement) {

		List<OntologyResponseProperty> propertyList =
				new ArrayList<OntologyResponseProperty>();

		NodeList children = propertiesElement.getChildNodes();

		for (int i = 0; i < children.getLength(); i++) {

			if (children.item(i).getNodeName().equals("property")) {

				Node propertyElementNode = children.item(i);

				String name =
						getAttribute("name", propertyElementNode
								.getAttributes());

				Node nodeValue = getChildElement(propertyElementNode, "value");

				String unit = null;

				String value = nodeValue.getTextContent();

				unit = getAttribute("unit", nodeValue.getAttributes());

				if (unit == null)
					unit = NOT_SPECIFIED;

				addPropertyToList(propertyList, name, unit, value);

			}
		}

		OntologyResponseProperty[] properties =
				(OntologyResponseProperty[]) propertyList
						.toArray(new OntologyResponseProperty[propertyList
								.size()]);

		return properties;
	}

	/**
	 * Adds a new property to the list collecting properties.
	 * @param propertyList List to collect properties.
	 * @param name Name of a property
	 * @param unit Unit of a property.
	 * @param value Value of a property
	 */
	private void addPropertyToList(List<OntologyResponseProperty> propertyList, String name, String unit, String value) {
		OntologyResponseProperty property =
				new OntologyResponseProperty(name, unit, value);

		if (isNoValidFigure(value))
			property.setPropertyType(PropertyType.NOT_NUMERIC);

		propertyList.add(property);
	}

	/**
	 * Checks if characters represent a valid figure.
	 * @param characters Characters to check
	 * @return Returns TRUE if characters represent a valid figure, and vice versa returns FALSE.
	 */
	private boolean isNoValidFigure(String characters) {

		char[] array = characters.toCharArray();

		for (char character : array) {

			int val = character;

			if (!(val >= 48 && val <= 57) && !(val == 44) && !(val == 46))
				return true;

		}

		return false;
	}

	/**
	 * Retrieves a specific child node.
	 * @param root Root of child node
	 * @param tagName Tag name of dedicated child node.
	 * @return Returns the child node if it was found, and not NULL if not.
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

}
