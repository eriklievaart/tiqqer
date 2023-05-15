package com.eriklievaart.tiqqer.swing;

import org.osgi.framework.BundleContext;

import com.eriklievaart.osgi.toolkit.api.ActivatorWrapper;
import com.eriklievaart.tiqqer.agent.api.TiqqerService;

public class Activator extends ActivatorWrapper {

	TiqqerUiService service = new TiqqerUiService();

	@Override
	protected void init(BundleContext context) throws Exception {
		addServiceWithCleanup(TiqqerService.class, service);
		service.show();
	}

	@Override
	protected void shutdown() throws Exception {
		service.shutdown();
	}
}
