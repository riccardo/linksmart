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
/// Summary description for EnergyProfile
/// </summary>
public class EnergyProfile
{
    IoTDevice m_IoTdevice = null;
    XmlDocument m_profiledocument = null;
    bool m_validationerror = false;
    string m_validationmessage;

    public EnergyProfile()
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

    public bool LoadProfile(string xmlstring)
    {
        try
        {
           m_profiledocument = new XmlDocument();

            m_profiledocument.LoadXml(xmlstring);
        }
        catch (Exception e)
        {
            m_IoTdevice.ReportError(e.Message);
            return false;
        }

        return true;
    }

    public bool LoadProfileFromURL(string xmlstring)
    {
        try
        {
            m_profiledocument = new XmlDocument();

            m_profiledocument.Load(xmlstring);
        }
        catch (Exception e)
        {
            m_IoTdevice.ReportError("Energy Profile Exception:" + e.Message);
            return false;
        }

        return true;
    }

    public string GetMaxEffect(string mode)
    {
        return GetEnergyProfileValue(".//*[name()='energyconsumption']//*[name()='effect']/*[name()='mode' and @name='" + mode + "']/*[name()='max']");
        
    }

    public string GetMinEffect(string mode)
    {
        return GetEnergyProfileValue(".//*[name()='energyconsumption']//*[name()='effect']/*[name()='mode' and @name='" + mode + "']/*[name()='min']");

    }

    public string GetAverageEffect(string mode)
    {
        return GetEnergyProfileValue(".//*[name()='energyconsumption']//*[name()='effect']/*[name()='mode' and @name='" + mode + "']/*[name()='average']");

    }

    public string GetExpectedLifeTime()
    {

        return GetEnergyProfileValue(".//*[name()='lifetime']//*[name()='expected']");
;
    }

    public string GetMinimumRunTime()
    {

        return GetEnergyProfileValue(".//*[name()='operation']//*[name()='minimumruntime']");
    }

    public string GetEnergyProfile()
    {
        if (m_profiledocument != null)
            return m_profiledocument.OuterXml;
        else
            return "<noenergyprofile/>";
    }

    public string GetEnergyProfileValue(string xpath)
    {
        try {
        if (m_profiledocument == null)
            return "";
        else
            {
                XmlNode profileNode=m_profiledocument.SelectSingleNode(xpath);

                if (profileNode != null)
                    return profileNode.InnerXml;
                else
                    return "";
            }
        }
        catch (Exception e)
        {
            m_IoTdevice.ReportError("EnergyProfileSearch: "+e.Message);

            return "";
        }

        return "";
    }

}