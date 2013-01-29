/**
 * Copyright 2004 Inderjeet Singh. All rights reserved. You may not modify, 
 * use, reproduce or distribute this software except in compliance with the 
 * terms of the license at http://tcpmon.dev.java.net/
 * $Id: StreamThread.java,v 1.4 2004/11/14 19:28:55 inder Exp $
 */

package eu.linksmart.network.backbone.impl.jxta;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.NoRouteToHostException;

import org.apache.log4j.Logger;

/**
 * This class downloads data from a stream in a separate thread.
 * @author Inderjeet Singh
 */
public class StreamThreadwControlR extends Thread {
	/**
	 * configuration parameter: the time to sleep (in Millis) if the
	 * data is not present on the input stream yet.
	 */
	private static final int DATA_ARRIVAL_WAIT_TIME = 50;
	private static Logger logger = Logger.getLogger(StreamThreadwControlR.class.getName());
	/**
	 * configuration parameter: the size of the data buffer.
	 */
	private static final int BUF_SIZE = 16*1024;
	private static final int MAX_NUM_RETRIES = 15;
	public String name;
	private Long id;
	private long sleepTime;
	SocketHandler socketHandler;
	
	/**
	 * Constructor
	 * @param src the source
	 * @param d the destination
	 * @param name the name
	 * @param id the id
	 * @param socketHandler the handler
	 */
	public StreamThreadwControlR(InputStream src, OutputStream d, String name, 
			Long id, SocketHandler socketHandler) {
		this.socketHandler = socketHandler;
		assert src != null;
		assert d != null;
		this.src = src;
		this.dst = d;
		this.name = name;
		this.id = id;
	}
	
	/**
	 * Constructor
	 * @param src the source
	 * @param dst the destination
	 */
	public StreamThreadwControlR(InputStream src, OutputStream[] dst) {
		super("stream-");
		assert src != null;
		for (int i = 0; i < dst.length; ++i) 
			assert dst[i] != null;
		this.src = src;
		this.dst = dst[0];
	}
	
	/**
	 * Closes all connections
	 */
	public void closeConnections() {
		try {
			src.close();
		} catch (Exception e) {
			logger.error(e);
		}

		try {
			dst.close();
		} catch (Exception e) {
			logger.error(e);
		}
	}
	
	/**
	 * Runs
	 */
	public void run() {
		try {
			setName(name);
			System.err.println("Sending in " + name);
			int count = copyStream();			
		} catch (EOFException eofe) {
			logger.error("EOF in " +name);
		} catch (NoRouteToHostException nrthe) {
			System.err.println("No route to the other end of the tunnel!");
		} catch (IOException ioe) {
			logger.error("IO EXception. " +name + ioe);
		} finally {
			closeConnections();
			System.err.println("Finished " + name);
		}
	}
	
	/**
	 * Copy stream byte by byte
	 * @return the number of bytes read
	 * @throws IOException
	 */
	private int copyStreamByteByByte() throws IOException {
		int bytesRead = 0;
		int tmp = 0;
		while ((tmp = src.read()) != -1) {
			++bytesRead;
			dst.write((char) tmp);
		}
		return bytesRead;
	}
	
	/**
	 * Copy all the data present in the src to the dst.
	 * @return the number of bytes read
	 * @throws IOException
	 */
	private int copyStream() throws IOException {
		byte buf[] = new byte[BUF_SIZE];
		int bytesRead = 0;
		int total = 0;
		int numRetries = 0;
		Long bitRate = new Long(0);
		
		do {
			if (src.available() == 0) {
				if (numRetries >= MAX_NUM_RETRIES) {
					throw new IOException("StreamThread: data not available "
						+ "on the connection");
				}

				try {
					Thread.currentThread().sleep(DATA_ARRIVAL_WAIT_TIME, 0);
				} catch (InterruptedException ie) {
					logger.error(ie);
				}
				
				++numRetries;
				logger.info("NumRetries: " + numRetries);
			}

			bytesRead = src.read(buf);
			if (bytesRead > 0) {
				numRetries = 0;
					dst.write(buf, 0, bytesRead);  
					dst.flush();	
					if (name.equals("Server -> JXTA")) {
						try {
							bitRate = socketHandler.bitRates.get(id);
							sleepTime = (long) (BUF_SIZE / bitRate *0.95); 
							System.out.println(id + " " + bitRate + " " + sleepTime);
							Thread.sleep(sleepTime);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				total += bytesRead;
			}
			else {
				System.err.println("Source not available" + bytesRead);
			}
		} while (bytesRead != -1);
		
		return total;
	}
	
	private InputStream src;
	private OutputStream dst;

}
