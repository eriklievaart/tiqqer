package com.eriklievaart.tiqqer.agent.api;

import java.util.logging.LogRecord;

public interface LogRecordListenerService {

	public void publish(LogRecord record);
}