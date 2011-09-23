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

import java.util.ArrayList;
import java.util.List;

/**
 * Instance of class collects reason result elements in several array lists.
 * 
 * @author Amro Al-Akkad
 * 
 */
public class ReasonResult {

	/**
	 * Collection for mean requirements.
	 */
	private final List<Req> meanRequirementList;

	/**
	 * Collection for extreme requirements.
	 */
	private final List<Req> extremeRequirementList;

	/**
	 * Auxiliary list for calculating correct average when an extremeReq is set.
	 */
	private final List<Req> auxExtremeRequirementList;

	/**
	 * Collection for average mean percentage list.
	 */
	private final List<ResultElement> averageMeanPercentageList;

	/**
	 * Collection for middle result list.
	 */
	private final List<ResultElement> middleResultList;

	/**
	 * Collection for select result list.
	 */
	private final List<ResultElement> selectResultList;

	/**
	 * Constructs a reason result.
	 */
	public ReasonResult() {
		super();

		this.meanRequirementList = new ArrayList<Req>();
		this.extremeRequirementList = new ArrayList<Req>();
		this.auxExtremeRequirementList = new ArrayList<Req>();

		this.averageMeanPercentageList = new ArrayList<ResultElement>();
		this.middleResultList = new ArrayList<ResultElement>();
		this.selectResultList = new ArrayList<ResultElement>();
	}

	/**
	 * @param name
	 *            Name of mean requirement.
	 * @param serviceName
	 *            Related service name of mean requirement.
	 * @param svalue
	 *            Value of requirement.
	 * @param unit
	 *            Unit of mean requirement.
	 * @param devicePID
	 *            Related device PID.
	 * @param deviceURI
	 *            Related device URI.
	 * @param disqualified
	 *            Flag indicating how well a device/service fits a not numeric
	 *            criteria.
	 */
	public void addMeanRequirement(String name, String serviceName,
			String svalue, String unit, String devicePID, String deviceURI,
			boolean disqualified) {

		Req requirement = new Req();
		requirement.setRequirementType(RequirementType.MEAN);
		requirement.setName(name);
		requirement.setServiceName(serviceName);

		try {
			requirement.setValue(Double.parseDouble(svalue));
		} catch (NumberFormatException e) {
			// the value is not numeric
			requirement.setValue(-1);
			requirement.setNotNumericValue(svalue);
		}
		requirement.setUnit(unit);
		requirement.setDevicePID(devicePID);
		requirement.setDeviceURI(deviceURI);
		requirement.setDisqualified(disqualified);

		this.meanRequirementList.add(requirement);

	}

	/**
	 * @param name
	 *            Name of extreme requirement.
	 * @param serviceName
	 *            Related service name of extreme requirement.
	 * @param svalue
	 *            Value of requirement.
	 * @param unit
	 *            Unit of extreme requirement.
	 * @param devicePID
	 *            Related device PID.
	 * @param deviceURI
	 *            Related device URI.
	 * @param disqualified
	 *            Flag indicating how well a device/service fits a not numeric
	 *            criteria.
	 */
	public void addExtremeRequirement(String name, String serviceName,
			String svalue, String unit, String devicePID, String deviceURI,
			boolean disqualified) {

		Req requirement = new Req();
		requirement.setRequirementType(RequirementType.EXTREME);
		requirement.setName(name);
		requirement.setServiceName(serviceName);
		try {
			requirement.setValue(Double.parseDouble(svalue));
		} catch (NumberFormatException e) {
			// the value is not numeric
			requirement.setValue(-1);
			requirement.setNotNumericValue(svalue);
		}
		requirement.setUnit(unit);
		requirement.setDevicePID(devicePID);
		requirement.setDeviceURI(deviceURI);
		requirement.setDisqualified(disqualified);

		this.extremeRequirementList.add(requirement);

	}

	/**
	 * @return Returns the list of auxiliary extreme requirements.
	 */
	public List<Req> getAuxExtremeRequirementList() {
		return auxExtremeRequirementList;
	}

	/**
	 * @param name
	 *            Name of auxiliary extreme requirement.
	 * @param serviceName
	 *            Related service name of auxiliary extreme requirement.
	 * @param svalue
	 *            Value of requirement.
	 * @param unit
	 *            Unit of auxiliary extreme requirement.
	 * @param devicePID
	 *            Related device PID.
	 * @param deviceURI
	 *            Related device URI.
	 * @param disqualified
	 *            Flag indicating how well a device/service fits a not numeric
	 *            criteria.
	 */
	public void addAuxExtremeRequirement(String name, String serviceName,
			String svalue, String unit, String devicePID, String deviceURI,
			boolean disqualified) {

		Req requirement = new Req();
		requirement.setRequirementType(RequirementType.EXTREME);
		requirement.setName(name);
		requirement.setServiceName(serviceName);
		try {
			requirement.setValue(Double.parseDouble(svalue));
		} catch (NumberFormatException e) {
			// the value is not numeric
			requirement.setValue(-1);
			requirement.setNotNumericValue(svalue);
		}
		requirement.setUnit(unit);
		requirement.setDevicePID(devicePID);
		requirement.setDeviceURI(deviceURI);
		requirement.setDisqualified(disqualified);

		this.auxExtremeRequirementList.add(requirement);

	}

	/**
	 * @param serviceName
	 *            Service name.
	 * @param devicePID
	 *            Device PID.
	 * @param deviceURI
	 *            URI of a device.
	 * @param averagePercentage
	 *            Average Percentage.
	 * @param isDisqualified
	 *            Flag indicating if this result is disqualified for a
	 *            device/service or not.
	 */
	public void addAverageMeanPercentage(String serviceName, String devicePID,
			String deviceURI, double averagePercentage, boolean isDisqualified) {
		ResultElement resultElement = new ResultElement();
		resultElement
				.setResultElementType(ResultElementType.AVERAGEMEANPERCENTAGE);
		resultElement.setServiceName(serviceName);
		resultElement.setDevicePID(devicePID);
		resultElement.setDeviceURI(deviceURI);
		resultElement.setAveragePercentage(averagePercentage);
		resultElement.setDisqualified(isDisqualified);

		this.averageMeanPercentageList.add(resultElement);

	}

	/**
	 * @param serviceName
	 *            Service name.
	 * @param devicePID
	 *            Device PID.
	 * @param deviceURI
	 *            URI of a device.
	 * @param averagePercentage
	 *            Average Percentage.
	 * @param isDisqualified
	 *            Flag indicating if this result is disqualified for a
	 *            device/service or not.
	 */
	public void addMiddleResult(String serviceName, String devicePID,
			String deviceURI, double averagePercentage, boolean isDisqualified) {

		ResultElement resultElement = new ResultElement();
		resultElement.setResultElementType(ResultElementType.MIDDLERESULT);
		resultElement.setServiceName(serviceName);
		resultElement.setDeviceURI(deviceURI);
		resultElement.setDevicePID(devicePID);
		resultElement.setAveragePercentage(averagePercentage);
		resultElement.setDisqualified(isDisqualified);

		this.middleResultList.add(resultElement);

	}

	/**
	 * @param serviceName
	 *            Service name.
	 * @param devicePID
	 *            Device PID.
	 * @param deviceURI
	 *            URI of a device.
	 * @param averagePercentage
	 *            Average Percentage of a select result.
	 * @param isDisqualified
	 *            Flag indicating if this result is disqualified for a
	 *            device/service or not.
	 */
	public void addSelectResult(String serviceName, String devicePID,
			String deviceURI, double averagePercentage, boolean isDisqualified) {

		ResultElement resultElement = new ResultElement();
		resultElement.setResultElementType(ResultElementType.SELECTRESULT);
		resultElement.setServiceName(serviceName);
		resultElement.setDevicePID(devicePID);
		resultElement.setDeviceURI(deviceURI);
		resultElement.setAveragePercentage(averagePercentage);
		resultElement.setDisqualified(isDisqualified);

		this.selectResultList.add(resultElement);

	}

	/**
	 * @return Returns select resul list.
	 */
	public List<ResultElement> getSelectResultList() {
		return selectResultList;
	}

	/**
	 * @param nfparameter
	 *            Non functional parameter of a specific mean requirement.
	 * @return Returns a list of <b>Req</b> elements that contain the specified
	 *         non functional parameter.
	 */
	public List<Req> getSpecificMeanRequirementList(String nfparameter) {

		List<Req> list = new ArrayList<Req>();

		for (Req req : this.meanRequirementList) {

			if (req.getName().equals(nfparameter))
				list.add(req);

		}

		return list;
	}

	/**
	 * @param nfparameter
	 *            Non functional parameter of a specific extreme requirement.
	 * @return Returns a list of <b>Req</b> elements that contain the specified
	 *         non functional parameter.
	 */
	public List<Req> getSpecificExtremeRequirementList(String nfparameter) {

		List<Req> list = new ArrayList<Req>();

		for (Req req : this.extremeRequirementList) {

			if (req.getName().equals(nfparameter))
				list.add(req);

		}

		return list;
	}

	/**
	 * @param serviceName
	 *            Related service name.
	 * @return Returns a specific average mean percentage list of
	 *         <b>ResultElement</b> according to the specified service name
	 *         parameter.
	 */
	public List<ResultElement> getSpecificAverageMeanPercentageList(
			String serviceName) {

		List<ResultElement> list = new ArrayList<ResultElement>();

		for (ResultElement resultElement : this.averageMeanPercentageList) {
			if (resultElement.getServiceName().equals(serviceName))
				list.add(resultElement);
		}
		return list;

	}

	/**
	 * @param serviceName
	 *            Related service name.
	 * @return Returns a specific list of <b>Req</b> according to the specified
	 *         service name parameter.
	 */
	public Req getSpecificAuxExtremeMeanPercentageList(String serviceName) {

		for (Req req : this.auxExtremeRequirementList) {

			if (req.getUniqueID().equals(serviceName))
				return req;
		}

		return null;

	}

	/**
	 * @return Returns the average mean percentage list.
	 */
	public List<ResultElement> getAverageMeanPercentageList() {
		return this.averageMeanPercentageList;
	}

	/**
	 * @return Returns the middle result list.
	 */
	public List<ResultElement> getMiddleResultList() {
		return this.middleResultList;
	}

	/**
	 * @return Returns the mean requirement list.
	 */
	public List<Req> getMeanRequirementList() {
		return this.meanRequirementList;

	}

	/**
	 * @return Returns the extreme requirement list.
	 */
	public List<Req> getExtremeRequirementList() {
		return this.extremeRequirementList;
	}

	/**
	 * @param serviceName
	 *            Related service name.
	 * @return Returns a specific select list of <b>ResultElement</b> according
	 *         to the specified service name parameter.
	 */
	public ResultElement getSpecificSelectResultElement(String serviceName) {
		for (ResultElement resultElement : this.selectResultList) {

			if (resultElement.getServiceName().equals(serviceName))
				return resultElement;
		}

		return null;
	}

}
