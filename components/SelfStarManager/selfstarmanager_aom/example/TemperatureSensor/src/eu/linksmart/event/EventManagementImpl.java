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
package eu.linksmart.event;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.*;

public class EventManagementImpl implements EventManagement {
    // Protocol
    // eventmanager/<method>
    // <method> := publish | subscribe | unsubscribe
    // publish: {topic: <some topic>, properties:{key1: value1, ..., keyN: valueN}}
    // subscribe: {topic: ..., subscriber: ...}

    private String publishURL;
    private String subscribeURL;
    private final String emURL;

    public EventManagementImpl(String emURL) {
        this.emURL = emURL;
        System.out.println("Event Manager is = " + emURL);
    }

    private String sendMessage(String method, String message) throws IOException {
        StringBuffer result = new StringBuffer();
        HttpConnection conn = null;
        OutputStream out = null;
        InputStream in = null;
        try {
            String resp;

            conn = (HttpConnection) Connector.open(
                    emURL + "/eventmanager/" + method, Connector.READ_WRITE, true);
            conn.setRequestMethod(HttpConnection.POST);
            conn.setRequestProperty("Connection", "close");

            out = conn.openOutputStream();
            out.write(message.getBytes());
            out.flush();
            in = conn.openInputStream();

            //InputStream in = conn.openInputStream();
            resp = conn.getResponseMessage();
            if (resp.equals("OK")) {
                int len = (int) conn.getLength();
                if (len > 0) {
                    byte[] data = new byte[len];
                    int actual = in.read(data);
                    result.append(new String(data, 0, actual));
                } else {
                    int ch;
                    while ((ch = in.read()) != -1) {
                        result.append((char) ch);
                    }
                }
            } else {
                result = new StringBuffer(conn.getResponseCode() + " " + resp);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        return result.toString();
    }

    public String publish(String topic, String[] property_keys,
            String[] property_values) throws IOException {
        String resp = "FAILURE";
        try {
            String publication = "{\"topic\": \"" + topic + "\", \"properties\":{";
            for (int i = 0; i < property_keys.length; i++) {
                publication += "\"" + property_keys[i] + "\"" + ":" + property_values[i];
                if (i < property_keys.length - 1) {
                    publication += ", ";
                }
            }
            publication += "}}";

            System.out.println("Publishing: " + publication);

            resp = sendMessage("publish", publication);

            System.out.println("Response: " + resp);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return resp;
    }

    public String notify_pull(String topic, String subscriber) throws IOException {
        String resp = "FAILURE";
        try {
            String message = "{\"topic\": \"" + topic + "\", \"subscriber\":\""
                    + subscriber + "\"}}";

            System.out.println("Getting notification: " + message);

            resp = sendMessage("notify_pull", message);

            System.out.println("Response: " + resp);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return resp;
    }
}
