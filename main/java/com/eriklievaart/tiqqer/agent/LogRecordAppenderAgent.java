package com.eriklievaart.tiqqer.agent;

import java.util.logging.LogRecord;

import com.eriklievaart.osgi.toolkit.api.ServiceCollection;
import com.eriklievaart.tiqqer.agent.api.LogRecordListenerService;
import com.eriklievaart.toolkit.logging.api.appender.AbstractAppender;

public class LogRecordAppenderAgent extends AbstractAppender {

	private ServiceCollection<LogRecordListenerService> clients;

	public LogRecordAppenderAgent(ServiceCollection<LogRecordListenerService> clients) {
		this.clients = clients;
	}

	@Override
	public void write(LogRecord record) {
		clients.allCall(client -> {
			try {
				client.publish(record);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	@Override
	public void close() {
	}
}