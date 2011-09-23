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

package eu.linksmart.limbo.lockmanager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class TCPProtocol implements ServerProtocol {
	
	
	private ServerSocket ssc; 
	private Socket sc; 
	private int port; 
	private InputStream is;
	private String url;
	private OutputStream os;
	private Logger log = Logger.getLogger(TCPProtocol.class.getName());
	private String clientHost;
	
	public TCPProtocol() {}
	public TCPProtocol(int port) {
		this.port=port;
		try {
			this.ssc = new ServerSocket( this.port );
		} catch (IOException e) {
		  e.printStackTrace();
		}
	}
	public void createConnectors(int port) {
		if (this.port != port)
		{
		this.port=port;
	    try {
			ssc = new ServerSocket( this.port );
		} catch (IOException e) {
			e.printStackTrace();
		}
		}
		try {
			this.url = "http://"+InetAddress.getLocalHost().getHostAddress()+":"+this.port;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		sc = null;
		this.clientHost = "";
   	}

	public String getServerURL() {
		return this.url;
	}
	
	public String getClientHost() {
		return this.clientHost;
	}

	public String receive() {
		String request = "";
		try {
			this.sc = this.ssc.accept() ;
			this.is = sc.getInputStream();
			this.os = sc.getOutputStream();
			this.clientHost = sc.getInetAddress().getHostAddress();
			if(this.clientHost.equals("127.0.0.1"))
				clientHost = InetAddress.getLocalHost().getHostAddress();
			log.info("Connection established. Receiving request...");
			int ch = 0;
			int chCount = 0;
			boolean condition = true;
			int content = -1;
        		StringBuffer sb = new StringBuffer();
			StringBuffer auxiliar = new StringBuffer();
			while(condition){
				ch = this.is.read();
				sb.append((char)ch);
				auxiliar.append((char)ch);
				if((char)ch == '\n'){
					String line = auxiliar.toString();
					if(line.toLowerCase().startsWith("content-length")){
						line = line.substring(0,line.length()-2);
						StringTokenizer st = new StringTokenizer(line,": ");
						st.nextToken();
						content = Integer.parseInt(st.nextToken());
					}
					else if(line.equalsIgnoreCase("\r\n"))
						condition = false;
					auxiliar = new StringBuffer();
				}
			}
			if(content != -1) {
				while(chCount<content){
					ch = this.is.read();
					sb.append((char)ch);
					chCount++;
				}
				request = sb.toString();
			}else {
				request = sb.toString();
				byte[] buffer = new byte[1024] ;
				int n ;
				do {
	                    		n = this.is.read(buffer);
	                    		if (n > 0)
	                        		request = request.concat(new String(buffer, 0, n));
	                	} while (n != -1);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return request;
	}
	
	public void send(String result) {
		
		try {
			this.os.write(result.getBytes());
			this.os.flush();
			this.os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void closeConnection() {
		try {
			ssc.close();
			is.close();
			sc.close();
			sc.setReuseAddress(true); 
		    ssc.setReuseAddress(true); 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

}
