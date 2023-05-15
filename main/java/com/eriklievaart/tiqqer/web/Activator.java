package com.eriklievaart.tiqqer.web;

import java.io.File;

import org.osgi.framework.BundleContext;

import com.eriklievaart.jl.core.api.osgi.LightningActivator;
import com.eriklievaart.jl.core.api.page.PageSecurity;
import com.eriklievaart.jl.core.api.websocket.WebSocketService;
import com.eriklievaart.osgi.toolkit.api.ContextWrapper;
import com.eriklievaart.tiqqer.agent.api.TiqqerService;

public class Activator extends LightningActivator {
	private static final String HOT_DEPLOYMENT_DIR = "com.eriklievaart.tiqqer.web.hot";
	private static final String DEBUG = "com.eriklievaart.tiqqer.web.debug";

	public Activator() {
		super("tiqqer");
	}

	@Override
	protected void init(BundleContext context) throws Exception {
		DebugLogger logger = new DebugLogger(getContextWrapper().getPropertyBoolean(DEBUG, false));

		LogWebSocketService service = new LogWebSocketService(logger);
		addServiceWithCleanup(WebSocketService.class, service);
		addServiceWithCleanup(TiqqerService.class, service);

		ContextWrapper wrapper = getContextWrapper(); // line required for osgi import
		String property = wrapper.getPropertyString(HOT_DEPLOYMENT_DIR, null);
		File hot = property == null ? null : new File(property);
		registerRoutes(hot);
	}

	private void registerRoutes(File hot) {
		StaticPageControllerFactory factory = new StaticPageControllerFactory(hot);
		addPageService(builder -> {
			builder.newIdentityRouteGet("", () -> factory.createController("/web/index.html"));
			builder.newIdentityRouteGet("style.css", () -> factory.createController("/web/style.css"));
			builder.newIdentityRouteGet("socket.js", () -> factory.createController("/web/socket.js"));
			builder.setSecurity(new PageSecurity((route, ctx) -> true));
		});
	}
}
