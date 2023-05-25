package com.eriklievaart.tiqqer.web;

import java.io.File;

import org.osgi.framework.BundleContext;

import com.eriklievaart.jl.core.api.osgi.LightningActivator;
import com.eriklievaart.jl.core.api.page.PageSecurity;
import com.eriklievaart.jl.core.api.websocket.WebSocketService;
import com.eriklievaart.tiqqer.agent.api.TiqqerService;
import com.eriklievaart.toolkit.logging.api.LogTemplate;

public class Activator extends LightningActivator {
	private static final String HOT_DEPLOYMENT_DIR = "com.eriklievaart.tiqqer.web.hot";
	private static final String DEBUG = "com.eriklievaart.tiqqer.web.debug";

	private LogTemplate log = new LogTemplate(getClass());

	public Activator() {
		super("tiqqer");
	}

	@Override
	protected void init(BundleContext context) throws Exception {
		DebugLogger logger = new DebugLogger(getContextWrapper().getPropertyBoolean(DEBUG, false));

		LogWebSocketService service = new LogWebSocketService(logger);
		addServiceWithCleanup(WebSocketService.class, service);
		addServiceWithCleanup(TiqqerService.class, service);

		String property = getContextWrapper().getPropertyString(HOT_DEPLOYMENT_DIR, null);
		File hot = property == null ? null : new File(property);
		log.info("hot loading from $; property=$", hot, HOT_DEPLOYMENT_DIR);
		registerRoutes(hot);
	}

	private void registerRoutes(File hot) {
		StaticsControllerFactory factory = new StaticsControllerFactory(hot);
		addPageService(builder -> {
			builder.newIdentityRouteGet("", () -> factory.createController("/web/index.html"));
			builder.newIdentityRouteGet("style.css", () -> factory.createController("/web/style.css"));
			builder.newIdentityRouteGet("socket.js", () -> factory.createController("/web/socket.js"));
			builder.newIdentityRouteGet("jquery.js", () -> factory.createController("/web/jquery-3.7.0.min.js"));
			builder.setSecurity(new PageSecurity((route, ctx) -> true));
		});
	}
}
