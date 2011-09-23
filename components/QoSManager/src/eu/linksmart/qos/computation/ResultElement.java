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
package eu.linksmart.qos.computation;

/**
 * Instance of class defines a result element of the final QoS result list.
 * @author Amro Al-Akkad
 *
 */

public class ResultElement{
	
	private ResultElementType resultElementType;
	
	private String devicePID;
	
	private String serviceName;
	
	private String deviceURI;
	
	private double averagePercentage;
	
	private boolean isDisqualified=false;
	
	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public double getAveragePercentage() {
		return averagePercentage;
	}

	public void setAveragePercentage(double averagePercentage) {
		this.averagePercentage = averagePercentage;
	}

	public ResultElementType getResultElementType() {
		return resultElementType;
	}

	public void setResultElementType(ResultElementType resultElementType) {
		this.resultElementType = resultElementType;
	}

	public static void copy(ResultElement source, ResultElement dest){
		
		dest.setAveragePercentage(source.getAveragePercentage());
		dest.setDevicePID(source.getDevicePID());
		dest.setDeviceURI(source.getDeviceURI());
		dest.setResultElementType(source.getResultElementType());
		dest.setServiceName(source.getServiceName());
		dest.setDisqualified(source.isDisqualified());
		
	}

	public void setDeviceURI(String deviceURI) {
		this.deviceURI = deviceURI;
	}

	public String getDeviceURI() {
		return deviceURI;
	}

	public void setDevicePID(String devicePID) {
		this.devicePID = devicePID;
	}

	public String getDevicePID() {
		return devicePID;
	}

	public void setDisqualified(boolean isDisqualified) {
		this.isDisqualified = isDisqualified;
	}

	public boolean isDisqualified() {
		return isDisqualified;
	}

}
