package eu.linksmart.clients;

import eu.linksmart.network.NMResponse;
import eu.linksmart.network.Registration;
import eu.linksmart.network.VirtualAddress;

import java.rmi.RemoteException;

/**
 * Created by carlos on 15.07.14.
 */
public interface BasicClient {

    public NMResponse sendData(VirtualAddress receiver, byte [] data, boolean synch) throws RemoteException;
    public Registration[] getServiceByDescription(String description) throws RemoteException;
}
