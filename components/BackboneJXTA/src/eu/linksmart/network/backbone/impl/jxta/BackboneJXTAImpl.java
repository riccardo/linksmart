package eu.linksmart.network.backbone.impl.jxta;

import org.osgi.service.component.ComponentContext;

import eu.linksmart.network.backbone.Backbone;

/*
 * TODO #NM refactoring
 */
public class BackboneJXTAImpl implements Backbone{
	
	private static String BACKBONE_JXTA = BackboneJXTAImpl.class.getSimpleName();
	
protected void activate(ComponentContext context) {
	
	System.out.println(BACKBONE_JXTA + "started");
	
}
protected void deactivate(ComponentContext context) {
	System.out.println(BACKBONE_JXTA + "stopped");
}

}
