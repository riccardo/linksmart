package eu.linksmart.remote.service.store;

import java.util.Map;

public interface LinkSmartRemoteServiceStore {

	public Object getRemoteHydraServiceByPID(String pid, String className) throws Exception;

	public Object getRemoteHydraServiceByDescription(String description, String className) throws Exception;
	
	public void removeRemoteHydraServiceByDescription(String description);

	/**
	 * @param query  The format of the query is supposed be like this:
	 * (key1==cond1)&&(key2==cond2*)||(key3==cond3)...</code>
	 * 
	 */
	public String getServiceCryptoHID(String query);
	
	/**
	 * @param attributes Method connects key values pairs with && operand
	 */
	public String getHydraServiceHIDByAttributes(Map <String,String> attributes);
}
