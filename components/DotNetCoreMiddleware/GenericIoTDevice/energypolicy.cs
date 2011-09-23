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
using IoT;
using System.Xml;


/// <summary>
/// Summary description for EnergyPolicy
/// </summary>
public class EnergyPolicy
{
    IoTDevice m_IoTdevice=null;
    XmlDocument m_policydocument = null;
    bool m_validationerror = false;
    string m_validationmessage="";

    public EnergyPolicy()
    {

    }

    public void SetDevice(IoTDevice theDevice)
    {
        m_IoTdevice = theDevice;
    }

    public IoTDevice GetDevice()
    {
        return m_IoTdevice;
    }

    public string GetEnergyPolicy()
    {
        if (m_policydocument != null)
            return m_policydocument.OuterXml;
        else
            return "<noenergypolicy/>";
    }

    public bool LoadPolicy(string xmlstring)
    {
        try
        {
            m_policydocument = new XmlDocument();

            m_policydocument.LoadXml(xmlstring);
        }
        catch (Exception e)
        {
            m_IoTdevice.ReportError(e.Message);
            return false;
        }

        return true;
    }

    public bool LoadPolicyFromURL(string xmlstring)
    {
        try
        {
            m_policydocument = new XmlDocument();

            m_policydocument.Load(xmlstring);
        }
        catch (Exception e)
        {
            m_IoTdevice.ReportError("Energy Policy Exception:"+e.Message);
            return false;
        }

        return true;
    }

    public string GetEnergyPolicyValue(string xpath)
    {
        try
        {
            if (m_policydocument == null)
                return "";
            else
            {
                XmlNode policyNode = m_policydocument.SelectSingleNode(xpath);

                if (policyNode != null)
                    return policyNode.InnerXml;
                else
                    return "";
            }
        }
        catch (Exception e)
        {
            m_IoTdevice.ReportError("EnergyPolicySearch: " + e.Message);

            return "";
        }

        return "";
    }

    public bool ValidateAction(string service, string action)
    {
        return IsEnabled(service, action, DateTime.Now); ;
    }

    public bool CheckNumberOnsAllowed(int number)
    {
        string maxstring = GetEnergyPolicyValue(".//*[name()='maxswitchonperday']");

        if (maxstring != "")
        {
            int max = System.Convert.ToInt32(maxstring);
            if (number > max)
            {
                m_validationerror = true;
                m_validationmessage = "Energy Policy Violation: Max number of switch ons are exceed, max allowed per day:" + maxstring;
            }

            return number <= max;
        }
        else
            return true;
    }

    public string GetValidationMessage()
    {
        return m_validationmessage;
    }

    public void ResetValidationError()
    {
        m_validationerror = false;
        m_validationmessage = "";
    }

    public bool IsEnabled(string service, string action, DateTime time)
    {
        if (m_policydocument==null)
            return true;

        XmlNodeList theDisabledNodes = m_policydocument.SelectNodes(".//*[name()='disable']/*[name()='service' and @type='time' and @name='"+service+"']");

        int upperbound = theDisabledNodes.Count;

        if (upperbound == 0)
            return true;

        for (int pos = 0; pos < upperbound; pos++)
        {
            XmlNode theNode = theDisabledNodes[pos];

            if (IsDisabledServiceNode(theNode, time))
            {
                XmlAttribute theAction = theNode.Attributes["action"];

                if (theAction == null || theAction.InnerText == "" || theAction.InnerText == action)
                {
                    m_validationerror=true;
                    m_validationmessage = "Energy Policy Violation:"+GetMessage(theNode);
                    return false;
                }
            }
        }

        return true;
    }

    bool IsDisabledServiceNode(XmlNode theNode,DateTime theTime)
    {
        XmlNode startNode, endNode;
        string startstring, endstring;
        
        try {
        startNode = theNode.SelectSingleNode("*[name()='starttime']");
        endNode = theNode.SelectSingleNode("*[name()='endtime']");

        startstring = startNode.InnerText;

        
        endstring = endNode.InnerText;

        DateTime startTime = DateTime.Parse(startstring);

        DateTime endTime = DateTime.Parse(endstring);

        if (theTime>=startTime && theTime<=endTime)
            return true;
        }
        catch (Exception e)
        {
            m_IoTdevice.ReportError("Energy Policy Exception:" + e.Message);
        }

        return false;
    }

    string GetMessage(XmlNode theNode)
    {
        XmlNode theMessage = theNode.SelectSingleNode("*[name()='message']");

        if (theMessage != null)
            return theMessage.InnerText;
        else
            return "";
    }
}
