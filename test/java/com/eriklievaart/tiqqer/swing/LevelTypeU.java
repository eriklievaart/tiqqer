package com.eriklievaart.tiqqer.swing;

import org.junit.Test;

import com.eriklievaart.toolkit.lang.api.check.Check;

public class LevelTypeU {

	@Test
	public void isLoggableInfo() {
		Check.isTrue(LevelType.INFO.isLoggable(LevelType.ERROR));
		Check.isTrue(LevelType.INFO.isLoggable(LevelType.WARN));
		Check.isTrue(LevelType.INFO.isLoggable(LevelType.INFO));
		Check.isFalse(LevelType.INFO.isLoggable(LevelType.DEBUG));
		Check.isFalse(LevelType.INFO.isLoggable(LevelType.TRACE));
	}
}