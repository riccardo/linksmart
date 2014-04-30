using System;
using System.Net;
using System.Collections;
using System.Collections.Generic;
using System.Text;

using System.Threading;

using Newtonsoft.Json;
using System.IO;
using System.Linq;
using System.Xml;
using System.Xml.XPath;

namespace EventManager
{
    class JSONServer
    {
        private string m_endPoint = "";
        public WebServer m_webServer = null;
        public bool start()
        {
            m_endPoint = "http://*:8812/"; ;
            m_webServer = new WebServer(m_endPoint);

            m_webServer.IncomingRequest += WebServer_IncomingRequest;
            m_webServer.Start();
            return true;

        }
        public bool stop()
        {

            m_webServer.Stop();
            return true;

        }

        /// <summary>
        /// Callback Event from WebServer when a HTTP request is made.
        /// </summary>
        /// <param name="sender">Sender</param>
        /// <param name="e">Arguments</param>
        public void WebServer_IncomingRequest(object sender, HttpRequestEventArgs e)
        {
            HttpListenerResponse response = e.RequestContext.Response;
            HttpListenerRequest request = e.RequestContext.Request;

            StreamReader sr = new StreamReader(request.InputStream);
            try
            {
                Newtonsoft.Json.JsonTextReader jr = new Newtonsoft.Json.JsonTextReader(sr);

                Newtonsoft.Json.Linq.JObject j = Newtonsoft.Json.Linq.JObject.Load(jr);
                string test = j.ToString();
                System.Console.WriteLine(request.RawUrl);
                System.Console.WriteLine(test);
                XmlDocument xDoc = (XmlDocument)JsonConvert.DeserializeXmlNode(j.ToString(), "Call");

                System.Console.WriteLine(xDoc.OuterXml);

                string responseString = "";


                byte[] buffer = System.Text.Encoding.UTF8.GetBytes(responseString);
                response.StatusCode = (int)HttpStatusCode.OK;
                response.StatusDescription = "OK";
                response.ContentLength64 = buffer.Length;
                response.ContentEncoding = Encoding.UTF8;
                response.OutputStream.Write(buffer, 0, buffer.Length);
                response.OutputStream.Close();
                response.Close();

                if (request.Url.AbsoluteUri.Contains("unsubscribe"))
                {
                    XmlNode xFilterNode = xDoc.SelectSingleNode("//topic/filter");
                    XmlNode xEndpoint = xDoc.SelectSingleNode("//topic/endpoint");
                    string endpoint = xEndpoint.InnerText;
                    string topic = xFilterNode.InnerText;
                    try
                    {
                        EventManagerImplementation.subscriptionList.RemoveAll(f => (f.Endpoint.Equals(endpoint) && f.Topic.Equals(topic)));
                        Console.WriteLine("Unsubscribe:\nTopic: {0}\nEndpoint: {1}", topic, endpoint);
                        return;
                    }
                    catch (Exception ex)
                    {
                        System.Console.WriteLine("Exception:" + ex.Message);
                        return;
                    }

                }
                else if (request.Url.AbsoluteUri.Contains("subscribe"))
                {
                    XmlNode xFilterNode = xDoc.SelectSingleNode("//topic/filter");
                    XmlNode xEndpoint = xDoc.SelectSingleNode("//topic/endpoint");
                    string endpoint = xEndpoint.InnerText;
                    string topic = xFilterNode.InnerText;
                    try
                    {
                        if (EventManagerImplementation.subscriptionList.Exists(f => (f.Endpoint != null && f.Endpoint.Equals(endpoint) == true && f.Topic.Equals(topic) == true)))
                        { Console.WriteLine("Subscription already exists"); }
                        else
                        {
                            Components.Subscription subscription = new Components.Subscription(topic, null, endpoint,null, 0, null, 0, null, "REST");
                            Subscribe subscribeClass = new Subscribe(subscription);
                            Thread subscribeThread = new Thread(new ThreadStart(subscribeClass.subscribe));
                            subscribeThread.Start();
                            //subscribeThread.Join();
                        }
                        return;
                    }
                    catch (Exception ex)
                    {
                        System.Console.WriteLine("Exception:" + ex.Message);
                        return;
                    }
                }


            }
            catch (Exception ex)
            {
                System.Console.WriteLine("Exception When Processing HTTP JSON Call");
                System.Console.WriteLine(ex.Message);
            }


        }
    }
}
