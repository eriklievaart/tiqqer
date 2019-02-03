package com.eriklievaart.tiqqer.agent;

import java.util.logging.LogRecord;

import com.eriklievaart.osgi.toolkit.api.ServiceCollection;
import com.eriklievaart.tiqqer.api.TiqqerService;
import com.eriklievaart.toolkit.logging.api.appender.AbstractAppender;

public class AgentAppender extends AbstractAppender {

	private ServiceCollection<TiqqerService> clients;

	public AgentAppender(ServiceCollection<TiqqerService> clients) {
		this.clients = clients;
	}

	@Override
	public void append(LogRecord record) {
		clients.allCall(client -> {
			try {
				client.publish(record);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
}
