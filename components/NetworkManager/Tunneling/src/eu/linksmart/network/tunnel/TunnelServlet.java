package eu.linksmart.network.tunnel;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import eu.linksmart.network.NMResponse;
import eu.linksmart.network.VirtualAddress;

public class TunnelServlet extends HttpServlet{

	private static final long serialVersionUID = 5473388358983564206L;
	private Tunnel tunnel;
	private static final Logger logger = Logger
			.getLogger(TunnelServlet.class.getName());

	protected TunnelServlet(Tunnel tunnel) {
		this.tunnel = tunnel;
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws IOException {
		logger.debug("Tunnel received GET request");
		processRequest(request, response, false);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		logger.debug("Tunnel received POST request");
		processRequest(request, response, true);
	}

	private void processRequest(HttpServletRequest request, HttpServletResponse response, boolean hasData) throws IOException {
		//get sender and receiver from request path
		VirtualAddress senderVirtualAddress = 
				tunnel.getBasicTunnelService().
				getSenderVirtualAddressFromPath(
						request, tunnel.getNM().getVirtualAddress());
		if(senderVirtualAddress == null) {
			response.sendError(
					HttpServletResponse.SC_BAD_REQUEST, BasicTunnelService.INVALID_VIRTUAL_ADDRESS_FORMAT);
			return;
		}
		VirtualAddress receiverVirtualAddress = null;
		try {
			receiverVirtualAddress = 
					tunnel.getBasicTunnelService().
					getReceiverVirtualAddressFromPath(request);
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
			return;
		}
		if(receiverVirtualAddress == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, BasicTunnelService.NO_SERVICE);
		}
		//compose request and headers
		String requestString = tunnel.getBasicTunnelService().processRequest(
				request,
				response,
				hasData);

		//send over LinkSmart
		NMResponse r = tunnel.getNM().sendData(
				senderVirtualAddress,
				receiverVirtualAddress,
				requestString.getBytes(),
				true);

		byte[] body = null;
		if (!r.getMessage().startsWith("HTTP/1.1 200 OK")) {
			response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
			//set whole response data as body
			body = r.getMessage().getBytes();
		} else {
			body = tunnel.getBasicTunnelService().composeResponse(r.getMessageBytes(), response);
		}
		//write body data	
		response.setContentLength(body.length);
		response.getOutputStream().write(body);
		response.getOutputStream().close();
	}
}
