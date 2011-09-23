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
 * Copyright (C) 2006-2010 [University of Paderborn]
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

package eu.linksmart.limbo.filesystemdevice.client;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.lang.StringEscapeUtils;

import eu.linksmart.storage.helper.Base64;
import eu.linksmart.storage.helper.BooleanResponse;
import eu.linksmart.storage.helper.ResponseFactory;
import eu.linksmart.storage.helper.VoidResponse;

public class LinkSmartSMOutputStream extends OutputStream {

	private FileSystemDeviceLimboClientPortImpl connection;
	private LinkSmartSMFile myFile;
	private long position;
	
	public LinkSmartSMOutputStream(LinkSmartSMFile file) throws LinkSmartIOException {
		super();
		if (file.isDirectory())
			throw new LinkSmartIOException("File " + file.getPath() + " on " + file.getWsAdress() + " does exists and is no file.");
		connection = file.getConnection();
		myFile = file;
		position = 0;
		if (file.exists()) {
			String response = connection.clearFile(file.getPath());
			response = StringEscapeUtils.unescapeXml(response);
			VoidResponse r = ResponseFactory.readVoidResponse(response);
			if (r.getErrorCode()!=0) {
				throw new LinkSmartIOException("Error " + r.getErrorCode() + ": " + r.getErrorMessage());
			}
		} else {
			if (!file.createNewFile())
				throw new LinkSmartIOException("Error: could not create File " + file.getPath() + " on Server " + file.getWsAdress());
		}
	}
	
	@Override
	public void close() {
		connection = null;
		myFile = null;
	}
	
	@Override
	public void write(byte[] arg0) throws IOException {
		// TODO Auto-generated method stub
		String dataSend = Base64.encode(arg0);
		String response = connection.writeFile(myFile.getPath(), "" + position, dataSend);
		response = StringEscapeUtils.unescapeXml(response);
		BooleanResponse r = ResponseFactory.readBooleanResponse(response);
		if (r.getErrorCode()!=0) {
			close();
			throw new LinkSmartIOException("Error " + r.getErrorCode() + ": " + r.getErrorMessage());
		}
		if (!r.getResult()) {
			close();
			throw new LinkSmartIOException("Error: write File responded false");
		}
	}



	@Override
	public void write(int arg0) throws IOException {
		// TODO Auto-generated method stub
		byte[] data = new byte[1];
		data[0] = (byte) arg0;
		write(data);
	}

}
