package eu.linksmart.network.tunnel;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import eu.linksmart.network.VirtualAddress;
import eu.linksmart.network.tunnel.MultipleMatchException;

public interface BasicTunnelService {
	
	public static final String NO_SERVICE = "Did not find matching service";
	public static final int SERVICE_DISCOVERY_TIMEOUT = 10*1000;
	public static final String INVALID_VIRTUAL_ADDRESS_FORMAT = "Was not able to parse Virtual Address";

	void sendRequest(VirtualAddress senderVirtualAddress,
			VirtualAddress receiverVirtualAddress, String requestString,
			HttpServletResponse response) throws IOException;

	String processRequest(HttpServletRequest request,
			HttpServletResponse response, boolean hasData) throws IOException;

	byte[] composeResponse(byte[] byteData, HttpServletResponse response);

	VirtualAddress getSenderVirtualAddressFromPath(HttpServletRequest request,
			VirtualAddress defaultSender);

	VirtualAddress getReceiverVirtualAddressFromPath(HttpServletRequest request)
			throws MultipleMatchException, Exception;

}
