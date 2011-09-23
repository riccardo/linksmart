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
package eu.linksmart.selfstar.test;

import org.junit.Test;
import org.osgi.framework.Constants;

import java.util.*;
import org.apache.felix.framework.Felix;
import static org.junit.Assert.*;

public class ASLTest {
    @Test
	public void installBundleInspector() throws Exception{
		// int port = 8080;
		// String wsdir=System.getProperty("lib.dir");
		// String fcmain=wsdir+"/flamenco_change_main/lib";
		// System.setProperty("ASL.withShell", "true");
		// System.setProperty("ASL.debug", "true");
		// Map map = new HashMap<String, String>();
		// map.put("org.osgi.service.http.port", "" + port);
		// map.put("org.osgi.service.http.port.secure", "8083");
		// map.put(Constants.FRAMEWORK_STORAGE, "fw-cache");
		// Felix felix = new Felix(map);
		// felix.start();
		// String[] locations = new String[]{
		//     //"file:"+wsdir+"/org.osgi.compendium-1.4.0.jar",
		//     "file:"+wsdir+"/../../../../distribution/SelfStarManager/selfstarmanager_asl/selfstarmanager_asl.jar",
		//     "file:"+wsdir+"/org.apache.felix.scr-1.4.0.jar",
		//     "file:"+wsdir+"/org.apache.felix.eventadmin-1.2.2.jar",
		//     "file:"+wsdir+"/eu.linksmart.bundleinspector-1.0.0.jar",
		// };
		// for (String location: locations) {
		//     felix.getBundleContext().installBundle(location);
		// }
	}
	
	public static void main(String args[]){
		try {
		    // FIXME: skip until build process is complete
		    // new ASLTest().installBundleInspector();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
