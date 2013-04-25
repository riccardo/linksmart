using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using eu.linksmart.eventing.NetworkManagerStub;
using log4net;

namespace eu.linksmart.eventing.util
{
    public class NetworkManagerUtil
    {

        /// <summary>
        /// get soap tunneling address from a pid. 
        /// </summary>
        /// <param name="networkManager">url of network manager to ask</param>
        /// <param name="pid">persistence id of a service</param>
        /// <param name="myHid">hid of the caller. at the moment we do not check this. leave this to 0</param>
        /// <returns>Soap tunneling url of a service</returns>
        public static string GetLinksmartUrlFromPid(string networkManager, string pid, string myHid)
        {
            ILog Log = LogManager.GetLogger(typeof(NetworkManagerUtil));
             String targetUrlHydraEventManager = null;
            String[] results = null;
            long maxTime = 10000;
            int maxResponses = 1;
            String query = "((PID==" + pid + "))";
            try{
                NetworkManagerApplicationService netManager = new NetworkManagerApplicationService();
                netManager.Url = networkManager;
                results = netManager.getHIDByAttributesAsString(myHid, null,
                        query, maxTime, maxResponses).Split(' ');
                if (results[0].Trim().Equals("")) return null;
                Uri nmUrl = new Uri(Properties.Settings.Default.NetworkManagerStubUrl);
                targetUrlHydraEventManager = "http://" + nmUrl.Host +
                    ":" + nmUrl.Port + "/SOAPTunneling/0/" + results[0].Trim() + "/0/hola";

             }catch (Exception ex) {
                Log.Info("Cannot find the Network Manager just yet.");
                Log.Debug(ex.Message);
            }
            return targetUrlHydraEventManager;

        }

        /// <summary>
        /// get soap tunneling address from service description
        /// </summary>
        /// <param name="networkManagerUrl">url of the network manager to ask</param>
        /// <param name="serviceDesc">service description</param>
        /// <returns>Soap tunneling url of a service</returns>
        public static string GetLinksmartUrlFromDesc(string networkManagerUrl, string serviceDesc)
        {
            ILog Log = LogManager.GetLogger(typeof(NetworkManagerUtil));
            String targetUrlHydraEventManager = null;
            try
            {
                String[] results = null;
                NetworkManagerApplicationService netManager = new NetworkManagerApplicationService();
                netManager.Url = networkManagerUrl;
                results = netManager.getHIDsbyDescriptionAsString(serviceDesc).Split(' ');
                if (results[0].Trim().Equals("")) return null;
                Uri nmUrl = new Uri(netManager.Url);
                targetUrlHydraEventManager = "http://" + nmUrl.Host +
                    ":" + nmUrl.Port + "/SOAPTunneling/0/" + results[0].Trim() + "/0/hola";
            }
            catch (Exception ex) {
                Log.Debug("Cannot find the Network Manager just yet.");
                Log.Debug(ex.Message);
            }
            return targetUrlHydraEventManager;

        }
    }
}
