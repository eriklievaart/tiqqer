package com.eriklievaart.tiqqer.api;

import java.util.logging.LogRecord;

public interface TiqqerService {

	public void publish(LogRecord record);
}
