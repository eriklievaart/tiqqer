package com.eriklievaart.tiqqer.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.lang.api.str.Str;

public class LogFilter {
	private static final int DEFAULT_LINES = 0;

	private int max = DEFAULT_LINES;
	private int filterLevel = Level.FINEST.intValue();
	private String clazz = "";
	private String message = "";

	public void setLines(String value) {
		String digits = value.replaceAll("\\D", "");
		max = Str.isBlank(digits) ? DEFAULT_LINES : Integer.parseInt(digits);
	}

	public void setMax(int value) {
		max = value;
	}

	public void setLevel(Level value) {
		Check.notNull(value);
		filterLevel = value.intValue();
	}

	public void setClazz(String value) {
		clazz = value;
	}

	public void setMessage(String value) {
		message = value;
	}

	boolean test(LogRecord record) {
		if (!Str.isBlank(clazz) && !record.getLoggerName().contains(clazz)) {
			return false;
		}
		if (!Str.isBlank(message) && !record.getMessage().contains(message)) {
			return false;
		}
		return record.getLevel().intValue() >= filterLevel;
	}

	public List<LogRecord> filter(List<LogRecord> records) {
		Check.notNull(records);
		return filterFromEnd(records, this::test);
	}

	<E> List<E> filterFromEnd(List<E> records, Predicate<E> predicate) {
		List<E> out = new ArrayList<>();

		for (int i = records.size() - 1; i >= 0; i--) {

			E record = records.get(i);
			if (predicate.test(record)) {
				out.add(record);

				if (out.size() >= max) {
					break;
				}
			}
		}
		Collections.reverse(out);
		return out;
	}
}
