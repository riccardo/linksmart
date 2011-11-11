package eu.linksmart.network.routing.impl;

import org.osgi.service.component.ComponentContext;

import eu.linksmart.network.backbone.Backbone;
import eu.linksmart.network.routing.BackboneRouter;


/*
 * TODO #NM refactoring
 */
public class BackboneRouterImpl implements BackboneRouter{
	private static String BACKBONE_ROUTER = BackboneRouterImpl.class.getSimpleName();
	
protected void activate(ComponentContext context) {
	
	System.out.println(BACKBONE_ROUTER + "started");
	
}
protected void deactivate(ComponentContext context) {
	System.out.println(BACKBONE_ROUTER + "stopped");
}

protected void bindBackbone(Backbone backbone){
	
}

protected void unbindBackbone(Backbone backbone){
	
}
}
