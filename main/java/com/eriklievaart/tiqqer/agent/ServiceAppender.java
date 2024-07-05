package com.eriklievaart.tiqqer.agent;

import java.util.logging.LogRecord;

import com.eriklievaart.osgi.toolkit.api.ServiceCollection;
import com.eriklievaart.toolkit.logging.api.appender.AbstractAppender;
import com.eriklievaart.toolkit.logging.api.appender.Appender;

public class ServiceAppender extends AbstractAppender {
	private ServiceCollection<Appender> sc;

	public ServiceAppender(ServiceCollection<Appender> sc) {
		this.sc = sc;
	}

	@Override
	public void write(LogRecord record) {
		sc.allCall(appender -> {
			try {
				appender.append(record);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	@Override
	public void close() {
	}
}