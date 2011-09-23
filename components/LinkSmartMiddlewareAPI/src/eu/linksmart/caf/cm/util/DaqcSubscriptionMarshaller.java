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
 * Copyright (C) 2006-2010 University of Reading,
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
package eu.linksmart.caf.cm.util;

import java.io.PrintStream;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import eu.linksmart.caf.Attribute;
import eu.linksmart.caf.Parameter;
import eu.linksmart.caf.daqc.DataAcquisitionComponent;
import eu.linksmart.caf.daqc.subscription.Subscription;

/**
 * Marshaller for encoding and decoding {@link Subscription}s to the {@link DataAcquisitionComponent}
 * @author Michael Crouch
 *
 */
public class DaqcSubscriptionMarshaller {

	/**
	 * Encodes the {@link Subscription} to the {@link PrintStream}
	 * @param sub the {@link Subscription}
	 * @param out the {@link PrintStream}
	 * @throws XmlMarshallingException
	 */
	public static void encodeSubscription(Subscription sub, PrintStream out) throws XmlMarshallingException {
		out.println("<Subscription protocol=\"" + sub.getProtocol() + "\" dataId=\"" + sub.getDataId() + "\">");
		out.println("<Attributes>");
		CoreObjectMarshaller.encodeAttributeList(sub.getAttributes(), out);
		out.println("</Attributes>");
		out.println("<Parameters>");
		CoreObjectMarshaller.encodeParameterList(sub.getParameters(), out);
		out.println("</Parameters>");
		out.println("</Subscription>");
	}
	
	/**
	 * Decodes the {@link Subscription} from the {@link Node}
	 * @param root the {@link Node}
	 * @return the {@link Subscription}
	 * @throws XmlMarshallingException
	 */
	public static Subscription decodeSubscription(Node root) throws XmlMarshallingException {
		if (!root.getNodeName().equals("Subscription"))
			throw new XmlMarshallingException("Node Name mismatch: Should be 'Subscription', was '" + root.getNodeName() + "'" );
		Subscription sub = new Subscription();
		sub.setAttributes(new Attribute[0]);
		sub.setParameters(new Parameter[0]);
		sub.setDataId(CmHelper.getAttributeValue("dataId", root));
		sub.setProtocol(CmHelper.getAttributeValue("protocol", root));
		
		NodeList list = root.getChildNodes();
		for (int i=0; i< list.getLength(); i++)
		{
			Node node = list.item(i);
			if (node.getNodeName().equals("Attributes"))
			{
				sub.setAttributes(CoreObjectMarshaller.decodeAttributeList(node));
			}
			else if (node.getNodeName().equals("Parameters"))
			{
				sub.setParameters(CoreObjectMarshaller.decodeParameterList(node));
			}
		}
		return sub;
	}	
}
