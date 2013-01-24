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
package eu.linksmart.types;

import static org.junit.Assert.*;
import eu.linksmart.network.identity.HIDManagerApplication;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

// Referenced classes of package eu.linksmart.types:
//            HID

@RunWith(value=JMock.class)
public class HIDTest {
	
	Mockery context;
	
    public HIDTest() {
        context = new JUnit4Mockery() {{
        	setImposteriser(ClassImposteriser.INSTANCE);        
        }};
    }

    @Test
    public void constructorWrongCase() {
        HID hid = new HID("blablabla");
        assertEquals(hid.getContextID1(), 0L);
        assertEquals(hid.getContextID2(), 0L);
        assertEquals(hid.getContextID3(), 0L);
        assertEquals(hid.getDeviceID(), 0L);
        assertEquals(hid.getLevel(), 0L);
    }

    @Test
    public void constructorStringCase() {
        HID hid = new HID("3.2.1.0");
        assertEquals(hid.getContextID1(), 1L);
        assertEquals(hid.getContextID2(), 2L);
        assertEquals(hid.getContextID3(), 3L);
        assertEquals(hid.getDeviceID(), 0L);
        assertEquals(hid.getLevel(), 3L);
    }

    @Test
    public void constructorHIDManagerCase() {
        final HIDManagerApplication appManager = context.mock(HIDManagerApplication.class, "eu.linksmart.network.identity.HIDManagerApplication");
        context.checking(new Expectations() {{
                ((HIDManagerApplication)one(appManager)).existsDeviceID(with(any(Long.TYPE)));
                will(returnValue(Boolean.valueOf(false)));
        }});
        HID hid = new HID(appManager);
        assertEquals(hid.getContextID1(), 0L);
        assertEquals(hid.getContextID2(), 0L);
        assertEquals(hid.getContextID3(), 0L);
        assertTrue(hid.getDeviceID() > 0L);
        assertEquals(hid.getLevel(), 0L);
        context.assertIsSatisfied();
    }
    
    @Test
    public void constructorWithHID() {
    	HID hid = new HID("3.2.1.0");
    	HID newHid = new HID(hid);
    	assertEquals(hid, newHid);
    }
    
    @Test
    public void constructorPartlyValidHID() {
    	HID hid = new HID("0.1.-2.3.-4.5.-6");
        assertEquals(hid.getDeviceID(), 3L);
    	assertEquals(hid.getContextID1(), -2L);
        assertEquals(hid.getContextID2(), 1L);
        assertEquals(hid.getContextID3(), 0L);
        assertEquals(hid.getLevel(), 2L);
    }
    
    @Test
    public void constructorInvalidHID() {
    	HID hid = new HID("0.1..3");
        assertEquals(hid.getDeviceID(), 0L);
    	assertEquals(hid.getContextID1(), 0L);
        assertEquals(hid.getContextID2(), 0L);
        assertEquals(hid.getContextID3(), 0L);
        assertEquals(hid.getLevel(), 0L);
    }
    
    @Test
    public void constructorShortHID() {
    	HID hid = new HID("0.1");
        assertEquals(hid.getDeviceID(), 0L);
    	assertEquals(hid.getContextID1(), 0L);
        assertEquals(hid.getContextID2(), 0L);
        assertEquals(hid.getContextID3(), 0L);
        assertEquals(hid.getLevel(), 0L);
    }
    
    @Test
    public void constructorHIDManagerCaseWith2Attempts() {
        final HIDManagerApplication appManager = context.mock(HIDManagerApplication.class, "eu.linksmart.network.identity.HIDManagerApplication");
        context.checking(new Expectations() {{
                ((HIDManagerApplication)one(appManager)).existsDeviceID(with(any(Long.TYPE)));
                will(returnValue(Boolean.valueOf(true)));
                ((HIDManagerApplication)one(appManager)).existsDeviceID(with(any(Long.TYPE)));
                will(returnValue(Boolean.valueOf(false)));
        }});
        HID hid = new HID(appManager);
        assertEquals(hid.getContextID1(), 0L);
        assertEquals(hid.getContextID2(), 0L);
        assertEquals(hid.getContextID3(), 0L);
        assertTrue(hid.getDeviceID() > 0L);
        assertEquals(hid.getLevel(), 0L);
        context.assertIsSatisfied();
    }
    
    @Test
    public void testEquality(){
    	HID hid = new HID("3.2.1.0");
    	HID newHid = new HID(hid.toString());
    	assertEquals(hid, newHid);
    }
    
    @Test
    public void testLevelCorrect() {
    	HID hid = new HID("3.2.1.0");
    	assertEquals(3, hid.getLevel());
    	hid.setLevel(2);
    	assertEquals(2, hid.getLevel());
    	assertEquals(hid.getLevel(), hid.level());
    }
    
    @Test
    public void testLevelWrong() {
    	HID hid = new HID("3.2.1.0");
    	assertEquals(3, hid.getLevel());
    	hid.setLevel(5);
    	assertEquals(3, hid.getLevel());
    	assertEquals(hid.getLevel(), hid.level());
    }
    
    @Test
    public void testEquals() {
    	HID hid = new HID("3.2.1.0");
    	assertTrue(hid.equals(hid));
    	assertFalse(hid.equals("test"));
    }
    
    @Test
    public void testSetContextID() {
    	HID hid = new HID("0.0.0.0");
    	hid.setContextID3(3);
    	hid.setContextID2(2);
    	hid.setContextID1(1);
    	hid.setDeviceID(0);
    	assertEquals("3.2.1.0", hid.toString());
    	assertEquals((Long)3L, hid.getContext(3));
    	assertEquals((Long)2L, hid.getContext(2));
    	assertEquals((Long)1L, hid.getContext(1));
    	assertEquals(null, hid.getContext(0));
    }
    
    @Test
    public void testSetContext() {
    	HID hid = new HID("0.0.0.0");
    	hid.setContext(3, 3);
    	hid.setContext(2, 2);
    	hid.setContext(1, 1);
    	hid.setDeviceID(0);
    	assertEquals("3.2.1.0", hid.toString());
    	assertEquals((Long)3L, hid.getContext(3));
    	assertEquals((Long)2L, hid.getContext(2));
    	assertEquals((Long)1L, hid.getContext(1));
    	assertEquals(null, hid.getContext(0));
    }
}
