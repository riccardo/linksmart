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
 * Copyright (C) 2006-2010 [Telefonica I+D]
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

/**
 * Implementation of the interface ShellConfigurator, that can can be used for
 * configuring the  middleware.
 */

package eu.linksmart.configurator.shell;

import java.util.Dictionary;

//import org.eclipse.osgi.framework.console.CommandInterpreter;
//import org.eclipse.osgi.framework.console.CommandProvider;
import org.osgi.framework.BundleContext;

import eu.linksmart.configurator.impl.ConfiguratorImpl;


/**
 * Equinox Shell Configurator that provides support for configuring the 
 *  middleware bundles
 */
//public class EquinoxShellConfigurator extends ShellConfigurator implements CommandProvider {
public class EquinoxShellConfigurator extends ShellConfigurator {
	
	/**
	 * Constructor of the class EquinoxShellConfigurator
	 * 
	 * @param configurator an implementation of the interface 
	 * eu.linksmart.configurator.Configurator
	 * @param context the bundle's execution context
	 */
	public EquinoxShellConfigurator(ConfiguratorImpl configurator,
			BundleContext context) {
		
		super(configurator, context);
		//context.registerService(CommandProvider.class.getName(), this, null);
	}
	
//	/**
//	 * _configure method
//	 * @param ci Command Interpreter
//	 */
//	public void _configure(CommandInterpreter ci) {
//		StringBuffer buffer = new StringBuffer();
//		
//		String arg1 = ci.nextArgument();
//		if (arg1 == null) {
//			ci.print(getHelp());
//			return;
//		}
//		if (arg1.equals("-l")) {
//			buffer.append("List of available configurations\n");
//			/* List all configurations. */
//			String[] configs = configurator.getAvailableConfigurations();
//			for (int i = 0; i < configs.length; i++) {
//				buffer.append("[" + i + "] - " + configs[i] + "\n");
//			}
//			ci.print(buffer.toString());
//		}
//		else if (arg1.equals("-d")) {
//			String arg2 = ci.nextArgument();
//			if (arg2 == null) {
//				ci.print(getHelp());
//				return;
//			}
//			else {
//				configurator.configure(arg2, null);
//				buffer.append("Deleted configuration for " + arg2 + "\n");
//				ci.print(buffer.toString());
//			return;
//			}
//		}
//		else if (arg1 == null) {
//			ci.print(getHelp());
//			return;
//		}
//		else {
//			String arg2 = ci.nextArgument();
//			if (arg2 == null) {
//				Dictionary d = configurator.getConfiguration(arg1);
//				if (d != null) {
//					ci.printDictionary(d, "Current configuration for " + arg1 + "\n");
//				}
//				else {
//					buffer.append("No configuration found for " + arg1 + " or "
//						+ "the configuration is empty\n");
//					ci.print(buffer.toString());
//					return;
//				}
//			}
//			else {
//				String arg3 = ci.nextArgument();
//				if (arg3 == null) {
//					String value = (String) configurator.getConfiguration(arg1).get(arg2);
//					buffer.append(arg1 + " - " + arg2 + " = " + value);
//					ci.print(buffer.toString());
//					return;
//				}
//				else {
//					configurator.configure(arg1, arg2, arg3);
//					String value = (String) configurator.getConfiguration(arg1).get(arg2);
//					buffer.append(arg1 + " - " + arg2 + " = " + value);
//					ci.print(buffer.toString());
//					return;
//				}
//			}
//		}
//	}
//
//	/**
//	 * Returns a string with launching information 
//	 * @return string the string with launching information
//	 */
//	public String getHelp() {
//		return "\tconfigure [-l] [-d] [<pid> , [<key> <value>]] - Provides "
//			+ "support for configuring the  middleware bundles\n";
//	}

	/**
	 * Unregisters a command
	 */
	@Override
	public void unregisterCommnand() {}

}
