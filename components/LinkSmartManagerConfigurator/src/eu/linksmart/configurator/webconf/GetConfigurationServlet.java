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

package eu.linksmart.configurator.webconf;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.JSONWriter;

import eu.linksmart.configurator.Configurator;


/**
 * Servlet for getting configuration
 */
public class GetConfigurationServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private Configurator config;

	/**
	 * Constructor
	 * 
	 * @param config the configuration properties of LinkSmart middleware
	 */
	public GetConfigurationServlet(Configurator config) {
		this.config = config;
	}
	
	/**
	 * Performs the HTTP GET operation
	 * 
	 * @param request HttpServletRequest that encapsulates the request to the servlet 
	 * @param response HttpServletResponse that encapsulates the response from the servlet
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		
		Map params = request.getParameterMap();
		if(params.containsKey("method")) {
			Object s = params.get("method");
			if (((String[]) params.get("method"))[0].equals("listConfigurations")) {
				String[] configs = config.getAvailableConfigurations();
				StringWriter writer = new StringWriter();
				JSONWriter jsonWriter = new JSONWriter(writer);
				
				try {
					jsonWriter.array().object().key("configurations").array();
					if (configs != null) {
						for (int i = 0; i < configs.length; i++) {
							jsonWriter.value(configs[i]);
						}
					}
					jsonWriter.endArray().endObject().endArray();
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				response.setContentLength(writer.toString().getBytes().length);
				response.getWriter().write(writer.toString());
			}
			else if ((((String[]) params.get("method"))[0].equals("getConfiguration"))
					&& (params.containsKey("configName"))) {
				
				String configName = ((String[]) params.get("configName"))[0];
				Dictionary configuration = config.getConfiguration(configName);
				Enumeration keys = configuration.keys();			
				StringWriter writer = new StringWriter();
				JSONWriter jsonWriter = new JSONWriter(writer);
				
				try {
					jsonWriter.array().object().key("name").value(configName).
						key("parameters").array();
					while (keys.hasMoreElements()) {
						String key = (String) keys.nextElement();
						String value = (String) configuration.get(key);
						jsonWriter.object().key("name").value(key).key("value").
							value(value).endObject();
					}
					jsonWriter.endArray().endObject().endArray();
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				response.setContentLength(writer.toString().getBytes().length);
				response.getWriter().write(writer.toString());
			}
			else if (((String[]) params.get("method"))[0].equals("postConfiguration") 
					&& (params.containsKey("configuration"))) {
				
				String config = ((String[]) params.get("configuration"))[0];
				JSONArray json = null;

				try {
					json = new JSONArray(new JSONTokener(config));
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				
				try {
					Dictionary d = new Hashtable();
					String configName = (String) json.getJSONObject(0).get("name");
					JSONArray parameters = json.getJSONObject(0).getJSONArray("parameters");
					for (int i = 0; i < parameters.length(); i++) {
						JSONObject jobj = parameters.getJSONObject(i);
						d.put((String) jobj.get("name"), (String) jobj.get("value"));
					}
					this.config.configure(configName, d);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Performs the HTTP POST operation
	 * 
	 * @param request HttpServletRequest that encapsulates the request to the servlet
	 * @param response HttpServletResponse that encapsulates the response from the servlet
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws IOException {
		
		Map params  = request.getParameterMap();
		if(params.containsKey("method")) {
			Object s = params.get("method");
			if (((String[]) params.get("method"))[0].equals("postConfiguration")
					&& (params.containsKey("configuration"))) {
				String config = ((String[]) params.get("configuration"))[0];
				JSONArray json = null;

				try {
					json = new JSONArray(new JSONTokener(config));
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				
				try {
					Dictionary d = new Hashtable();
					String configName = (String) json.getJSONObject(0).get("name");
					JSONArray parameters = json.getJSONObject(0).getJSONArray("parameters");
					for (int i = 0; i < parameters.length(); i++) {
						JSONObject jobj = parameters.getJSONObject(i);
						d.put((String) jobj.get("name"), (String) jobj.get("value"));
					}
					this.config.configure(configName, d);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Main method to make a test
	 * 
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		StringWriter writer = new StringWriter();
		JSONWriter jsonWriter = new JSONWriter(writer);
		
		try {
			jsonWriter = jsonWriter.array().object().key("key1").array().value("value1");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
