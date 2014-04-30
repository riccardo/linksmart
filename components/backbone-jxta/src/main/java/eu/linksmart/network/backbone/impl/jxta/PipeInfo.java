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
 * Copyright (C) 2006-2010 [Telefonica I+D]
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

package eu.linksmart.network.backbone.impl.jxta;

import net.jxta.pipe.OutputPipe;

/**
 * Pipe Info
 */
public class PipeInfo {

	private OutputPipe outputPipe;
	private Long time;

	/**
	 * Constructor. Sets the output pipe. Sets the time to the current time
	 * 
	 * @param outputPipe the output pipe
	 */
	public PipeInfo(OutputPipe outputPipe) {
		this.outputPipe = outputPipe;
		this.time = System.currentTimeMillis();
	}

	/** 
	 * Gets the output pipe
	 * 
	 * @return the output pipe
	 */
	public OutputPipe getOutputPipe() {
		return outputPipe;
	}

	/**
	 * Sets the output pipe
	 * 
	 * @param outputPipe the output pipe
	 */
	public void setOutputPipe(OutputPipe outputPipe) {
		this.outputPipe = outputPipe;
	}
	
	/**
	 * Gets the time
	 * 
	 * @return the time
	 */
	public Long getTime() {
		return time;
	}

	/**
	 * Sets the time
	 * 
	 * @param time the time
	 */
	public void setTime(Long time) {
		this.time = time;
	}

}
