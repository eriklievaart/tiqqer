package com.eriklievaart.tiqqer.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketListener;

import com.eriklievaart.toolkit.io.api.RuntimeIOException;
import com.eriklievaart.toolkit.lang.api.collection.ListTool;
import com.eriklievaart.toolkit.lang.api.str.Str;

public class LogWebSocket implements WebSocketListener {

	private Session session;
	private DebugLogger debug;
	private boolean update = true;
	private boolean disconnected = false;
	private int buffer = LogWebSocketService.DEFAULT_BUFFER;
	private CopyOnWriteArrayList<LogRecord> records;
	private LogFilter filter = new LogFilter();

	public LogWebSocket(List<LogRecord> buffer, DebugLogger debug) {
		this.records = new CopyOnWriteArrayList<>(buffer);
		this.debug = debug;
	}

	@Override
	public void onWebSocketConnect(Session connected) {
		debug.log("socket opened $", getClass().getSimpleName());
		this.session = connected;
	}

	@Override
	public void onWebSocketClose(int status, String message) {
		debug.log("socket closed $", getClass().getSimpleName());
		disconnected = true;
		records.clear();
	}

	@Override
	public void onWebSocketText(String input) {
		try {
			switch (input.trim().replaceFirst("\\W.*", "")) {

			case "fetch":
				fetch();
				break;

			case "clear":
				clear();
				break;

			case "config":
				config(input.trim().replaceFirst("\\w++", "").trim());
				break;

			default:
				throw new RuntimeException("unrecognized command: " + input);
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void onWebSocketError(Throwable arg0) {
	}

	@Override
	public void onWebSocketBinary(byte[] arg0, int arg1, int arg2) {
	}

	public boolean isDisconnected() {
		return disconnected;
	}

	private void clear() {
		records.clear();
		update = true;
	}

	void config(String raw) {
		debug.log("config: $", raw);

		for (String setting : raw.split("\\s++")) {
			String key = setting.replaceFirst("=.*", "").trim();
			String value = setting.replaceFirst("[^=]++=", "".trim());
			config(key, value);
		}
		update = true;
	}

	private void config(String key, String value) {
		switch (key) {

		case "level":
			filter.setLevel(Level.parse(value.toUpperCase()));
			return;

		case "class":
			filter.setClazz(value);
			return;

		case "message":
			filter.setMessage(value);
			return;

		case "lines":
			filter.setLines(value);
			return;

		case "buffer":
			buffer = Integer.parseInt(value.trim().replaceAll("\\D++", ""));
			return;

		default:
			throw new RuntimeIOException("Unknown setting %", key);
		}
	}

	private void fetch() throws IOException {
		if (!update) {
			return;
		}
		ArrayList<LogRecord> clone = new ArrayList<>(records);
		List<String> lines = ListTool.map(filter.filter(clone), r -> {
			return Str.sub("$,$,$", r.getLevel(), r.getLoggerName(), HtmlMessage.format(r.getMessage()));
		});
		debug.log("returning $ out of $ records", lines.size(), clone.size());
		session.getRemote().sendString(Str.joinLines(lines));
		update = false;
	}

	public void update(LogRecord record) throws IOException {
		if (!disconnected) {
			records.add(record);
			if (records.size() > buffer) {
				records.remove(0);
			}
			update = true;
		}
	}
}
