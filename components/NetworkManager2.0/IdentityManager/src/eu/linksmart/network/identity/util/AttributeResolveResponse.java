package eu.linksmart.network.identity.util;

import java.io.Serializable;

import eu.linksmart.network.HID;

/**
 * Class containing hid and AttributeResponseFilter pairs.
 * @author Vinkovits
 *
 */
public class AttributeResolveResponse implements Serializable {
	private static final long serialVersionUID = 1L;
	HID hid;
	AttributeResolveFilter filter;

	public AttributeResolveResponse(HID hid, AttributeResolveFilter filter) {
		this.hid = hid;
		this.filter = filter;
	}

	public HID getHid() {
		return this.hid;
	}

	public AttributeResolveFilter getFilter() {
		return this.filter;
	}
}
