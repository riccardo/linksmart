package eu.linksmart.network.backbone.impl.jxta;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.ComponentContext;


//import eu.linksmart.network.identity.HIDManagerApplication;
//import eu.linksmart.network.impl.NetworkManagerApplicationSoapBindingImpl;

/**
 * NetworkManagerApplication Servlet
 */
public class BackboneJXTAStatus extends HttpServlet {

	ComponentContext context;


	/**
	 * Constructor
	 * 
	 * @param context
	 *            the bundle's context
	 * @param nmServiceImpl
	 *            the Network Manager Service implementation
	 */
	public BackboneJXTAStatus(ComponentContext context) {

		this.context = context;
	}

	/**
	 * Performs the HTTP GET operation
	 * 
	 * @param request
	 *            HttpServletRequest that encapsulates the request to the
	 *            servlet
	 * @param response
	 *            HttpServletResponse that encapsulates the response from the
	 *            servlet
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		response.setContentType("text/html");
		URL cssFile = context.getBundleContext().getBundle()
				.getResource("resources/NetworkManager.css");
		BufferedReader cssReader = new BufferedReader(new InputStreamReader(
				cssFile.openStream()));

		String temp;
		String css = "";
		while ((temp = cssReader.readLine()) != null) {
			css = css + temp;
		}
		cssReader.close();

		response.getWriter().println(
				"<html><head>" + "<style type=\"text/css\">" + css
						+ "</style></head>");
		response.getWriter().println(
				"<body><table><tr>" + "<td valign=\"middle\" width=80%><h1>"
						+ "Status page for the local Network Manager</h1></td>"
						+ "<td align=\"right\" width=20%>"
						+ "<img src=\"files/0.gif\" /></td></tr></table>");
		response.getWriter().println(
				"<h1>" + "Status of the JXTA Backbone" + "</h1>");

		// Add content of status page

		// End of page
		response.getWriter().println("</body></html>");
	}

}
