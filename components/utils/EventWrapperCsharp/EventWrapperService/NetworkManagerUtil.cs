using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using eu.linksmart.eventing.NetworkManagerStub;

namespace eu.linksmart.eventing.util
{
    public class NetworkManagerUtil
    {
        /// <summary>
        /// get soap tunneling address from a pid. 
        /// </summary>
        /// <param name="networkManagerUrl">url of network manager to ask</param>
        /// <param name="pid">pid</param>
        /// <param name="myHid"></param>
        /// <returns></returns>
        public static string GetLinksmartUrlFromPid(string networkManagerUrl, string pid, string myHid)
        {

            Registration[] vaResults = null;
            NetworkManager netManager = new NetworkManager();
            netManager.Url = networkManagerUrl;
            NetworkManagerStub.Part p = new NetworkManagerStub.Part();
            p.key = "PID";
            p.value = pid;

            vaResults = netManager.getServiceByAttributes(new NetworkManagerStub.Part[] { p });
            String targetUrlHydraEventManager=null;
            if (vaResults.Length > 0)
            {
                Uri nmUrl = new Uri(Properties.Settings.Default.SoapTunnelUrl);
                targetUrlHydraEventManager = "http://" + nmUrl.Host +
                    ":" + nmUrl.Port + "/SOAPTunneling/0/" + vaResults[0].virtualAddressAsString + "/0/hola";
            }
            return targetUrlHydraEventManager;

        }

        /// <summary>
        /// get soap tunneling address from service description
        /// </summary>
        /// <param name="networkManagerUrl">url of the network manager to ask</param>
        /// <param name="serviceDesc">service description</param>
        /// <returns></returns>
        public static string GetLinksmartUrlFromDesc(string networkManagerUrl, string serviceDesc)
        {

            Registration[] vaResults = null;
            NetworkManager netManager = new NetworkManager();
            String targetUrlHydraEventManager = null;
            netManager.Url = networkManagerUrl;
            NetworkManagerStub.Part p = new NetworkManagerStub.Part();
            p.key = "DESCRIPTION";
            p.value = serviceDesc;

            vaResults = netManager.getServiceByAttributes(new NetworkManagerStub.Part[] { p });

            if (vaResults.Length > 0)
            {
                Uri nmUrl = new Uri(Properties.Settings.Default.SoapTunnelUrl);
                targetUrlHydraEventManager = "http://" + nmUrl.Host +
                    ":" + nmUrl.Port + "/SOAPTunneling/0/" + vaResults[0].virtualAddressAsString + "/0/hola";
            }

            return targetUrlHydraEventManager;

        }
    }
}
