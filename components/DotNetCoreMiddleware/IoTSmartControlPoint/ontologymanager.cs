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
// ----------------------------------------------------------------------------
//  Copyright (C) 2010  CNet Svenska AB part of Hydra Project.
// 
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU Lesser General Public License as published by
// the Free Software Foundation, either version 3.0 of the License, or
// (at your option) any later version.
// 
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Lesser General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with this program. If not, see <http://www.gnu.org/licenses/>.
// 
// http://www.cnet.se mailto:IoT@cnet.se
//  ----------------------------------------------------------------------------

using System;
using OpenSource.UPnP;
using OpenSource.Utilities;
using System.Collections.Generic;
using System.Collections;
using System.ServiceModel;
using System.Runtime.Serialization;
using System.ServiceModel.Description;
using System.Xml;
using System.Xml.Xsl;
using System.Xml.XPath;




namespace IoT
{

    public class IoTApplicationOntologyManager
    {
        string m_wsendpoint;
        string m_discoveryrules="";
        string m_userules="";
        string m_ontologyurl = "";

        bool m_remoteontologyavailable = true;

        public IoTApplicationOntologyManager()
        {

        }

        public void SetDiscoveryRules(string userules, string filename)
        {
            m_userules=userules;
            m_discoveryrules=filename;
        }

        public void SetOntologyUrl(string url)
        {
            m_ontologyurl = url;
        }

        public XmlDocument ResolveDeviceUsingXSL(XmlNode discoveryInfo, String rulefile)
		{
			try
			{
				
				XslTransform xsltDoc = new XslTransform();
				xsltDoc.Load(rulefile);

				XmlUrlResolver myResolver=new XmlUrlResolver();
                XsltArgumentList xslArg = new XsltArgumentList();

                XSLOntologyManager obj = new XSLOntologyManager();
                xslArg.AddExtensionObject("urn:IoT_ontologymanager", obj);

				XmlReader tmpXml2=xsltDoc.Transform(discoveryInfo,xslArg,myResolver);

                
				XmlDocument outXml=new XmlDocument();
				outXml.Load(tmpXml2);
				return outXml;
			}
			catch(XmlException xe)
			{
				string blaha=xe.Message;
               			}
			catch(Exception ex)
			{

                string blaha = ex.Message;
    		}

			return null;
		}


       public void UpdateURIwithIoTUDN(string deviceURI,string IoTUDN)
       {
            OntologyManager.ApplicationOntologyManagerService myOntologyMgr = new IoT.OntologyManager.ApplicationOntologyManagerService();
            if (m_ontologyurl != "")
                myOntologyMgr.Url = m_ontologyurl;
            myOntologyMgr.assignPID(deviceURI,IoTUDN);
       }

        public void RemoveInstanceWithDeviceURI(string deviceURI)
       {
            OntologyManager.ApplicationOntologyManagerService myOntologyMgr = new IoT.OntologyManager.ApplicationOntologyManagerService();
            if (m_ontologyurl != "")
                myOntologyMgr.Url = m_ontologyurl;
            myOntologyMgr.removeDevice(deviceURI);
       }
       
        
        
        public XmlDocument ResolveDevice(XmlNode discoveryInfo)
        {
            
            if (m_userules=="yes"&&!discoveryInfo.OuterXml.Contains("Phidget"))
                return ResolveDeviceUsingXSL(discoveryInfo, m_discoveryrules);
            else if (true)
            {
                OntologyManager.ApplicationOntologyManagerService myOntologyMgr = new IoT.OntologyManager.ApplicationOntologyManagerService();

                if (m_ontologyurl != "")
                    myOntologyMgr.Url = m_ontologyurl;

                string devicename="";
                string devicetype="";
                string vendor="";
                
                try
                {
                    XmlNode nameNode=discoveryInfo.SelectSingleNode("//*[name()='name']");
                    XmlNode typeNode=discoveryInfo.SelectSingleNode("//*[name()='majordevicetype']");
                    XmlNode vendorNode=discoveryInfo.SelectSingleNode("//*[name()='vendor']");
                    if (nameNode!=null)
                        devicename = nameNode.InnerText;
                    if (typeNode!=null)
                        devicetype = typeNode.InnerText;
                    if (vendorNode!=null)
                        vendor = vendorNode.InnerText;
                }
                catch (Exception e)
                {
                }

                string resolvemessage = "";

                try
                {
                    //if (m_remoteontologyavailable)
                    //{if (discoveryInfo.OuterXml.Contains("Z600"))
                    //    resolvemessage = myOntologyMgr.resolveDevice(discoveryInfo.OuterXml); 
                    //else if (discoveryInfo.OuterXml.Contains("Phidget"))
                    //    resolvemessage = myOntologyMgr.resolveDevice(discoveryInfo.OuterXml); 
                    //}
                    
                    resolvemessage = myOntologyMgr.resolveDevice(discoveryInfo.OuterXml);
                }

                catch (Exception e)
                {
                    m_remoteontologyavailable = false;
                }
                
                //resolveDevice(devicename, devicetype, vendor);

                if (!m_remoteontologyavailable||resolvemessage == null || resolvemessage == "")
                    return ResolveDeviceUsingXSL(discoveryInfo, m_discoveryrules);

                resolvemessage = resolvemessage.Replace("<?xml version=\"1.0\" encoding=\"utf-8\"?>", "");

                

                resolvemessage = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" + resolvemessage;

                XmlDocument returnDoc = new XmlDocument();

                try
                {
                    returnDoc.LoadXml(resolvemessage);
                }
                catch (Exception e)
                {
                    returnDoc.Load("<resolveerror>" + e.Message + "</resolveerror>");
                }

                return returnDoc;
            }
            else
            {
                string name = discoveryInfo.LocalName;

                if (name == "bluetoothdevice")
                    return ResolveBluetoothDevice(discoveryInfo);
                else if (name == "tellstickdevice")
                    return ResolveTellstickDevice(discoveryInfo);
            }
            return null;
        }

        public XmlDocument ResolveBluetoothDevice(XmlNode discoveryInfo)
        {
            XmlDocument myDoc = null;

            string basicphone = "<basicphonedevice><name>Z600</name><deviceclass>Phone</deviceclass><vendor>Ericsson</vendor></basicphonedevice>";

            string devicetype = discoveryInfo.LocalName;

            if (devicetype == "bluetoothdevice")
            {
                XmlNode myDeviceName = discoveryInfo.SelectSingleNode("//name");

                if (myDeviceName != null)
                {
                    string devicename = myDeviceName.InnerText;

                    if (devicename == "Z600")
                    {
                        myDoc = new XmlDocument();
                        myDoc.LoadXml(basicphone);
                    }
                }
            }
            return myDoc;
        }

        public XmlDocument ResolveTellstickDevice(XmlNode discoveryInfo)
        {
            string name = discoveryInfo.LocalName;
            string returnString = "";
            XmlDocument myDoc = null;


            if (name == "tellstickdevice")
            {
                XmlNode theNameNode, theIDNode, theVendorNode;
                bool isenhanced = false;
                theNameNode = discoveryInfo.SelectSingleNode("name");
                theIDNode = discoveryInfo.SelectSingleNode("deviceid");
                theVendorNode = discoveryInfo.SelectSingleNode("vendor");

                if (theNameNode != null)
                {
                    if (theNameNode.InnerText.Contains("Light"))
                    {
                        returnString = returnString + "<enhancedswitchdevice><name>";
                        isenhanced = true;
                    }
                    else
                        returnString = returnString + "<basicswitchdevice><name>";

                    returnString = returnString + theNameNode.InnerText + "</name><vendor>";
                }
                if (theVendorNode != null)
                    returnString = returnString + theVendorNode.InnerText;

                returnString = returnString + "</vendor><deviceid>";

                if (theIDNode != null)
                    returnString = returnString + theIDNode.InnerText;

                returnString = returnString + "</deviceid>";

                if (isenhanced)
                    returnString = returnString + "</enhancedswitchdevice>";
                else
                    returnString = returnString + "</basicswitchdevice>";

            }

            if (returnString != "")
            {
                myDoc = new XmlDocument();

                myDoc.LoadXml(returnString);
            }
            return myDoc;
        }
    }

    public class XSLOntologyManager
    {
        OntologyManager.ApplicationOntologyManagerService m_ontologymanager;

        public XSLOntologyManager()
        {
            m_ontologymanager = new IoT.OntologyManager.ApplicationOntologyManagerService();
            
        }

        //public XmlNode answer(string query)
        //{
        //    string xmlanswer=m_ontologymanager.answer(query);

        //    return OntologyStringToXml(xmlanswer);
        //}

        //public XmlNode getDeviceDescription(string deviceid)
        //{
        //    string xmlanswer = m_ontologymanager.getDeviceDescription(deviceid);

        //    return OntologyStringToXml(xmlanswer);
        //}

        //public XmlNode getSupplierInfo(string deviceid)
        //{
        //    string xmlanswer = m_ontologymanager.getSupplierInfo(deviceid);

        //    return OntologyStringToXml(xmlanswer);
        //}

        //public XmlNode getAllDeviceServices(string deviceid)
        //{
        //    string xmlanswer = m_ontologymanager.getAllDeviceServices(deviceid);

        //    return OntologyStringToXml(xmlanswer);
        //}

        //public XmlNode getDeviceServices(string deviceid, string categoryName)
        //{
        //    string xmlanswer = m_ontologymanager.getDeviceServices(deviceid, categoryName);

        //    return OntologyStringToXml(xmlanswer);
        //}

        //public XmlNode getDevicesWithService(string categoryName)
        //{
        //    string xmlanswer = m_ontologymanager.getDevicesWithService(categoryName);

        //    return OntologyStringToXml(xmlanswer);
        //}

        /*public XmlNode resolveDevice(string name, string type, string vendor)
        {
            string xmlanswer = m_ontologymanager.resolveDevice(name, type, vendor);

            return OntologyStringToXml(xmlanswer);
        }*/

        

        public XmlNode OntologyStringToXml(string xmlanswer)
        {
            XmlDocument myDoc = new XmlDocument();

            try
            {


                myDoc.LoadXml(xmlanswer);

                return myDoc;

            }
            catch (Exception e)
            {

                myDoc.LoadXml("<ontologyerror>" + e.Message + "</ontologyerror>");

                return myDoc;

            }

            return null;
        }
    }
}