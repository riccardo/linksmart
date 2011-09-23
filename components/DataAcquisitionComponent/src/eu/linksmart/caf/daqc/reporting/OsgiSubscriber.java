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

import java.rmi.RemoteException;

import org.apache.log4j.Logger;

import eu.linksmart.caf.daqc.report.DaqcReportingService;
import eu.linksmart.caf.daqc.report.DataReport;
import eu.linksmart.caf.daqc.subscription.Subscriber;

/**
 * Implemented {@link SubscriberStub} for communication with a subscriber over
 * OSGi
 * 
 */
public class OsgiSubscriber extends SubscriberStub {

	/** the {@link Logger} */
	private static final Logger logger = Logger.getLogger(OsgiSubscriber.class);
	
	/** the {@link DaqcReportingService} */
	private final DaqcReportingService reportingService;

	/**
	 * Constructor
	 * 
	 * @param subscriber
	 *            the {@link Subscriber}
	 * @param reportingService
	 *            the {@link DaqcReportingService} to report to
	 */
	public OsgiSubscriber(Subscriber subscriber,
			DaqcReportingService reportingService) {
		super(subscriber);
		this.reportingService = reportingService;
	}

	/**
	 * Returns whether the service has been properly initialised
	 * 
	 * @return true if initialised correctly
	 */
	public boolean hasOsgiReporter() {
		if (reportingService == null)
			return false;
		return true;
	}

	@Override
	public boolean report(DataReport report) {
		if (reportingService == null) {
			logger.error("OsgiSubscriber not intialised");
			return false;
		}
		try {
			return reportingService.reportAcquiredData(report);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean matchHid(String hid) {
		if (hid.equals(this.getSubscriber().getSubscriberHid()))
			return true;
		return false;
	}

}
