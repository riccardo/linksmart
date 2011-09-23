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
package eu.linksmart.caf.cm.managers;

import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.util.tracker.ServiceTracker;


import eu.linksmart.caf.cm.ContextManager;
import eu.linksmart.caf.cm.event.IEventHandler;
import eu.linksmart.caf.cm.impl.CmManagerHub;
import eu.linksmart.eventmanager.EventManagerPort;
import eu.linksmart.eventmanager.Part;

/**
 * {@link CmInternalManager} manager that provides an extensible mechanism for
 * adding additional event handling functionality in the {@link ContextManager}.
 * <p> Automatically finds and registers any {@link IEventHandler}s in the OSGi
 * framework, subscribing to the events they require, and forwarding to
 * occurrence of the events on to them.
 * 
 * @author Michael Crouch
 * 
 */
public class ContextEventManager extends CmInternalManager {

	/** The Id for this Internal Manager */
	public static final String MANAGER_ID =
			"eu.linksmart.caf.cm.ContextEventManager";

	/** the {@link ComponentContext} */
	private final ComponentContext context;

	/** the {@link ServiceTracker} */
	private ServiceTracker tracker;

	/** the {@link Set} of {@link IEventHandler}s */
	private Set<IEventHandler> handlers;

	/**
	 * Constructor
	 * 
	 * @param context
	 *            the {@link ComponentContext}
	 */
	public ContextEventManager(ComponentContext context) {
		this.context = context;
		handlers = new HashSet<IEventHandler>();
	}

	/**
	 * Initialises the manager, setting up the listener
	 */
	private void init() {
		tracker =
				new ServiceTracker(context.getBundleContext(),
						IEventHandler.class.getName(), null);
		tracker.open();
		ServiceReference[] refs = tracker.getServiceReferences();
		if (refs != null) {

			for (int i = 0; i < refs.length; i++) {
				IEventHandler handler =
						(IEventHandler) context.getBundleContext().getService(
								refs[i]);
				this.addHandler(handler);

			}
		}
		ServiceListener sl = new ServiceListener() {

			@Override
			public void serviceChanged(ServiceEvent theEvent) {
				switch (theEvent.getType()) {
					case ServiceEvent.REGISTERED: {
						IEventHandler handler =
								(IEventHandler) context.getBundleContext()
										.getService(
												theEvent.getServiceReference());
						addHandler(handler);
						break;
					}
					case ServiceEvent.MODIFIED:
						return;

					case ServiceEvent.UNREGISTERING: {
						IEventHandler handler =
								(IEventHandler) context.getBundleContext()
										.getService(
												theEvent.getServiceReference());
						removeHandler(handler);
						return;
					}
					default:
						return;
				}
			}
		};
		try {
			String filter =
					"(objectclass=" + IEventHandler.class.getName() + ")";
			context.getBundleContext().addServiceListener(sl, filter);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getManagerId() {
		return MANAGER_ID;
	}

	@Override
	public void initialise(CmManagerHub hub) {
		//Do nothing
	}

	@Override
	public void completedInit() {
		init();	
	}
	
	@Override
	public void shutdown() {
		// none
	}

	/**
	 * Handles the given Event, by forwarding to the appropriate
	 * {@link IEventHandler}(s). Returns false, if none could be found matching
	 * the topic.
	 * 
	 * @param topic
	 *            the topic of the Event
	 * @param parts
	 *            the event {@link Part}s
	 * @return whether it was handled
	 */
	public boolean handleEvent(String topic, Part[] parts) {
		Iterator<IEventHandler> it = handlers.iterator();
		boolean handled = false;
		while (it.hasNext()) {
			IEventHandler handler = it.next();
			if (handler.canHandleEvent(topic)) {
				new HandleEventThread(handler, topic, parts).run();
				handled = true;
			}
		}
		return handled;
	}

	/**
	 * Adds and registers a {@link IEventHandler}
	 * 
	 * @param handler
	 *            the {@link IEventHandler}
	 */
	public void addHandler(IEventHandler handler) {
		if (handlers.contains(handler))
			return;

		CmManagerHub hub = this.getManagerHub();
		handler.register(hub);
		handlers.add(handler);

		// subscribe to the handled event topics
		EventManagerPort em = hub.getCmApp().getEventManager();
		String cmHid = hub.getCmApp().getHid();
		if (em != null) {
			Iterator<String> it = handler.getHandledTopics().iterator();
			while (it.hasNext()) {
				try {
					em.subscribeWithHID(it.next(), cmHid);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Unregisters and removes the {@link IEventHandler}
	 * 
	 * @param handler
	 *            the {@link IEventHandler}
	 */
	public void removeHandler(IEventHandler handler) {
		handler.unregistering();
		handlers.remove(handler);
	}

	/**
	 * Simple {@link Thread} for handling Events
	 * 
	 * @author Michael Crouch
	 * 
	 */
	public class HandleEventThread extends Thread {

		/** the {@link IEventHandler} */
		private IEventHandler handler;

		/** the event topic */
		private String topic;

		/** the event {@link Part}s */
		private Part[] parts;

		/**
		 * Constructor
		 * 
		 * @param handler
		 *            the {@link IEventHandler}
		 * @param topic
		 *            the topic
		 * @param parts
		 *            the {@link Part}s
		 */
		public HandleEventThread(IEventHandler handler, String topic,
				Part[] parts) {
			this.handler = handler;
			this.topic = topic;
			this.parts = parts.clone();
		}

		@Override
		public void run() {
			handler.handleEvent(topic, parts);
		}
	}
}
