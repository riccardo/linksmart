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
package eu.linksmart.caf.daqc.reporting;

import eu.linksmart.caf.daqc.report.DaqcReportingService;
import eu.linksmart.caf.daqc.report.Data;
import eu.linksmart.caf.daqc.report.DataReport;

/**
 * Implemented {@link Runnable} that packages the data into a {@link DataReport}
 * and sends to the {@link DaqcReportingService} provided by the
 * {@link SubscriberStub}
 * 
 * @author Michael Crouch
 * 
 */
public class DataReportRunnable implements Runnable {

	/** the data aliases */
	private final String[] dataIdAliases;
	
	/** the data to report */
	private final Data[] sendData;
	
	/** the {@link SubscriberStub} to report to */
	private final SubscriberStub reportTo;
	
	/** the status of the report */
	private final boolean status;
	
	/** the associated message */
	private final String message;

	/**
	 * Constructor
	 * @param reportTo the {@link SubscriberStub} to report to
	 * @param aliases the data aliases 
	 * @param sendData the data to report
	 * @param status the status of the report
	 * @param message the associated message
	 */
	public DataReportRunnable(SubscriberStub reportTo, String[] aliases,
			Data[] sendData, boolean status, String message) {
		this.reportTo = reportTo;
		this.dataIdAliases = aliases.clone();
		this.sendData = sendData;
		this.status = status;
		this.message = message;
	}

	@Override
	public void run() {
		DataReport dataReport = new DataReport();

		dataReport.setAliases(dataIdAliases);
		dataReport.setReportedData(sendData);
		dataReport.setMessage(message);
		dataReport.setStatus(status);
		reportTo.report(dataReport);
	}
}
