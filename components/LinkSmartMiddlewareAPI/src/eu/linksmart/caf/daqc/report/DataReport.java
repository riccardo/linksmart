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
 * Copyright (C) 2006-2010 University of Reading,
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
package eu.linksmart.caf.daqc.report;

import eu.linksmart.caf.daqc.subscription.Subscription;

/**
 * Container for the {@link Data} to be reported back to subscribers. The associated
 * {@link Data} (in an array), is provided along with all subscirber-known aliases
 * for the data.<p>
 * 
 * The {@link DataReport} also contains the status of the {@link Subscription} the report is
 * responding to, as well as a description message.
 * @author Michael Crouch
 *
 */
public class DataReport {
	
	private String[] aliases;
	private Data[] reportedData;
	
	/**
	 * Status and message allows DataReport to inform subscriber of a failure in communication with the
	 * data source. Failure will result in the subscription being cancelled.
	 */
	private boolean status;
	private String message;
	
	/**
	 * Gets the known aliases for the {@link Data}
	 * @return the array of aliases
	 */
	public String[] getAliases() {
		return aliases;
	}

	/**
	 * Sets the aliases
	 * @param aliases the array of aliases
	 */
	public void setAliases(String[] aliases) {
		this.aliases = aliases;
	}

	/**
	 * Gets the array of {@link Data} in the report
	 * @return the array of {@link Data}
	 */
	public Data[] getReportedData() {
		return reportedData;
	}

	/**
	 * Sets the array of {@link Data} in the report
	 * @param reportedData the array of {@link Data}
	 */
	public void setReportedData(Data[] reportedData) {
		this.reportedData = reportedData;
	}

	/**
	 * Returns the status of the {@link Subscription}
	 * @return the status
	 */
	public boolean isStatus() {
		return status;
	}

	/**
	 * Sets the status of the {@link Subscription}
	 * @param status the status
	 */
	public void setStatus(boolean status) {
		this.status = status;
	}

	/**
	 * Gets the message
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Sets the message
	 * @param message the message
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	
	
}
