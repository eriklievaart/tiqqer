package com.eriklievaart.tiqqer.agent;

import java.io.File;
import java.util.List;
import java.util.logging.Level;

import org.osgi.framework.BundleContext;

import com.eriklievaart.osgi.toolkit.api.ActivatorWrapper;
import com.eriklievaart.osgi.toolkit.api.ContextWrapper;
import com.eriklievaart.tiqqer.agent.api.LogRecordListenerService;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;
import com.eriklievaart.toolkit.lang.api.str.Str;
import com.eriklievaart.toolkit.logging.api.LogConfig;
import com.eriklievaart.toolkit.logging.api.appender.Appender;
import com.eriklievaart.toolkit.logging.api.appender.ConsoleAppender;
import com.eriklievaart.toolkit.logging.api.appender.RotatingFileAppender;
import com.eriklievaart.toolkit.logging.api.level.LogLevelTool;

public class Activator extends ActivatorWrapper {

	private static final String ACTIVE = "true";
	private static final String SERVICE_PROPERTY = "com.eriklievaart.tiqqer.agent.service";
	private static final String CONSOLE_PROPERTY = "com.eriklievaart.tiqqer.agent.console";
	private static final String CONSOLE_LEVEL_PROPERTY = "com.eriklievaart.tiqqer.agent.console.level";
	private static final String FILE_PROPERTY = "com.eriklievaart.tiqqer.agent.file";
	private static final String FILE_LEVEL_PROPERTY = "com.eriklievaart.tiqqer.agent.file.level";

	@Override
	protected void init(BundleContext context) throws Exception {

		List<Appender> appenders = NewCollection.list();
		addConsoleAppender(getContextWrapper(), appenders);
		addFileAppender(getContextWrapper(), appenders);
		addServiceAppender(getContextWrapper(), appenders);
		LogConfig.setDefaultAppenders(appenders);

		for (Appender appender : LogConfig.getDefaultAppenders()) {
			System.out.println(Str.sub("configured appender: $", appender));
		}
	}

	private void addServiceAppender(ContextWrapper wrapper, List<Appender> appenders) {
		String property = wrapper.getPropertyString(SERVICE_PROPERTY, ACTIVE);
		if (Str.isEqual(property, ACTIVE)) {
			appenders.add(new LogRecordAppenderAgent(getServiceCollection(LogRecordListenerService.class)));
		}
	}

	private void addConsoleAppender(ContextWrapper wrapper, List<Appender> appenders) {
		Level level = LogLevelTool.toLevel(wrapper.getPropertyString(CONSOLE_LEVEL_PROPERTY, "TRACE"));

		String property = wrapper.getPropertyString(CONSOLE_PROPERTY, ACTIVE);
		if (Str.isEqual(property, ACTIVE)) {
			ConsoleAppender appender = new ConsoleAppender();
			appender.setLevel(level);
			appenders.add(appender);
		}
	}

	private void addFileAppender(ContextWrapper wrapper, List<Appender> appenders) {
		Level level = LogLevelTool.toLevel(wrapper.getPropertyString(FILE_LEVEL_PROPERTY, "TRACE"));

		wrapper.getPropertyStringOptional(FILE_PROPERTY, path -> {
			RotatingFileAppender appender = new RotatingFileAppender(new File(path));
			appender.setLevel(level);
			appenders.add(appender);
		});
	}
}