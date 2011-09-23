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
import java.io.InputStream;

import org.apache.commons.lang.StringEscapeUtils;

import eu.linksmart.storage.helper.Base64;
import eu.linksmart.storage.helper.ResponseFactory;
import eu.linksmart.storage.helper.StringResponse;

public class LinkSmartSMInputStream extends InputStream {

	private FileSystemDeviceLimboClientPortImpl connection;
	private LinkSmartSMFile myFile;
	private long position;

	public LinkSmartSMInputStream(LinkSmartSMFile file) throws LinkSmartIOException {
		super();
		if (!file.isFile())
			throw new LinkSmartIOException("File " + file.getPath() + " on " + file.getWsAdress() + " does not exists or is no file.");
		connection = file.getConnection();
		myFile = file;
		position = 0;
	}
	
	@Override
	public void close() throws IOException {
		connection = null;
		myFile = null;
		super.close();
	}

	@Override
	public boolean markSupported() {
		return false;
	}

	@Override
	public int read(byte[] arg0) throws IOException {
		String response = connection.readFile(myFile.getParent(), "" + position, "" + arg0.length);
		response = StringEscapeUtils.unescapeXml(response);
		StringResponse r = ResponseFactory.readStringResponse(response);
		if(r.getErrorCode()!=0) {
			close();
			throw new LinkSmartIOException("Error " + r.getErrorCode() + ": " + r.getErrorMessage());
		}
		try {
			byte[] data=Base64.decode(r.getResult());
			if (data.length > arg0.length) {
				throw new LinkSmartIOException("Error: read to much data (" + data.length + " insteaqd of " + arg0.length + ")");
			}
			System.arraycopy(data, 0, arg0, 0, data.length);
			return data.length;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new LinkSmartIOException("Error decoding data", e);
		}
	}

	@Override
	public long skip(long arg0) throws IOException {
		if(position + arg0 > myFile.length()) {
			close();
			throw new LinkSmartIOException("Error: leaving end of file");
		}
		position += arg0;
		return position;
	}

	@Override
	public int read() throws IOException {
		byte[] data = new byte[1];
		read(data);
		return data[0];
	}

}
