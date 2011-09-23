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
package eu.linksmart.selfstar.aom.example;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ThermometerDataServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private ThermometerSampler sampler;
	
	public ThermometerDataServlet(ThermometerSampler sampler) {
		this.sampler = sampler;
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		out.println(dataToString());
		out.close();
	}
	
	private String dataToString() {
		
		LinkedList<String> ids = new LinkedList<String>(sampler.getTemperatureData().keySet());
		Collections.sort(ids);
		
		StringBuffer output = new StringBuffer("");
		output.append("{ 'ids': [");

		
		int maxSize = 0;
		for (int i = 0; i < ids.size(); i++) {
			String id = ids.get(i);
			output.append("'" + id + "'");
			if (i < ids.size() - 1) {
				output.append(",");
			}
			maxSize = Math.max(maxSize, sampler.getTemperatureData().get(id).size());
		}
		output.append("], 'data': ");
		output.append("[");
		for (int j = 0; j < ids.size(); j++) {
			String id = ids.get(j);
			output.append("[");
			
			LinkedList<Double> data = new LinkedList<Double>(sampler.getTemperatureData().get(id));
			for (int i = 0; i < maxSize - data.size(); i ++) {
				data.push(0.0);
			}
			
			for (int i = 0; i < data.size(); i++) {
				output.append(data.get(i));
				if (i < data.size() - 1) {
					output.append(", ");
				}
			}
			output.append("]");
			if (j < ids.size() - 1) {
				output.append(", ");
			}
		}
		output.append("]");
		output.append(", battery: '" + sampler.getBatteryLevel() + "'");
		output.append("}");

		return output.toString();
	}

}
