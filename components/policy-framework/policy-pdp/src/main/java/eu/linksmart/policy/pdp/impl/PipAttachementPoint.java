package eu.linksmart.policy.pdp.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.wso2.balana.XACMLConstants;
import org.wso2.balana.attr.BagAttribute;
import org.wso2.balana.cond.EvaluationResult;
import org.wso2.balana.ctx.Attribute;
import org.wso2.balana.ctx.EvaluationCtx;
import org.wso2.balana.finder.AttributeFinderModule;

import eu.linksmart.policy.pip.PolicyInformationPoint;

public class PipAttachementPoint extends AttributeFinderModule {

	/** logger */
	private static final Logger logger = Logger.getLogger(PipAttachementPoint.class);
	
	private PolicyInformationPoint pip;

	protected PipAttachementPoint(PolicyInformationPoint pip) {
		this.pip = pip;
	}

	@Override
	public Set<String> getSupportedCategories() {
		return pip.getSupportedCategories();
	}

	@Override
	public Set<URI> getSupportedIds() {
		return pip.getSupportedIds();
	}

	@Override
	public EvaluationResult findAttribute(URI attributeType, URI attributeId,
			String issuer, URI category, EvaluationCtx context) {
		//parse evaluationcontext into string for LS PIP interface
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		context.getRequestCtx().encode(bos);
		String requestString = bos.toString();
		String attribute = pip.findAttribute(attributeType, attributeId, issuer, category,
				requestString);
		//cast XACML standard attribute xml back into Balana specific Attribute object
		if(attribute != null) {
			try {
				Element node =  DocumentBuilderFactory
						.newInstance()
						.newDocumentBuilder()
						.parse(new ByteArrayInputStream(attribute.getBytes()))
						.getDocumentElement();
				Attribute attr = Attribute.getInstance(node, XACMLConstants.XACML_VERSION_3_0);
				if(attr != null) {
					return new EvaluationResult(new BagAttribute(attributeType, attr.getValues()));
				}
			} catch (Exception e) {
				logger.warn("Cannot parse attribute received from PIP");
			} 
		}
		//if there was an error the implementation expects an empty BagAttribute
		return new EvaluationResult(BagAttribute.createEmptyBag(attributeType));
	}
	
	@Override
    public boolean isDesignatorSupported() {
        return true;
    }
	
	@Override
	public String getIdentifier() {
		return pip.getId();
	}
	
	protected PolicyInformationPoint getPip() {
		return pip;
	}
}
