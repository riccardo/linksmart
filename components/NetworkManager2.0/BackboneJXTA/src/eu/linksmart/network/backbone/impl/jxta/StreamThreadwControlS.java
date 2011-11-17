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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import net.jxta.document.AdvertisementFactory;
import net.jxta.endpoint.Message;
import net.jxta.endpoint.MessageElement;
import net.jxta.endpoint.StringMessageElement;
import net.jxta.id.IDFactory;
import net.jxta.peer.PeerID;
import net.jxta.pipe.OutputPipe;
import net.jxta.pipe.PipeID;
import net.jxta.pipe.PipeService;
import net.jxta.protocol.PipeAdvertisement;

import org.apache.log4j.Logger;

/**
 * This class downloads data from a stream in a separate thread.
 * @author Inderjeet Singh
 */
public class StreamThreadwControlS extends Thread {
	/**
	 * configuration parameter: the time to sleep (in Millis) if the
	 * data is not present on the input stream yet.
	 */
	private static final int DATA_ARRIVAL_WAIT_TIME = 50;
	private static Logger logger = Logger.getLogger(StreamThreadwControlS.class.getName());
	/**
	 * configuration parameter: the size of the data buffer.
	 */
	private static final int BUF_SIZE = 16*1024;
	private static final int MAX_NUM_RETRIES = 15;
	private OutputPipe outputPipe;
	public String name;
	private int cont = 1;
	private long bitRate = 0;
	private long NotBitRate = 0;
	private Long id;
	private SocketHandler socketHandler;
	
	/**
	 * Constructor
	 * @param src the source
	 * @param d the destination
	 * @param name the name
	 * @param pID teh peer ID
	 * @param id the ID
	 * @param socketHandler the socker handler
	 */
	public StreamThreadwControlS(InputStream src, OutputStream d, String name,
			PeerID pID, Long id, SocketHandler socketHandler) {

		this.socketHandler = socketHandler;
		assert src != null;
		assert d != null;
		this.src = src;
		this.dst = d;
		this.name = name;
		this.id = id;
		outputPipe = createOutputPipe(pID);
	}
	
	/**
	 * Constructor
	 * @param src the source
	 * @param dst the destination
	 */
	public StreamThreadwControlS(InputStream src, OutputStream[] dst) {
		super("stream-");
		assert src != null;
		for (int i = 0; i < dst.length; ++i) { 
			assert dst[i] != null;
		}
		this.src = src;
		this.dst = dst[0];
	}
	
	/**
	 * Closes all connections
	 */
	public void closeConnections() {
		try {
			outputPipe.close();
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
		long startTime = System.currentTimeMillis();
		
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
				long before = System.currentTimeMillis();
				dst.write(buf, 0, bytesRead);  
				dst.flush();
				calulateRate(before, System.currentTimeMillis(), bytesRead);
				total += bytesRead;				
			}
			else {
				System.err.println("Source not available" + bytesRead);
			}
			System.out.println((System.currentTimeMillis() - startTime)/1000 + "-Bytes Read in "+name + ": " + bytesRead);
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
		if (current -before != 0) {
			long rate = bytesRead / (current - before);
			if (cont < 10) {
				bitRate += rate;
				cont += 1;
			}
			else if (cont == 10) {
				bitRate += rate;
				bitRate = bitRate / 10;
				System.out.println(bitRate + " Kbps");
				cont = 1;
				if ((bitRate > NotBitRate * 1.5) || (bitRate < NotBitRate / 1.5)) {
					NotBitRate = bitRate;
					System.out.println("Sending new Rate = " + NotBitRate);
					
					new Thread(new Runnable() {
						public void run() {
							try {
								outputPipe.send(createMessage(NotBitRate, id));
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}).start();
				}
				bitRate = 0;
			}
		}		
	}
	
	/**
	 * Creates a message
	 * @param bitRate2 the bitrate
	 * @param id the ID
	 * @return the message
	 */
	private Message createMessage(long bitRate2, long id) {
		Message msg = new Message();
		MessageElement elem = new StringMessageElement("BitRate",
			String.valueOf(bitRate2), null);
		msg.addMessageElement(elem);
		elem = new StringMessageElement("ID", String.valueOf(id), null);
		msg.addMessageElement(elem);
		return msg;
	}
	
	private InputStream src;
	private OutputStream dst;
	
	/**
	 * Create a pipe advertisement
	 * @return the pipe advertisement
	 */
	private PipeAdvertisement createPipeAdv() {		
		PipeID pipeID = null;
		PipeAdvertisement pipeAdv = (PipeAdvertisement)
			AdvertisementFactory.newAdvertisement(PipeAdvertisement.getAdvertisementType());
		String ppid =
			"urn:jxta:uuid-59616261646162614E5047205032503342C3E540E0904149B8C29F1C9CB9424504";
		
		try {
			pipeID = (PipeID) IDFactory.fromURI(new URI(ppid));
		} catch (URISyntaxException e) {
			logger.error("Imposible to create PipeID for the pipe advertisement");
		}
		
		pipeAdv.setPipeID(pipeID);
		pipeAdv.setName("Input Pipe Advertisement for streaming");
		pipeAdv.setType(PipeService.UnicastType);
		logger.debug("Pipe for Streamming control created: " + pipeID);
		return pipeAdv;
	}
	
	/**
	 * Creates an output pipe
	 * @param pID the peer ID
	 * @return the output pipe
	 */
	private OutputPipe createOutputPipe(PeerID pID) {
		OutputPipe outputPipe = null;
		PipeAdvertisement pipeAdv = createPipeAdv();
		Set<PeerID> peersID = new HashSet<PeerID>();
		peersID.add(pID);
		
		try {
			outputPipe = socketHandler.bbjxta.netPeerGroup.
				getPipeService().createOutputPipe(pipeAdv, peersID, 0);
		} catch (IOException e) {
			logger.error("Error when creating pipe: Timeout");
		} catch (NullPointerException e) {
			logger.error("Error updating pipeTable: peerID or outputPipe don't exist");
			e.printStackTrace();
		}

		return outputPipe;
	}	

}
