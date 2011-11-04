package eu.linksmart.network.session;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;

public class SessionTest {
	static final String SESSION_ID = "TESTID";
	static final String CLIENT_HID = "0.0.0.31546531";
	static final String SERVER_HID = "0.0.0.235446851";
	static final long SESSION_DELAY = 1000;
	static final String SESSION_DATA_PATH = System.getProperty("java.io.tmpdir") + "SessionDataPath\\";
	
	@Test
	public void testConstructor() {
		Session s = new Session(SESSION_ID, CLIENT_HID, SERVER_HID, SESSION_DELAY, SESSION_DATA_PATH);
		long currentTime = System.currentTimeMillis();
		assertEquals(SESSION_ID, s.getSessionID());
		assertEquals(CLIENT_HID, s.getClientHID());
		assertEquals(SERVER_HID, s.getServerHID());
		assertTrue(s.getExpirationTime() > currentTime);
		assertTrue(Math.abs(currentTime + SESSION_DELAY - s.getExpirationTime()) < 100);
	}
	
	/*@Test
	public void testSaveSession() throws IOException {
		Session s = new Session(SESSION_ID, CLIENT_HID, SERVER_HID, SESSION_DELAY, SESSION_DATA_PATH);
		s.saveSession();
		FileInputStream file = new FileInputStream(SESSION_DATA_PATH + "session" + SESSION_ID + ".ser");
		assertTrue(file.available() > 0);
		file.close();
	}*/
}
