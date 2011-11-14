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
 * Copyright (C) 2006-2010
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

package eu.linksmart.security.protocols.net.common;

import java.util.LinkedList;
import java.util.List;

import eu.linksmart.security.protocols.net.transport.Command;
/**
 * 
 * CommandQueue is a FIFO Queue implementation for storing new Commands, which
 * will later be used in the Protocol FSM. There is one CommandQueue for every
 * session in the reference implementation. This is just the basic 
 * consumer/producer "pattern" to avoid busy waiting on the consumer side. 
 * 
 * @author Stephan Heuser - stephan.heuser@sit.fraunhofer.de
 *
 */

public class CommandQueue {
	
	private List<Command> queue = new LinkedList<Command>();
	
	/**
	 * Add a new Command to the queue
	 * @param cmd the to-be-added command
	 */
	
	public synchronized void put(Command cmd) {
		queue.add(cmd);
		notifyAll();
	}
	
	/**
	 * Get the first Command from the Queue
	 * @return the first Command
	 */
	
	public synchronized Command get() {
		if (queue.size() <= 0) {
			try {
				wait();
			} catch (InterruptedException e) {
				//Do nothing
			}
		}
		return queue.remove(0);
	}
}
