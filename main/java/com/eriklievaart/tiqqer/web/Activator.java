package com.eriklievaart.tiqqer.web;

import java.io.File;

import org.osgi.framework.BundleContext;

import com.eriklievaart.javalightning.bundle.api.osgi.LightningActivator;
import com.eriklievaart.javalightning.bundle.api.page.PageSecurity;
import com.eriklievaart.osgi.toolkit.api.ContextWrapper;

public class Activator extends LightningActivator {
	private static final String HOT_DEPLOYMENT_DIR = "com.eriklievaart.tiqqer.web.hot";

	public Activator() {
		super("tiqqer");
	}

	@Override
	protected void init(BundleContext context) throws Exception {
		ContextWrapper wrapper = getContextWrapper(); // osgi import
		String property = wrapper.getPropertyString(HOT_DEPLOYMENT_DIR, null);
		File hot = property == null ? null : new File(property);
		StaticPageControllerFactory factory = new StaticPageControllerFactory(hot);

		addPageService(builder -> {
			builder.newIdentityRouteGet("", () -> factory.createController("/web/index.html"));
			builder.newIdentityRouteGet("style.css", () -> factory.createController("/web/style.css"));
			builder.setSecurity(new PageSecurity((route, ctx) -> true));
		});
	}
}
