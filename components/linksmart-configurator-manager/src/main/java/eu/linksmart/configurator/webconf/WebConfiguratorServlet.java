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
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Configurator WebServlet
 */
public class WebConfiguratorServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static Map<String, String> contentTypes;
	
	static {
		contentTypes = new HashMap<String, String>();
		contentTypes.put("html", "text/html");
		contentTypes.put("css", "text/css");
		contentTypes.put("js", "text/javascript");
		contentTypes.put("png", "image/png");
		contentTypes.put("gif", "image/gif");
		contentTypes.put("jpg", "image/jpeg");
	}
	
	/**
	 * Performs the HTTP GET operation
	 * 
	 * @param request HttpServletRequest that encapsulates the request to the servlet 
	 * @param response HttpServletResponse that encapsulates the response from the servlet
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws IOException {
		
		OutputStream body = response.getOutputStream();
		String path = request.getPathInfo();
		byte[] buffer = new byte[8192];
		int read;
		String resource;
		
		/* Get resource path. */
		if (path == null) {
			resource = "/resources/index.html";
		}
		else if(path.equals("/")) {
			resource = "/resources/index.html";
		}
		else {
			resource = "/resources" + path;
		}
		
		/* Get Content-Type. */
		String extension = resource.substring(resource.lastIndexOf('.') + 1);
		String contentType = contentTypes.get(extension);
		
		/* Get resource as stream. */
		InputStream stream = WebConfiguratorServlet.class.getResourceAsStream(resource);
		
		/* Check if resource was found. */
		if(stream != null){
			/* Set headers. */
			response.setHeader("Content-Type", contentType);
			response.setHeader("Cache-Control", "max-age=604800");
			
			/* Send response header with Content-Length. */
			response.setStatus(200);
			response.setContentLength(stream.available());
			
			/* Write response to output stream. */
			while((read = stream.read(buffer)) != -1){
				body.write(buffer, 0, read);
			}
		}
		else {
			byte[] bytes = "404 Not Found".getBytes();
			
			/* Send response header with Content-Length. */
			response.setStatus(404);
			response.setContentLength(bytes.length);
			
			body.write(bytes);
		}
		
		/* Close. */
		body.close();
	}

}
