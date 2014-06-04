package eu.linksmart.policy.pip;

import java.net.URI;
import java.util.Set;

public interface PolicyInformationPoint {

	/**
	 * Tries to retrieve an attribute that is missing from the request to evaluate
	 * a policy.
	 * @param attributeType type of attribute searched
	 * @param attributeId id of attribute searched
	 * @param issuer
	 * @param category
	 * @param request the original request xml
	 * @return null if attribute was not found, else xml of an XACML attribute with attribute values
	 */
	public String findAttribute(URI attributeType, URI attributeId, String issuer,
            URI category, String request);
	
	/**
	 * Returns what attribute categories are supported, e.g. subject, resource, etc.
	 * @return
	 */
	public Set<String> getSupportedCategories();
	
	/**
	 * Returns what attribute ids are supported, i.e. what attributeId values
	 * are interpreted by findAttribute.
	 * @return
	 */
	public Set<URI> getSupportedIds();
	
	/**
	 * @return A unique identification for this PIP.
	 */
	public String getId();
}
