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
// UPnP .NET Framework Device Stack, Core Module
// Device Builder Build#1.0.3896.16026

using System;
using OpenSource.UPnP;
using IoTDeviceExample;

namespace IoTDeviceExample
{
	/// <summary>
	/// Summary description for Main.
	/// </summary>
	class SampleIoTDeviceMain
	{
		/// <summary>
		/// The main entry point for the application.
		/// </summary>
		[STAThread]
		static void Main(string[] args)
		{
			// Starting UPnP Device
			System.Console.WriteLine("UPnP .NET Framework Stack");
			System.Console.WriteLine("Device Builder Build#1.0.3896.16026");
			MyIoTDevice device = new MyIoTDevice("1","MyComputer", "CNet OpenSource", "urn:schemas-upnp-org:IoTdevice:1");
			device.AddServices();
			device.Start();
			System.Console.WriteLine("Press return to stop device.");
			System.Console.ReadLine();
			device.Stop();
		}
		
	}
}

