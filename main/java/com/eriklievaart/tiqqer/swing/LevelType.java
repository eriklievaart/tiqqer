package com.eriklievaart.tiqqer.swing;

import java.util.logging.Level;

import com.eriklievaart.toolkit.lang.api.FormattedException;

public enum LevelType {

	ERROR, WARN, INFO, DEBUG, TRACE;

	public static LevelType fromJulLevel(Level level) {
		switch (level.toString()) {

		case "FINEST":
			return TRACE;

		case "FINER":
			return TRACE;

		case "FINE":
			return DEBUG;

		case "CONFIG":
		case "INFO":
			return INFO;

		case "WARNING":
			return WARN;

		case "ERROR":
			return ERROR;

		case "SEVERE":
			return ERROR;
		}
		throw new FormattedException("unknown level %", level.toString());
	}

	public boolean isLoggable(LevelType level) {
		return this.ordinal() >= level.ordinal();
	}
}
