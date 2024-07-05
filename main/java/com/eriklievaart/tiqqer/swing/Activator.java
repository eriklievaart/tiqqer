package com.eriklievaart.tiqqer.swing;

import org.osgi.framework.BundleContext;

import com.eriklievaart.osgi.toolkit.api.ActivatorWrapper;
import com.eriklievaart.tiqqer.agent.api.TiqqerService;
import com.eriklievaart.tiqqer.swing.api.TiqqerFrame;

public class Activator extends ActivatorWrapper {
	private static final String SHOW = "com.eriklievaart.tiqqer.swing.show";

	TiqqerUiService service = new TiqqerUiService();

	@Override
	protected void init(BundleContext context) throws Exception {
		addServiceWithCleanup(TiqqerFrame.class, service);
		addServiceWithCleanup(TiqqerService.class, service);

		if (getContextWrapper().getPropertyString(SHOW, "true").equals("true")) {
			service.show();
		}
	}

	@Override
	protected void shutdown() throws Exception {
		service.shutdown();
	}
}
