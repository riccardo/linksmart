/**
 * Copyright 2004 Inderjeet Singh. All rights reserved. You may not modify, 
 * use, reproduce or distribute this software except in compliance with the 
 * terms of the license at http://tcpmon.dev.java.net/
 * $Id: StreamThread.java,v 1.4 2004/11/14 19:28:55 inder Exp $
 */

package eu.linksmart.network.backbone;

import java.io.ByteArrayOutputStream;
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
public class StreamThread extends Thread {
	/**
	 * configuration parameter: the time to sleep (in Millis) if the
	 * data is not present on the input stream yet.
	 */
	private static final int DATA_ARRIVAL_WAIT_TIME = 50;
	private static Logger logger = Logger.getLogger(StreamThread.class.getName());
	/**
	 * configuration parameter: the size of the data buffer.
	 */
	private static final int BUF_SIZE = 16*1024;
	private static final int MAX_NUM_RETRIES = 15;
	public String name;
	
	/**
	 * Constructor
	 * @param src the input stream
	 * @param d the output stream
	 * @param name the name
	 */
	public StreamThread(InputStream src, OutputStream d, String name) {
		assert src != null;
		assert d != null;
		this.src = src;
		this.dst = d;
		this.name = name;
	}
	
	/**
	 * Constructor
	 * @param src the input stream
	 * @param dst the output stream
	 */
	public StreamThread(InputStream src, OutputStream[] dst) {
		super("stream-");
		assert src != null;
		for (int i = 0; i < dst.length; ++i) 
			assert dst[i] != null;
		this.src = src;
		this.dst = dst[0];
	}
	
	/**
	 * Closes connections
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
			logger.error("EOF in " + name);
		} catch (NoRouteToHostException nrthe) {
			System.err.println("No route to the other end of the tunnel!");
		} catch (IOException ioe) {
			logger.error("IO EXception. " + name + ioe);
		} finally {
			closeConnections();
			System.err.println("Finished " + name);
		}
	}
	
	/**
	 * Copy all the data present in the src to the dst byte by byte
	 * @return an integer
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
	 * @return an integer
	 * @throws IOException
	 */
	private int copyStream() throws IOException {		
		byte buf[] = new byte[BUF_SIZE];
		int bytesRead = 0;
		int sleepTime = 200;
		int total = 0;
		int numRetries = 0;
		long startTime = System.currentTimeMillis();
		
		do {
			if (src.available() == 0) {
				if (numRetries >= MAX_NUM_RETRIES)
					throw new IOException("StreamThread: data not available "
						+ "on the connection");
				try {
					Thread.currentThread().sleep(DATA_ARRIVAL_WAIT_TIME, 0);
				} catch (InterruptedException ie) {
					logger.error(ie);
				}
				++numRetries;
				logger.info("NumRetries: " + numRetries);
			}
			else {
				if (name.equals("JXTA -> Renderer")) {
					System.out.println((System.currentTimeMillis() - startTime) / 1000
						+ "-Bytes available in JXTA: " + src.available());
				}
			}
			
			bytesRead = src.read(buf);
			if (bytesRead > 0) {
				numRetries = 0;
				ByteArrayOutputStream reverseData = new ByteArrayOutputStream();
				long before = System.currentTimeMillis();
				dst.write(buf, 0, bytesRead);
				dst.flush();
				calulateRate(before, System.currentTimeMillis(), bytesRead);
				
				if (name.equals("Server -> JXTA")) {
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				reverseData.write(buf, 0, bytesRead);
				System.out.println(reverseData.toString());
				total += bytesRead;
			}
			else {
				System.err.println("Source not available" + bytesRead);
			}
			
			System.out.println((System.currentTimeMillis() - startTime) / 1000
				+ "-Bytes Read in "+name + ": " + bytesRead);
		} while (bytesRead != -1);
		
		return total;
	}

	/**
	 * Calculate rate
	 * @param before the start time
	 * @param current the current time
	 * @param bytesRead the number of bytes read
	 */
	private void calulateRate(long before, long current, int bytesRead) {
		try {
			long rate = bytesRead / (current - before);
			System.out.println(rate + " bps");
		} catch (Exception e) {}		
	}
	
	private InputStream src;
	private OutputStream dst;
	
}
