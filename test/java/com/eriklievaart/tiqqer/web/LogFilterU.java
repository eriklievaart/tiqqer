package com.eriklievaart.tiqqer.web;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import com.eriklievaart.tiqqer.web.LogFilter;
import com.eriklievaart.toolkit.lang.api.check.Check;

public class LogFilterU {

	@Test
	public void testLevel() {
		LogFilter filter = new LogFilter();

		LogRecord finest = new LogRecord(Level.FINEST, "");
		LogRecord fine = new LogRecord(Level.FINE, "");
		LogRecord info = new LogRecord(Level.INFO, "");

		filter.setLevel(Level.FINEST);
		Check.isTrue(filter.test(finest));
		Check.isTrue(filter.test(fine));
		Check.isTrue(filter.test(info));

		filter.setLevel(Level.FINE);
		Check.isFalse(filter.test(finest));
		Check.isTrue(filter.test(fine));
		Check.isTrue(filter.test(info));

		filter.setLevel(Level.INFO);
		Check.isFalse(filter.test(finest));
		Check.isFalse(filter.test(fine));
		Check.isTrue(filter.test(info));
	}

	@Test
	public void testClazz() {
		LogFilter filter = new LogFilter();

		LogRecord record = new LogRecord(Level.INFO, "");
		record.setLoggerName(getClass().getName());

		filter.setClazz("");
		Check.isTrue(filter.test(record));

		filter.setClazz("eriklievaart");
		Check.isTrue(filter.test(record));

		filter.setClazz("LogFilterU");
		Check.isTrue(filter.test(record));

		filter.setClazz("apache");
		Check.isFalse(filter.test(record));
	}

	@Test
	public void testMessage() {
		LogFilter filter = new LogFilter();

		LogRecord aaa = new LogRecord(Level.INFO, "aaa");
		LogRecord bbb = new LogRecord(Level.INFO, "bbb");
		LogRecord abc = new LogRecord(Level.INFO, "abc");

		filter.setMessage("");
		Check.isTrue(filter.test(aaa));
		Check.isTrue(filter.test(bbb));
		Check.isTrue(filter.test(abc));

		filter.setMessage("aaa");
		Check.isTrue(filter.test(aaa));
		Check.isFalse(filter.test(bbb));
		Check.isFalse(filter.test(abc));

		filter.setMessage("b");
		Check.isFalse(filter.test(aaa));
		Check.isTrue(filter.test(bbb));
		Check.isTrue(filter.test(abc));
	}

	@Test
	public void filterFromEnd() {
		LogFilter filter = new LogFilter();
		filter.setMax(3);

		List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
		List<Integer> result = filter.filterFromEnd(list, r -> true);
		Assertions.assertThat(result).isEqualTo(Arrays.asList(3, 4, 5));
	}
}
