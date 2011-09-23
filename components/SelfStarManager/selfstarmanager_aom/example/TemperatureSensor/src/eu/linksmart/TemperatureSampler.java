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
package eu.linksmart;

import com.sun.spot.peripheral.IBattery;
import com.sun.spot.peripheral.Spot;
import com.sun.spot.resources.Resources;
import com.sun.spot.resources.transducers.ISwitch;
import com.sun.spot.resources.transducers.ISwitchListener;
import com.sun.spot.resources.transducers.ITriColorLED;
import com.sun.spot.resources.transducers.ITemperatureInput;
import com.sun.spot.resources.transducers.LEDColor;
import com.sun.spot.resources.transducers.SwitchEvent;
import com.sun.spot.util.Utils;
import eu.linksmart.event.EventManagement;
import eu.linksmart.event.EventManagementImpl;
import java.io.IOException;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import org.json.me.JSONException;
import org.json.me.JSONObject;

public class TemperatureSampler extends MIDlet {

    private static int SAMPLE_PERIOD = 2 * 1000;  // in milliseconds
    private double startTemperature;
    private ITriColorLED[] leds;
    private IBattery battery;
    private int battery_level_modifier = 0; // Debug
    private EventManagement eventManagement;
    private String ieeeAddress;

    protected void startApp() throws MIDletStateChangeException {
        ieeeAddress = System.getProperty("IEEE_ADDRESS");
        System.out.println("Starting LinkSmart temperature sensor application on " + ieeeAddress + " ...");

        eventManagement = new EventManagementImpl(getAppProperty("Event-Manager").trim());

        leds = new ITriColorLED[8];
        for (int i = 0; i < 8; i++) {
            leds[i] = (ITriColorLED) Resources.lookup(ITriColorLED.class, "LED" + (i + 1));
        }

        battery = Spot.getInstance().getPowerController().getBattery();


        // Listen for downloads/commands over USB connection
        new com.sun.spot.service.BootloaderListenerService().getInstance().start();

        // Find buttons
        ISwitch sw1 = (ISwitch) Resources.lookup(ISwitch.class, "SW1");
        if (sw1 != null) {
            sw1.addISwitchListener(new ISwitchListener() {

                public void switchPressed(SwitchEvent se) {
                    battery_level_modifier = (battery_level_modifier + 25) % 100;
                    flashLEDs();
                    System.out.println("Battery level modified by: " + battery_level_modifier);
                }

                public void switchReleased(SwitchEvent se) {
                }
            });
        }

        ISwitch sw2 = (ISwitch) Resources.lookup(ISwitch.class, "SW2");
        if (sw2 != null) {
            sw2.addISwitchListener(new ISwitchListener() {

                public void switchPressed(SwitchEvent se) {
                    startTemperature = startTemperature - 0.25; // Simulate temperature rise
                }

                public void switchReleased(SwitchEvent se) {
                }
            });

        }

        // Start!

        startSampling();
    }

    private void startSampling() {
        ITemperatureInput temperatureInput = (ITemperatureInput) Resources.lookup(ITemperatureInput.class);
        // Get the first temperature reading
        try {
            startTemperature = temperatureInput.getCelsius();
        } catch (IOException ex) {
            System.err.println("Caught " + ex + " while collecting sensor sample.");
        }
        long now = System.currentTimeMillis();
        while (true) {
            try {
                // Get the current time and temperature reading
                now = System.currentTimeMillis();
                double reading = temperatureInput.getCelsius();
                System.out.println("Start temperature value = " + startTemperature);
                System.out.println("Temperature value = " + reading);

                // Flash LEDs to indicate a reading
                flashLEDs();

                // Set LEDs to indicate temperature
                showTemperature(reading);

                // Communicate with backend
                communicate(reading);

            } catch (Exception e) {
                System.err.println("Caught " + e + " while collecting/sending sensor sample.");
            } finally {
                // Go to sleep to conserve battery
                Utils.sleep(Math.max(0, SAMPLE_PERIOD - (System.currentTimeMillis() - now)));
            }
        }
    }

    private void showTemperature(double reading) {
        // Set LEDs to indicate temperature
        int numberOfLEDs = Math.max(1, Math.min(8, (int) (4 + 4 * (reading - startTemperature))));
        for (int i = 0; i < numberOfLEDs; i++) {
            leds[i].setColor(getCurrentColor());
            leds[i].setOn();
        }
        for (int i = numberOfLEDs; i < 8; i++) {
            leds[i].setOff();
        }
    }

    private void flashLEDs() {
        boolean[] led_states = new boolean[8];
        for (int i = 0; i < 8; i++) {
            led_states[i] = leds[i].isOn();
            leds[i].setOff();
        }
        Utils.sleep(100);
        LEDColor color = getCurrentColor();
        for (int i = 0; i < 8; i++) {
            if (led_states[i]) {
                leds[i].setColor(color);
                leds[i].setOn();
            }
        }
    }

    protected void pauseApp() {
        // This will never be called by the Squawk VM
    }

    protected void destroyApp(boolean arg0) throws MIDletStateChangeException {
        // Only called if startApp throws any exception other than MIDletStateChangeException
        System.out.println("destroyApp: " + arg0);
    }

    private LEDColor getCurrentColor() {
        int level = getBatteryLevel();
        if (level > 80) {
            return LEDColor.GREEN;
        } else if (level > 50) {
            return LEDColor.YELLOW;
        } else {
            return LEDColor.RED;
        }
    }

    private void updateSamplePeriod(String event) {
        try {
            JSONObject json = new JSONObject(event);
            Object jsonResult = json.get("result");
            if (jsonResult instanceof JSONObject) {
                if (((JSONObject) jsonResult).has("sampling.period")) {
                    Object samplingRate = ((JSONObject) jsonResult).get("sampling.period");
                    int NEW_SAMPLE_PERIOD = 1000 * Integer.parseInt((String) samplingRate); // FIXME: Add checks
                    if (NEW_SAMPLE_PERIOD != SAMPLE_PERIOD) {
                        System.out.println("New sampling period: " + samplingRate);
                        SAMPLE_PERIOD = NEW_SAMPLE_PERIOD;
                        flashLEDs();
                        flashLEDs();
                        flashLEDs();
                    }
                }
            }
        } catch (JSONException ex) {
            System.out.println("Event was: " + event);
            ex.printStackTrace();
        }
    }

    private int getBatteryLevel() {
        int level = battery.getBatteryLevel() - battery_level_modifier;
        if (level > 0) {
            return level;
        } else {
            return 100 + level;
        }
    }
    boolean sending = false;

    private synchronized boolean sending() {
        return sending;
    }

    private synchronized void doneSending() {
        sending = false;
    }

    private synchronized void startSending() {
        sending = true;
    }

    private void communicate(final double reading) throws IOException, JSONException {
//        if (!sending()) {
//            startSending();
//            new Thread(new Runnable() {
//                public void run() {
                    try {
                        // Send temperature reading
                        eventManagement.publish("spot/reading", new String[]{"result", "battery", "sampling.period", "id"}, new String[]{"" + reading, "" + getBatteryLevel(), "" + SAMPLE_PERIOD / 1000, "\"" + ieeeAddress + "\""});
                        // Get messages
                        String event = eventManagement.notify_pull("spot/message", ieeeAddress);
                        updateSamplePeriod(event);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
//                    } finally {
//                        doneSending();
//                    }
//                }
//            }).start();
//        }
    }
}
