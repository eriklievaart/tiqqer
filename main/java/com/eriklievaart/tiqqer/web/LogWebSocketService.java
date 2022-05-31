package com.eriklievaart.tiqqer.web;

import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;
import java.util.logging.LogRecord;

import com.eriklievaart.jl.core.api.websocket.WebSocketService;
import com.eriklievaart.tiqqer.api.TiqqerService;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;

public class LogWebSocketService implements WebSocketService, TiqqerService {
	public static final int DEFAULT_BUFFER = 10000;

	private final DebugLogger debug;
	private final List<LogRecord> buffer = NewCollection.concurrentList();
	private final List<LogWebSocket> sockets = NewCollection.concurrentList();

	public LogWebSocketService(DebugLogger debug) {
		this.debug = debug;
	}

	@Override
	public String getPath() {
		return "/tiqqer";
	}

	@Override
	public Supplier<?> webSocketSupplier() {
		return () -> {
			LogWebSocket socket = new LogWebSocket(buffer, debug);
			sockets.add(socket);
			return socket;
		};
	}

	@Override
	public void publish(LogRecord record) {
		buffer.add(record);
		if (buffer.size() > DEFAULT_BUFFER) {
			buffer.remove(0);
		}
		removeDeadSockets();
		sendUpdate(record);
	}

	private void sendUpdate(LogRecord record) {
		for (LogWebSocket socket : sockets) {
			try {
				socket.update(record);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void removeDeadSockets() {
		Iterator<LogWebSocket> iterator = sockets.iterator();
		while (iterator.hasNext()) {
			LogWebSocket socket = iterator.next();
			if (socket.isDisconnected()) {
				debug.log("removing dead socket");
				sockets.remove(socket);
			}
		}
	}
}
