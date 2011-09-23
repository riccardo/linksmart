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
 * Copyright (C) 2006-2010 
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
package eu.linksmart.selfstar.cc.asl.core;

import java.lang.reflect.Method;
import java.util.Hashtable;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public class OSGi_Device{

	private BundleContext bc;
	private Hashtable<String,Bundle> bundles=new Hashtable<String,Bundle>();

	
	public OSGi_Device(){
	}
	
	public void setBundleContext(BundleContext context){
		bc=context;
	}
	
	public BundleContext getBundleContext(){
		return bc;
	}

/*	public void start_device(String args[]) {
		try{
			if (args==null)
				args=new String[]{};
			System.out.println("starting"+EclipseStarter.class);
			bc=EclipseStarter.startup(new String[]{}, null);
			System.out.println("Started...");
		} catch (Exception e){
			e.printStackTrace(Logger.getStream());
		}
	}	
*/
	
/*	public static void main(String args[]) throws Exception{
		Logger.println("Starting server");
		Equinox_OSGI_Device dev = new Equinox_OSGI_Device();
		new OSGI_Device_Connector.ServerRole(dev); // starts the main loop
		
	}
*/
	/* (non-Javadoc)
	 * @see testbed.OSGi_Device#start_device(java.lang.String[])
	 */
	
	/*
	public void start_device(String[] args) throws Exception {
		impl.start_device(args);
		
	}

	/* (non-Javadoc)
	 * @see testbed.OSGi_Device#stop_device()
	 */
/*	public void stop_device() {
		try{
				System.out.println("Closing osgi platform");
				EclipseStarter.shutdown();
				System.exit(0);
		} catch (Exception e){
			e.printStackTrace(Logger.getStream());
		}
	}
*/	
	/* (non-Javadoc)
	 * @see testbed.OSGi_Device#deploy_component(testbed.Testbed_Component)
	 */
	public Bundle deploy_component(String url) {
		try {
			System.out.println("deploying:"+url+"\n"+bc);
			if (bc==null){
				throw new Exception("Must start device first");
			}
			Bundle b = bc.installBundle("file:"+url);
			bundles.put(url,b);
			return b;
		//} catch (DuplicateBundleException d){
			
		} catch (Exception e){
			e.printStackTrace(System.out);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see testbed.OSGi_Device#undeploy_component(testbed.Testbed_Component)
	 */
/*	public void undeploy_component(String url) {
		try {
			if (bc==null){
				throw new Exception("Device not active");
			}
			Bundle b = bundles.get(url);
			b.uninstall();
		} catch (Exception e){
			e.printStackTrace(Logger.getStream());
		}
	}
*/
	
	/* (non-Javadoc)
	 * @see testbed.OSGi_Device#start_service(testbed.Testbed_Service)
	 */
/*	public void start_service(String url) throws Exception{
			Bundle b = bundles.get(url);
			b.start();
	}
*/
	/* (non-Javadoc)
	 * @see testbed.OSGi_Device#stop_service(testbed.Testbed_Service)
	 */
/*	public void stop_service(String url){
		try{
			Bundle b = bundles.get(url);
			Logger.println("bundlestatus: "+b.getState()+"b: "+b);
			b.stop();
			Logger.println("bundlestatus: "+b.getState()+"b: "+b);
		} catch (Exception e){ 
			e.printStackTrace(Logger.getStream());
		}
			
	}
	*/

	/* (non-Javadoc)
	 * @see testbed.OSGi_Device#bind(testbed.Equinox_OSGI_Service, testbed.Equinox_OSGI_Service, java.lang.String)
	 */
	
	/**
	 * Binds the client to the interface implemented by the server. The requirements 
	 * the client and server bundles must comply with are as follows: First, the server must
	 * register a service implementing the given interface. Second, the client must have
	 * registered a service implementing the eu.linksmart.asl.interfaces.Bindable interface. 
	 * Third, the Bindable service registered by the client must implement the 
	 * getBindMethod(string c), returning the name of the actual method used to bind the
	 * client's Bindable component to the server's service of type c.
	 */

	public void bind(Bundle client, Bundle server, String interfacename){
		try {
			Object serverimpl=null,clientcomp=null;
			// Get a reference to the object registered as a service of type interfacename
			// by the server bundle:
			ServiceReference serverref=null;
			ServiceReference srefs[] = client.getBundleContext().getAllServiceReferences(interfacename, null);
			for (int i=0;i<srefs.length;i++){
				if (srefs[i].getBundle()==server){
					serverimpl = client.getBundleContext().getService(srefs[i]);
					serverref=srefs[i];
				}
			}
			srefs = client.getBundleContext().getAllServiceReferences("eu.linksmart.asl.interfaces.Bindable", null);
			for (int j=0; j<srefs.length;j++){
				if(srefs[j].getBundle()==client){
					clientcomp=client.getBundleContext().getService(srefs[j]); // getting the bindable object within the client bundle
				}
			}
			// check everything went ok:
			if ((serverimpl==null)||(clientcomp==null)||(serverref==null))
				throw new Exception("Cannot bind! (serverimpl,clientcomp,serverref="+serverimpl+clientcomp+serverref+")");
			//get find bind and unbind methods through bindable interface:
			Method getbind = clientcomp.getClass().getMethod("getBindMethod", new Class[]{String.class});
			String bindname = (String)getbind.invoke(clientcomp, new Object[]{interfacename});
			Method bind = clientcomp.getClass().getMethod(bindname, new Class[]{Object.class});
			//assume no server currently bound:
			//bind server
			bind.invoke(clientcomp, new Object[]{serverimpl});
			// the server was getService'ed above on serverref, so no need to do it again
		/*} catch (InvalidSyntaxException e) {
			// only if filter has wrong syntax; none given !
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();*/
		} catch (Exception e){
			System.out.println(e);
			e.printStackTrace(System.out);
		}
		
		
	}

	
	/* (non-Javadoc)
	 * @see testbed.OSGi_Device#unbind(testbed.Equinox_OSGI_Service, testbed.Equinox_OSGI_Service, java.lang.String)
	 */
/*	public void unbind(String clienturl, String serverurl, String interfacename){
		System.out.println("\t<Unbinding>"+clienturl+","+serverurl+","+interfacename);
		Bundle client,server;
		client = bundles.get(clienturl);
		server = bundles.get(serverurl);
		unbind(client,server,interfacename);
	}
*/
	/**
	 * Unbinds the client from the interface implemented by the server. The requirements 
	 * the client and server bundles must comply with are as follows: First, the server must
	 * register a service implementing the given interface. Second, the client must have
	 * registered a service implementing the eu.linksmart.asl.interfaces.Bindable interface. 
	 * Third, the Bindable service registered by the client must implement the 
	 * getUnbindMethod(string c), returning the name of the actual method used to unbind the
	 * client's Bindable component from the server's service of type c.
	 */

	
	public void unbind(Bundle client, Bundle server, String interfacename){
		//BundleContext cbc= c.getBundleContext();
		try {
			Object serverimpl=null,clientcomp=null;
			ServiceReference serverref=null;
			ServiceReference srefs[] = client.getBundleContext().getAllServiceReferences(interfacename, null);
			for (int i=0;i<srefs.length;i++){
				if (srefs[i].getBundle()==server){
					serverimpl = client.getBundleContext().getService(srefs[i]);
					serverref=srefs[i];
				}
				/*if (srefs[i].getBundle()==nserver){
					nserverimpl = client.getBundleContext().getService(srefs[i]);
					nserverref=srefs[i];
				}*/
			}
			srefs = client.getBundleContext().getAllServiceReferences("eu.linksmart.asl.interfaces.Bindable", null);
			for (int j=0; j<srefs.length;j++){
				if(srefs[j].getBundle()==client){
					clientcomp=client.getBundleContext().getService(srefs[j]); // getting the bindable object within the client bundle
				}
			}
			// check everything went ok:
			if ((serverimpl==null)||(clientcomp==null)||(serverref==null))
				throw new Exception("Cannot unbind! (serverimpl,clientcomp,serverref="+serverimpl+clientcomp+serverref+")");
			//get find bind and unbind methods through bindable interface:
			Method getunbind = clientcomp.getClass().getMethod("getUnbindMethod", new Class[]{String.class});
			String unbindname = (String)getunbind.invoke(clientcomp, new Object[]{interfacename});
			Method unbind = clientcomp.getClass().getMethod(unbindname, new Class[]{Object.class});
			//assume no server currently bound:
			//bind server
			unbind.invoke(clientcomp, new Object[]{serverimpl});
			// the server was getService'ed above, and when originally bound, so we need to unget it two times:
			client.getBundleContext().ungetService(serverref);
			client.getBundleContext().ungetService(serverref);
		/*} catch (InvalidSyntaxException e) {
			// only if filter has wrong syntax; none given !
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();*/
		} catch (Exception e){
			System.out.println(e);
			e.printStackTrace(System.out);
		}
	}


	
	/* (non-Javadoc)
	 * @see testbed.OSGi_Device#printStatus()
	 */
	public String printStatus() {
		try {
			BundleContext bc = getBundleContext();
		} catch (Exception e){
			System.out.println("-Exception: "+e);
			e.printStackTrace(System.out);
		}
		System.out.println(bc+" bundles");
		Bundle b[] = bc.getBundles();
		StringBuffer sb = new StringBuffer();
		sb.append("ID\tSTATUS\tNAME\n");
		int status;
		String state="unknown";
		for(int i=0;i<b.length;i++){
			sb.append(b[i].getBundleId()+"\t"); // bundle id
			status = b[i].getState();
			switch (status){
			case Bundle.ACTIVE:      state="ACTIVE"; break;
			case Bundle.INSTALLED:   state="INSTALLED"; break;
			case Bundle.RESOLVED:    state="RESOLVED"; break;
			case Bundle.STARTING:    state="STARTING"; break;
			case Bundle.STOPPING:    state="STOPPING"; break;
			case Bundle.UNINSTALLED: state="UNINSTALLED"; break;
			}
			sb.append(state+"\t"); // bundle status
			sb.append(b[i].getSymbolicName()+"\n"); // symbolic name
		}
		return sb.toString();
	}
	
	/**
	 * The server bundle must have registered a service implementing testbed.ServiceInfo
	 * so that it can return the object that must be unbound from the current client and
	 * bound again to the new one. In addtion, it will provide the names of the bind and
	 * unbind methods on that object.
	 * 
	 * @param clienturl
	 * @param curserverurl
	 * @param newserverurl
	 * @param interfacename
	 */
/*	public void rebind(String clienturl, String curserverurl, String newserverurl, String interfacename){
		Bundle client,cserver,nserver;
		client = bundles.get(clienturl);
		cserver = bundles.get(curserverurl);
		nserver = bundles.get(newserverurl);
		try {
			Object cserverimpl=null,clientcomp=null,nserverimpl=null;
			ServiceReference cserverref=null, nserverref=null;
			ServiceReference srefs[] = client.getBundleContext().getAllServiceReferences(interfacename, null);
			for (int i=0;i<srefs.length;i++){
				if (srefs[i].getBundle()==cserver){
					cserverimpl = client.getBundleContext().getService(srefs[i]);
					cserverref=srefs[i];
				}
				if (srefs[i].getBundle()==nserver){
					nserverimpl = client.getBundleContext().getService(srefs[i]);
					nserverref=srefs[i];
				}
			}
			srefs = client.getBundleContext().getAllServiceReferences("Bindable", null);
			for (int j=0; j<srefs.length;j++){
				if(srefs[j].getBundle()==client){
					clientcomp=client.getBundleContext().getService(srefs[j]); // getting the bindable object within the client bundle
				}
			}
			// check everything went ok:
			if ((cserverimpl==null)||(clientcomp==null)||(nserverimpl==null)||(cserverref==null)||(nserverref==null))
				throw new Exception("Cannot rebind! (cserverimpl,clientcomp,nserverimpl,cserverref,nserverref="+cserverimpl+clientcomp+nserverimpl+cserverref+nserverref+")");
			//get find bind and unbind methods through bindable interface:
			Method getbind = clientcomp.getClass().getMethod("getBindMethod", new Class[]{String.class});
			Method getunbind = clientcomp.getClass().getMethod("getUnbindMethod", new Class[]{String.class});
			String bindname = (String)getbind.invoke(clientcomp, new Object[]{interfacename});
			String unbindname = (String)getunbind.invoke(clientcomp, new Object[]{interfacename});
			Method bind = clientcomp.getClass().getMethod(bindname, new Class[]{Object.class});
			Method unbind = clientcomp.getClass().getMethod(unbindname, new Class[]{Object.class});
			//unbind current server:
			unbind.invoke(clientcomp,new Object[]{cserverimpl});
			//bind new server
			bind.invoke(clientcomp,new Object[]{nserverimpl});
			// unget two times - first for the original binding now removed, second corresponding to the get above...
			client.getBundleContext().ungetService(cserverref);
			client.getBundleContext().ungetService(cserverref);
			// the new server was getService'ed above on nserverref, so no need to do it again
		} catch (InvalidSyntaxException e) {
			// only if filter has wrong syntax; none given !
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
*/	


}
