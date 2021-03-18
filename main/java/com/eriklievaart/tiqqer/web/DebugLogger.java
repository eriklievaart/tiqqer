package com.eriklievaart.tiqqer.web;

import com.eriklievaart.toolkit.lang.api.str.Str;

public class DebugLogger {

	private boolean enabled;

	public DebugLogger(boolean enabled) {
		this.enabled = enabled;
	}

	public void log(String message) {
		if (enabled) {
			System.out.println(message);
		}
	}

	public void log(String format, Object... args) {
		if (enabled) {
			System.out.println("@tiqqer@ " + Str.sub(format, args));
		}
	}
}
