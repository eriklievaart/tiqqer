package com.eriklievaart.tiqqer.agent.api;

import java.util.logging.LogRecord;

public interface TiqqerService {

	public void publish(LogRecord record);
}
