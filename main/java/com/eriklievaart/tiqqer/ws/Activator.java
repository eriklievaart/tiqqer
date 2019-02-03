package com.eriklievaart.tiqqer.ws;

import javax.servlet.Servlet;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;

import com.eriklievaart.osgi.toolkit.api.ActivatorWrapper;
import com.eriklievaart.toolkit.logging.api.LogTemplate;

public class Activator extends ActivatorWrapper {
	private static final String HTTP_SERVLET_PATTERN = HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN;

	private LogTemplate log = new LogTemplate(getClass());

	@Override
	protected void init(BundleContext context) throws Exception {
		ClassLoader original = Thread.currentThread().getContextClassLoader();
		ClassLoader jettyClassLoader = getJettyBundle(context).adapt(BundleWiring.class).getClassLoader();
		Thread.currentThread().setContextClassLoader(jettyClassLoader);

		try {
			addServiceWithCleanup(Servlet.class, new MyWebSocketServlet(), dictionary(HTTP_SERVLET_PATTERN, "/socket"));

		} catch (Exception e) {
			log.error("unable to start WebSocketServlet", e);

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
