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
package eu.linksmart.qos.client.response;

import java.io.StringWriter;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This class serves to create the QoS response in XML structure.
 * 
 * @author Amro Al-Akkad
 * 
 */
public class CreateXMLResponse {

	/**
	 * Constant for indicating a service running on a device fulfills the
	 * quality criteria.
	 */
	private static final String QUALIFIED = "qualified";

	/**
	 * Constant for indicating a service running on a device fulfills the
	 * quality criteria.
	 */
	private static final String DISQUALIFIED = "disqualified";

	/**
	 * Namespace of QoS manager.
	 */
	private static final String NAMESPACE_URI =
			"http://qos.linksmart.eu";

	/**
	 * Executes a detail.
	 * 
	 * @param document
	 *            Document.
	 * @param Details
	 *            Details.
	 * @return Returns a XML element representing a detail.
	 */
	private Element executeDetail(Document document, Vector<Detail> Details) {

		String details = "Details";
		Element detailsElement = document.createElement(details);

		for (int i = 0; i < Details.size(); i++) {
			String property, value, unit;
			property = Details.elementAt(i).getParameter();
			value = Details.elementAt(i).getValue();
			unit = Details.elementAt(i).getUnit();

			Element detailElement =
					createDetailElement(document, property, value, unit);
			detailsElement.appendChild(detailElement);
		}
		return detailsElement;
	}

	/**
	 * Executes a rank.
	 * 
	 * @param lists
	 *            Lists of rank element.
	 * @param rankRecordDetails
	 *            Rank record details.
	 * @param range Range to execute rank, i.e. between 1 and lists.size.
	 * @return Returns the final string as XML.
	 * @throws ParserConfigurationException
	 * @throws TransformerException
	 */
	public String executeRank(Vector<RankElement> lists,
			Vector<Vector<Detail>> rankRecordDetails, int range)
			throws ParserConfigurationException, TransformerException {
		String root = "ResultList";
		DocumentBuilderFactory documentBuilderFactory =
				DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder =
				documentBuilderFactory.newDocumentBuilder();

		Document document = documentBuilder.newDocument();
		Element rootElement = document.createElementNS(NAMESPACE_URI, root);
		document.appendChild(rootElement);

		for (int i = 0; i < range; i++) {

			Element detailsElement =
					this.executeDetail(document,
							(Vector<Detail>) rankRecordDetails.elementAt(i));
			String position;
			if ((5 - i) > 0) {
				position = String.valueOf(5 - i);
			} else {
				position = "0";
			}

			Element rankElement =
					createRankElement(document, String.valueOf(i + 1), lists
							.elementAt(i).isDisqualified(), lists.elementAt(i)
							.getServicename(), lists.elementAt(i)
							.getDevicePID(), lists.elementAt(i).getDeviceURI(),
							position, lists.elementAt(i).getAverage() + "%",
							detailsElement);
			rootElement.appendChild(rankElement);

		}

		String finalXMLString = null;

		// write to string
		Transformer transformer =
				TransformerFactory.newInstance().newTransformer();

		transformer.setOutputProperty(OutputKeys.INDENT, "YES");
		DOMSource source = new DOMSource(document);
		StringWriter stringWriter = new StringWriter();

		StreamResult result = new StreamResult(stringWriter);
		transformer.transform(source, result);
		finalXMLString = stringWriter.getBuffer().toString();

		return finalXMLString;

	}

	/**
	 * Creates a rank element.
	 * 
	 * @param document
	 *            Document.
	 * @param position
	 *            Position of a rank element.
	 * @param disqualified
	 *            Flag indicating if the service on a device fulfills the
	 *            criteria.
	 * @param serviceOperation
	 *            Service Operation.
	 * @param devicePID
	 *            PID of a device.
	 * @param deviceURI
	 *            URI of a device.
	 * @param rate
	 *            Rate of a rank element.
	 * @param averagePercentage
	 *            Average percentage of a device.
	 * @param detailsElement
	 *            Details element.
	 * @return Returns a new rank element.
	 */
	private Element createRankElement(Document document, String position,
			boolean disqualified, String serviceOperation, String devicePID,
			String deviceURI, String rate, String averagePercentage,
			Element detailsElement) {

		final String rankString = "Rank";
		Element rankElement = document.createElement(rankString);

		final String positionString = "Position";
		Element positionElement = document.createElement(positionString);
		positionElement.appendChild(document.createTextNode(position));

		final String validnessString = "Validness";
		org.w3c.dom.Attr validnessAttr =
				document.createAttribute(validnessString);

		if (disqualified)
			validnessAttr.setTextContent(DISQUALIFIED);
		else
			validnessAttr.setTextContent(QUALIFIED);

		positionElement.setAttributeNode(validnessAttr);

		final String serviceOperationString = "ServiceOperation";
		Element serviceOperationElement =
				document.createElement(serviceOperationString);
		serviceOperationElement.appendChild(document
				.createTextNode(serviceOperation));

		final String devicePIDString = "DevicePID";
		Element devicePIDElement = document.createElement(devicePIDString);
		devicePIDElement.appendChild(document.createTextNode(devicePID));

		final String deviceURIString = "DeviceURI";
		Element deviceURIElement = document.createElement(deviceURIString);
		deviceURIElement.appendChild(document.createTextNode(deviceURI));

		final String rateString = "Rate";
		Element rateElement = document.createElement(rateString);
		rateElement.appendChild(document.createTextNode(rate));

		final String avgString = "AveragePercentage";
		Element avgElement = document.createElement(avgString);
		avgElement.appendChild(document.createTextNode(averagePercentage));

		rankElement.appendChild(positionElement);
		rankElement.appendChild(devicePIDElement);
		rankElement.appendChild(deviceURIElement);
		rankElement.appendChild(serviceOperationElement);
		rankElement.appendChild(rateElement);
		rankElement.appendChild(avgElement);
		rankElement.appendChild(detailsElement);

		return rankElement;
	}

	/**
	 * Creates a detail element.
	 * 
	 * @param document
	 *            Document.
	 * @param property
	 *            Property of a detail.
	 * @param value
	 *            Value of a Detail.
	 * @param unit
	 *            Unit of a detail.
	 * @return Returns a new <b>Detail</b> element.
	 */
	private Element createDetailElement(Document document, String property,
			String value, String unit) {

		final String detailsString = "Detail";
		Element detailElement = document.createElement(detailsString);

		final String propertyString = "Property";
		Element propertyElement = document.createElement(propertyString);
		propertyElement.appendChild(document.createTextNode(property));

		final String valueString = "Value";
		Element valueElement = document.createElement(valueString);
		valueElement.appendChild(document.createTextNode(value));

		final String unitString = "Unit";
		Element unitElement = document.createElement(unitString);
		unitElement.appendChild(document.createTextNode(unit));

		detailElement.appendChild(propertyElement);
		detailElement.appendChild(valueElement);
		detailElement.appendChild(unitElement);

		return detailElement;
	}
}