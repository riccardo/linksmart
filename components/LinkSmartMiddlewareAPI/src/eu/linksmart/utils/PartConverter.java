package eu.linksmart.utils;

import java.util.Properties;
import java.util.Map.Entry;

@Deprecated public class PartConverter {
	@Deprecated public static Part[] fromProperties(Properties attributes) {
			@SuppressWarnings("unchecked")
			Entry<String, String>[] entries = (Entry<String, String>[]) attributes
					.entrySet().toArray();
			Part[] newAttributes = new Part[entries.length];
			for (int i = 0; i < entries.length; i++) {
				newAttributes[i] = new Part(entries[i].getKey(),
						entries[i].getValue());
			}
			return newAttributes;
		}

	@Deprecated public static Properties toProperties(Part[] attributes) {
		Properties result = new Properties();
		for (Part p : attributes) {
			result.put(p.getKey(), p.getValue());
		}
		return result;
	}
}
