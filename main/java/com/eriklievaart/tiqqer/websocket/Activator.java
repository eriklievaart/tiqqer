package com.eriklievaart.tiqqer.websocket;

import javax.servlet.Servlet;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;

import com.eriklievaart.osgi.toolkit.api.ActivatorWrapper;
import com.eriklievaart.tiqqer.api.TiqqerService;

public class Activator extends ActivatorWrapper {
	private static final String HTTP_SERVLET_PATTERN = HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN;
	private static final String DEBUG = "com.eriklievaart.tiqqer.web.debug";

	@Override
	protected void init(BundleContext context) throws Exception {
		ClassLoader original = Thread.currentThread().getContextClassLoader();
		ClassLoader jettyClassLoader = getJettyBundle(context).adapt(BundleWiring.class).getClassLoader();
		Thread.currentThread().setContextClassLoader(jettyClassLoader);

		try {
			DebugLogger debug = new DebugLogger(getContextWrapper().getPropertyBoolean(DEBUG, false));
			LogWebSockets sockets = new LogWebSockets(debug);
			CreatorWebSocketServlet servlet = new CreatorWebSocketServlet(sockets);
			addServiceWithCleanup(Servlet.class, servlet, dictionary(HTTP_SERVLET_PATTERN, "/tiqqer-socket"));
			addServiceWithCleanup(TiqqerService.class, sockets);

		} catch (Exception e) {
			e.printStackTrace();
			throw e;

		} finally {
			Thread.currentThread().setContextClassLoader(original);
		}
	}

	private Bundle getJettyBundle(BundleContext context) {
		for (Bundle bundle : context.getBundles()) {
			if (bundle.getSymbolicName().equals("org.apache.felix.http.jetty")) {
				return bundle;
			}
		}
		throw new RuntimeException("Jetty not found");
	}
}
