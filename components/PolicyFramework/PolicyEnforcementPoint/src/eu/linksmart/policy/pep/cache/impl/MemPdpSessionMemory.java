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
package eu.linksmart.policy.pep.cache.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import eu.linksmart.policy.pep.cache.PdpSessionMemory;

/**
 * <p>In-memory {@link PdpSessionMemory} implementation</p>
 * 
 * <p>This implementation supports session keep-alive only for entries that do 
 * not specify a lifetime timeout value. If an entry specifies a lifetime 
 * timeout value, keep-alive is disabled for that request.</p>
 * 
 * @author Marco Tiemann
 *
 */
public class MemPdpSessionMemory implements PdpSessionMemory {

	/** logger */
	private static final Logger logger 
			= Logger.getLogger(MemPdpSessionMemory.class); 
	
	/** session item memory */
	List<PdpSessionItem> sessionItems
			= Collections.synchronizedList(new ArrayList<PdpSessionItem>());
	
	/** comparator for sorting <code>sessionItems</code> */
	Comparator<PdpSessionItem> comparator = null;
	
	/** 
	 * flag to determine whether to sort by time (if TRUE) or by session ID 
	 * (if FALSE) 
	 */
	boolean sortByTime = false;
	
	/** session lifetime */
	long sessionLifetime = 100000000L;
	
	/** 
	 * flag to determine whether sessions are extended when new matching calls 
	 * are added to the memory
	 */
	private boolean keepAlive = true;
	
	/** flag to determine whether to run an background memory cleaning thread */
	private boolean useCleaner = false;
	
	/** cleaner {@link Timer} */
	private Timer cleanerTimer = new Timer();
	
	/** cleaner interval */
	private long cleanerInterval = 100000000L;
	
	/** No-args constructor */
	public MemPdpSessionMemory() {
		super();
		comparator = (sortByTime) ? new TimestampComparator()
				: new SessionIdComparator();
		cleanerTimer.scheduleAtFixedRate(new Cleaner(), cleanerInterval, 
				cleanerInterval);
	}
	
	/* (non-Javadoc)
	 * @see eu.linksmart.policy.pep.cache.PdpSessionMemory#getLifetime()
	 */
	@Override
	public long getLifetime() {
		return sessionLifetime;
	}

	/* (non-Javadoc)
	 * @see eu.linksmart.policy.pep.cache.PdpSessionMemory#setLifetime(long)
	 */
	@Override
	public void setLifetime(long theLifetime) {
		sessionLifetime = theLifetime;
		sessionItems.clear();
	}	

	/* (non-Javadoc)
	 * @see eu.linksmart.policy.pep.cache.PdpSessionMemory#setLifetime(
	 * 		java.lang.String)
	 */
	@Override
	public void setLifetime(String theSessionLifetime) {
		try {
			setLifetime(convertStringToTimestamp(theSessionLifetime));
		} catch (NumberFormatException nfe) {
			logger.warn("Unparseable time value, could not set session " 
					+ "lifetime value to " + theSessionLifetime);
		}
	}

	/* (non-Javadoc)
	 * @see eu.linksmart.policy.pep.cache.PdpSessionMemory#getKeepAlive()
	 */
	@Override
	public boolean getKeepAlive() {
		return keepAlive;
	}

	/* (non-Javadoc)
	 * @see eu.linksmart.policy.pep.cache.PdpSessionMemory#setKeepAlive(boolean)
	 */
	@Override
	public void setKeepAlive(boolean theKeepCallsAlive) {
		keepAlive = theKeepCallsAlive;
		if (!keepAlive) {
			sessionItems.clear();
		}
	}
	
	/**
	 * @param theSortByTime
	 * 				a flag inidicting whether to sort cached sessions by 
	 * 				time (for <code>true</code>) or session ID (for 
	 * 				<code>false</code>)
	 */
	public void setSortByTime(boolean theSortByTime) {
		sortByTime = theSortByTime;
		comparator = (sortByTime) ? new TimestampComparator()
				: new SessionIdComparator();
		Collections.sort(sessionItems, comparator); 
	}

	/**
	 * @return
	 * 				a flag determining whether a background cleaner process is 
	 * 				used
	 */
	public boolean getUseCleaner() {
		return useCleaner;
	}
	
	/**
	 * @param theUseCleaner
	 * 				a flag to determine whether a background cleaner process is 
	 * 				to be used
	 */
	public void setUseCleaner(boolean theUseCleaner) {
		useCleaner = theUseCleaner;
	}
	
	/**
	 * Restarts cleaner timer with the argument cleaner timer interval
	 * 
	 * @param theInterval
	 * 				the cleaner timer interval
	 */
	public void setCleanerInterval(long theInterval) {
		cleanerInterval = theInterval;
		cleanerTimer.cancel();
		cleanerTimer.purge();
		cleanerTimer = new Timer();
		cleanerTimer.scheduleAtFixedRate(new Cleaner(), cleanerInterval, 
				cleanerInterval);
	}
	
	/* (non-Javadoc)
	 * @see eu.linksmart.policy.pep.cache.PdpSessionMemory#add(
	 * 		eu.linksmart.policy.pep.cache.impl.PdpSessionItem)
	 */
	@Override
	public void add(PdpSessionItem theNtry) {
		if (sortByTime) {
			if ((theNtry.getSessionId() != null) 
					&& (theNtry.getParameters() != null)) {
				int sis = sessionItems.size();
				for (int j=0; j < sis; j++) {
					PdpSessionItem ntry = sessionItems.get(j);				
					if ((ntry.getSessionId() != null) 
							&& (ntry.getSessionId().equals(
									theNtry.getSessionId()))
							&& (ntry.getParameters() != null)
							&& (ntry.getParameters().equals(
									theNtry.getParameters()))) {
						sessionItems.set(j, ntry);
						return;
					}
				}
				int i = Collections.binarySearch(sessionItems, theNtry,
						comparator);
				sessionItems.add((-i - 1), theNtry);
			}
		} else {
			int i = Collections.binarySearch(sessionItems, theNtry, comparator);
			if (i >= 0) {
				// there already exists an entry for this session ID/parameter
				sessionItems.set(i, theNtry);
			} else {
				sessionItems.add((-i - 1), theNtry);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see eu.linksmart.policy.pep.cache.PdpSessionMemory#find(
	 * 		java.lang.String, java.lang.String, long)
	 */
	@Override
	public PdpSessionItem find(String theSessionId, String theParameters, 
			long theTimestamp) {
		if ((theSessionId == null) || (theParameters == null)) {
			return null;
		}
		int i = -1;
		if (sortByTime) {
			for (int j=0; j < sessionItems.size(); j++) {
				PdpSessionItem ntry = sessionItems.get(j);				
				if ((ntry.getSessionId() != null) 
						&& (ntry.getSessionId().equals(theSessionId))
						&& (ntry.getParameters() != null)
						&& (ntry.getParameters().equals(theParameters))) {
					i = j;
					break;
				}
			}			
		} else {
			PdpSessionItem lookupNtry = new PdpSessionItem(theSessionId, 
					theParameters);
			i = Collections.binarySearch(sessionItems, lookupNtry, comparator);
		}
		if (i >= 0) {
			PdpSessionItem ntry = sessionItems.get(i);
			if (checkTimestamp(ntry)) {
				// only keep alive if no timeout is set 
				if ((keepAlive) && (sessionItems.get(i).getTimeout() == null)) {
					sessionItems.get(i).setTimestamp(new Long(theTimestamp));
					if (sortByTime) {
						// move item to the new appropriate position
						PdpSessionItem rentry = sessionItems.remove(i);
						int k = Collections.binarySearch(sessionItems, rentry, 
								comparator);
						sessionItems.add(-k - 1, rentry);
					}
				}
				return ntry;
			} else if (sortByTime) {
				for (int j=0; j <= i; j++) {
					sessionItems.remove(0);
				}
			} else {
				sessionItems.remove(i);
			}			
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see eu.linksmart.policy.pep.PdpSessionMemory#flush()
	 */
	@Override
	public void flush() {
		sessionItems.clear();
	}	
	
	/**
	 * Checks whether timestamp is within session lifetime range
	 * 
	 * @param theNtry
	 * 				{@link PdpSessionItem}
	 * @return
	 * 				TRUE if timestamp is within session lifetime range,
	 * 				FALSE if not (modified by timeout value if set)
	 */
	private boolean checkTimestamp(PdpSessionItem theNtry) {
		long life = System.currentTimeMillis() 
				- theNtry.getTimestamp().longValue();
		if (theNtry.getTimeout() != null) {
			long timeout = theNtry.getTimeout().longValue();
			return (life <= timeout) ? true : false;
		}
		return (life <= sessionLifetime) ? true : false;
	}
	
	/**
	 * Converts time value with unit qualifier to millisecond time value
	 * 
	 * @param theTime
	 * 				the <code>String</code> time value
	 * @return
	 * 				the <code>long</code> millisecond time value
	 * @throws NumberFormatException
	 * 				if <code>theTime</code> cannot be converted
	 */
	private long convertStringToTimestamp(String theTime) 
			throws NumberFormatException {
		String strTime = theTime.toLowerCase();
		if (strTime.endsWith("d")) {
			return Long.parseLong(strTime.substring(0, strTime.length() - 1))
					* 24 * 60 * 60 * 1000;
		} else if (strTime.endsWith("h")) {
			return Long.parseLong(strTime.substring(0, strTime.length() - 1))
					* 60 * 60 * 1000;
		} else if (strTime.endsWith("m")) {
			return Long.parseLong(strTime.substring(0, strTime.length() - 1))
					* 60 * 1000;
		} else if (strTime.endsWith("ms")) {
			return Long.parseLong(strTime.substring(0, strTime.length() - 2));
		} else if (strTime.endsWith("s")) {
			return Long.parseLong(strTime.substring(0, strTime.length() - 1))
					* 1000;
		} else {
			return Long.parseLong(strTime);
		}
	}
	
	
	/**
	 * <p>{@link Comparator} implementation that compares {@link PdpSessionItem}s 
	 * in order of session ID and parameters</p>
	 * 
	 * @author Marco Tiemann
	 *
	 */
	final class SessionIdComparator 
			implements Comparator<PdpSessionItem> {

		/* (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(PdpSessionItem theSub, PdpSessionItem theRef) {
			if (theSub == null) {
				return (theRef == null) ? 0 : -1;
			}
			if (theRef == null) {
				return 1;
			}
			if (theSub.getSessionId() == null) {
				return (theRef.getSessionId() == null) ? 0 : -1;
			}
			if (theRef.getSessionId() == null) {
				return (theRef.getSessionId() == null) 
						? (theRef.getParameters() == null) 
						? (theRef.getParameters() == null) 
						? 0 : -1 : (theRef.getParameters() == null) 
						? 1 : theRef.getParameters().compareTo(
								theRef.getParameters()) : -1;
			}
			int r = theSub.getSessionId().compareTo(theRef.getSessionId());
			if (r != 0) {
				return r;
			}
			return (theSub.getParameters() == null) 
					? (theRef.getParameters() == null) 
							? 0 : -1 : (theRef.getParameters() == null) 
									? 1 : theSub.getParameters().compareTo(
													theRef.getParameters());
		}
		
	}
	

	/**
	 * <p>{@link Comparator} implementation that compares {@link PdpSessionItem}s 
	 * in order of timestamp, session ID and parameters</p>
	 * 
	 * @author Marco Tiemann
	 *
	 */
	final class TimestampComparator
			implements Comparator<PdpSessionItem> {
		
		/** session comparator */
		private Comparator<PdpSessionItem> sessionComparator
				= new SessionIdComparator();

		/* (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(PdpSessionItem theSub, PdpSessionItem theRef) {
			if (theSub == null) {
				return (theRef == null) ? 0 : -1;
			}
			if (theRef == null) {
				return 1;
			}
			if (theSub.getTimestamp() == null) {
				return (theRef.getTimestamp() == null) ? 0 
						: sessionComparator.compare(theSub, theRef);
			}
			if (theRef.getTimestamp() == null) {
				return sessionComparator.compare(theSub, theRef);
			}		
			long subTime = theSub.getTimestamp().longValue();
			if (theSub.getTimeout() != null) {
				long subOut = theSub.getTimeout().longValue();
				subTime += subOut - sessionLifetime;
			}
			long refTime = theRef.getTimestamp().longValue();
			if (theRef.getTimeout() != null) {
				long refOut = theRef.getTimeout().longValue();
				refTime += refOut - sessionLifetime;
			}
			long d = subTime - refTime;
			return (d < 0) ? -1 : (d > 0) ? 1
					: sessionComparator.compare(theSub, theRef);
		}
		
	}
	
	/**
	 * <p><code>sessionItems</code> cleaner {@link TimerTask}</p>
	 * 
	 * @author Marco Tiemann
	 *
	 */
	final class Cleaner extends TimerTask {		

		/* (non-Javadoc)
		 * @see java.util.TimerTask#run()
		 */
		@Override
		public synchronized void run() {
			List<PdpSessionItem> cleanedSessionItems = Collections
					.synchronizedList(new ArrayList<PdpSessionItem>());
			if (sortByTime) {
				PdpSessionItem ntry = new PdpSessionItem(new Long
						(System.currentTimeMillis() - sessionLifetime));
				int i = Collections.binarySearch(sessionItems, ntry, 
						comparator);
				if (i > 0) {
					int sis = sessionItems.size();
					for (int j=i; j < sis; j++) {
						cleanedSessionItems.add(sessionItems.get(j));
					}
				} else {
					cleanedSessionItems.addAll(sessionItems);
				}
			} else {
				for (PdpSessionItem ntry : sessionItems) {
					if (!hasExpired(ntry)) {
						cleanedSessionItems.add(ntry);
					}
				}
			}
			sessionItems = cleanedSessionItems;
		}
		
		/**
		 * Checks whether a session lifetime has expired
		 * 
		 * @param theNtry
		 * 					the {@link PdpSessionItem}
		 * @return
		 * 					TRUE if <code>theNtry</code> has expired,
		 * 					FALSE if not
		 */
		private boolean hasExpired(PdpSessionItem theNtry) {
			long life = System.currentTimeMillis() 
					- theNtry.getTimestamp().longValue();
			if (theNtry.getTimeout() != null) {
				long timeout = theNtry.getTimeout().longValue();
				return (life > timeout) ? true : false;
			}
			if (life > sessionLifetime) {
				return true;
			}
			return false;
		}
		
	}
	
}
