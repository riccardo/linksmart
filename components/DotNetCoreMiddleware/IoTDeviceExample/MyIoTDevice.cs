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
// UPnP .NET Framework Device Stack, Device Module
// Device Builder Build#1.0.3896.16026

using System;
using OpenSource.UPnP;
using IoTDeviceExample;
using IoT;

using System.ServiceModel;
namespace IoTDeviceExample
{
	/// <summary>
	/// Summary description forcomautest.
	/// </summary>
	public class MyIoTDevice:IoTDevice
	{
		
		public MyIoTDevice(string HydraID, string name, string vendor, string deviceURN):base(HydraID, name, vendor, deviceURN)
		{
			FriendlyName = "MyComputer";
			Manufacturer = "CNet OpenSource";
			ManufacturerURL = "http://www.hydramiddleware.eu";
			ModelName = "Sample Auto-Generated Hydra Device";
			ModelDescription = "Hydra Device Using Hydra DDK";
			ModelNumber = "1";
			HasPresentation = false;
            DeviceURN = "urn:schemas-upnp-org:IoTdevice:1";
			Intel.Sample.DvnameService nameService = new Intel.Sample.DvnameService();
			nameService.External_GetComputerName = new Intel.Sample.DvnameService.Delegate_GetComputerName(nameService_GetComputerName);
			AddService(nameService);
			
			// Setting the initial value of evented variables
		}
		
		override public void Start()
		{
			StartDevice();
		}
		
		override public void Stop()
		{
			StopDevice();
		}
		
		override public System.String CreateWS()
		{
            Console.WriteLine("Creating NameService Web Service...");
            IoTWCFServiceLibrary.NameServiceWS myWS = new IoTWCFServiceLibrary.NameServiceWS(this);
            InitiateWebService(myWS, "IoTWCFServiceLibrary.IIoTDeviceExample_nameServiceWSService", "IoTDevice/NameService");
			return m_wsendpoint;
		}
        public void nameService_GetComputerName(out System.String name)
		{
            name = Environment.MachineName + " " + Environment.UserName + " " + Environment.UserDomainName;
            Console.WriteLine("Computer Name :" + name);
		}
		
	}
}


namespace IoTWCFServiceLibrary
{
	[ServiceContract()]
	public interface IIoTDeviceExample_nameServiceWSService
	{
		
		[OperationContract]
		void GetComputerName(out System.String name);
	}
	
	[ServiceBehavior(InstanceContextMode = InstanceContextMode.Single,IncludeExceptionDetailInFaults = true)]
	public class NameServiceWS:IIoTDeviceExample_nameServiceWSService
	{
		IoTDeviceExample.MyIoTDevice m_comautest;
		public NameServiceWS(IoTDeviceExample.MyIoTDevice theDevice)
		{
			m_comautest = theDevice;
		}
		
		public void GetComputerName(out System.String name)
		{
			m_comautest.nameService_GetComputerName(out name);
		}
		
	}
}
