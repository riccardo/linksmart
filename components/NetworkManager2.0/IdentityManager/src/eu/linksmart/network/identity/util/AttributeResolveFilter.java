package eu.linksmart.network.identity.util;

import java.io.Serializable;

/**
 * Class containing an attribute resolve request
 * including the Bloom-filter, the random and the
 * string composed of requested keys.
 * @author Vinkovits
 *
 */
public class AttributeResolveFilter implements Serializable {
	static final long serialVersionUID = 1L;
	boolean[] bloomFilter;
	String attributeKeys;
	Long random;

	public AttributeResolveFilter(boolean[] bloom, String attr, long rand) {
		bloomFilter = bloom;
		attributeKeys = attr;
		random = new Long(rand);
	}

	public boolean[] getBloomFilter() {
		return bloomFilter;
	}
	public String getAttributeKeys() {
		return attributeKeys;
	}
	public long getRandom() {
		return random.longValue();
	}
}
