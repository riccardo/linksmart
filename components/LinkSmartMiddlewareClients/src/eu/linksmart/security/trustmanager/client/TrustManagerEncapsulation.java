package eu.linksmart.security.trustmanager.client;

import java.rmi.RemoteException;

import eu.linksmart.security.trustmanager.TrustManager;

public class TrustManagerEncapsulation implements TrustManager{
	
	private TrustManagerPortType tmPort = null;
	
	public TrustManagerEncapsulation(TrustManagerPortType tmPort) {
		this.tmPort = tmPort;
	}

	@Override
	public double getTrustValue(String token) throws RemoteException {
		return tmPort.getTrustValue(token);
	}

	@Override
	public double getTrustValueWithIdentifier(String token,
			String trustModelIdentifier) throws RemoteException {
		return tmPort.getTrustValueWithIdentifier(token, trustModelIdentifier);
	}

	@Override
	public String createTrustToken() throws RemoteException {
		return createTrustToken();
	}

	@Override
	public boolean createTrustTokenWithFriendlyName(String identifier)
			throws RemoteException {
		return createTrustTokenWithFriendlyName(identifier);
	}

	@Override
	public String getTrustToken(String identifier) throws RemoteException {
		return getTrustToken(identifier);
	}

}
