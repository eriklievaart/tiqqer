package com.eriklievaart.tiqqer.agent;

import org.osgi.framework.BundleContext;

import com.eriklievaart.osgi.toolkit.api.ActivatorWrapper;
import com.eriklievaart.osgi.toolkit.api.ServiceCollection;
import com.eriklievaart.tiqqer.agent.api.TiqqerService;
import com.eriklievaart.toolkit.logging.api.appender.Appender;

public class Activator extends ActivatorWrapper {

	@Override
	protected void init(BundleContext context) throws Exception {
		ServiceCollection<TiqqerService> clients = getServiceCollection(TiqqerService.class);
		addServiceWithCleanup(Appender.class, new AgentAppender(clients));
	}
}
