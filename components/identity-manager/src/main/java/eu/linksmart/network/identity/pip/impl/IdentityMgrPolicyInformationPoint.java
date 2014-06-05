package eu.linksmart.network.identity.pip.impl;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.wso2.balana.ParsingException;
import org.wso2.balana.XACMLConstants;
import org.wso2.balana.attr.DateTimeAttribute;
import org.wso2.balana.attr.StringAttribute;
import org.wso2.balana.ctx.AbstractRequestCtx;
import org.wso2.balana.ctx.Attribute;
import org.wso2.balana.ctx.RequestCtxFactory;
import org.wso2.balana.xacml3.Attributes;

import eu.linksmart.network.Registration;
import eu.linksmart.network.VirtualAddress;
import eu.linksmart.network.identity.impl.IdentityManagerImpl;
import eu.linksmart.policy.LinkSmartXacmlConstants;
import eu.linksmart.policy.pip.PolicyInformationPoint;
import eu.linksmart.utils.Part;

public class IdentityMgrPolicyInformationPoint implements PolicyInformationPoint {

	protected static Logger LOG = Logger.getLogger(IdentityMgrPolicyInformationPoint.class);

	private Set<String> supportedCategories = new HashSet<String>();
	private Set<URI> supportedIds = new HashSet<URI>();
	private IdentityManagerImpl idM;

	public IdentityMgrPolicyInformationPoint(IdentityManagerImpl idM) {
		this.idM = idM;
		//add categories IdentityMgr can provide information about
		supportedCategories.add(XACMLConstants.SUBJECT_CATEGORY);
		supportedCategories.add(XACMLConstants.RESOURCE_CATEGORY);
		//add specific ids IdentityMgr can provide information about
		supportedIds.add(URI.create(XACMLConstants.RESOURCE_ID));
		supportedIds.add(LinkSmartXacmlConstants.RESOURCE_RESOURCE_PID.getUri());
		supportedIds.add(LinkSmartXacmlConstants.RESOURCE_LINK_SMART_PREFIX.getUri());		
		supportedIds.add(LinkSmartXacmlConstants.SUBJECT_SUBJECT_ID.getUri());
		supportedIds.add(LinkSmartXacmlConstants.SUBJECT_SUBJECT_PID.getUri());
		supportedIds.add(LinkSmartXacmlConstants.SUBJECT_LINK_SMART_PREFIX.getUri());
	}

	@Override
	public String findAttribute(URI attributeType, URI attributeId,
			String issuer, URI category, String request) {
		try {
			AbstractRequestCtx requestCtx = RequestCtxFactory.getFactory().getRequestCtx(request);
			Set<Attributes> attributes = requestCtx.getAttributesSet();
			Attributes usedAttrs = null;
			//go through all attributes in request and pick out the one we search attribute for
			for(Attributes a : attributes) {
				if(a.getCategory().equals(category)) {
					usedAttrs = a;
					break;
				}
			}
			//if we found an appropriate attribute in the request we go on
			if(usedAttrs != null) {
				//we search for the virtual address in the request
				URI virtualAddressFieldInCategoryId = 
						(category.equals(URI.create(XACMLConstants.SUBJECT_CATEGORY))) ? 
								LinkSmartXacmlConstants.SUBJECT_SUBJECT_ID.getUri() : LinkSmartXacmlConstants.RESOURCE_RESOURCE_ID.getUri();

								for(Attribute a : usedAttrs.getAttributes()) {
									if(a.getId().equals(virtualAddressFieldInCategoryId) &&
											a.getType().toString().equals(StringAttribute.identifier)) {
										//found virtual address for entity, get registration of it
										StringAttribute av = (StringAttribute)a.getValue();
										Registration reg = idM.getServiceInfo(new VirtualAddress(av.getValue()));
										if(reg != null) {
											//extract the LS attribute key that is searched
											String[] uriParts = attributeId.toString().split(":");
											if(uriParts != null && uriParts.length != 0 ) {
												//the last part is the key
												String key = uriParts[uriParts.length - 1];
												for(Part part : reg.getAttributes()) {
													if(part.getKey().equalsIgnoreCase(key)) {
														//return the found value in an attribute XML
														Attribute foundAttr = new Attribute(
																category,
																issuer,
																new DateTimeAttribute(),
																new StringAttribute(part.getValue()),
																true,
																XACMLConstants.XACML_VERSION_3_0);

														StringBuilder sb = new StringBuilder();
														foundAttr.encode(sb);
														return sb.toString();
													}
												}
												//if we get here we do not have the requested information locally
												//TODO trying to resolve the information by discovery
											}
										} else {
											LOG.debug("Did not find registration for PIP resolution: " + av.getValue());
										}
									}
								}
			}
		} catch (ParsingException e) {
			LOG.warn("Was not able to parse request received from PDP in PIP:findAttribute!", e);
		}
		return null;
	}

	@Override
	public Set<String> getSupportedCategories() {
		return this.supportedCategories;
	}

	@Override
	public Set<URI> getSupportedIds() {
		return supportedIds;
	}

	@Override
	public String getId() {
		return IdentityMgrPolicyInformationPoint.class.getSimpleName();
	}

}
