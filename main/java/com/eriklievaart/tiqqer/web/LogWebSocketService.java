package com.eriklievaart.tiqqer.web;

import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;
import java.util.logging.LogRecord;

import com.eriklievaart.jl.core.api.websocket.WebSocketService;
import com.eriklievaart.tiqqer.agent.api.LogRecordListenerService;
import com.eriklievaart.toolkit.lang.api.IdGenerator;
import com.eriklievaart.toolkit.lang.api.collection.Box2;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;

public class LogWebSocketService implements WebSocketService, LogRecordListenerService {
	public static final int DEFAULT_BUFFER = 10000;

	private IdGenerator id = new IdGenerator();
	private final DebugLogger debug;
	private final List<Box2<Integer, LogRecord>> buffer = NewCollection.concurrentList();
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
		Box2<Integer, LogRecord> box = new Box2<>(id.nextInt(), record);
		buffer.add(box);
		if (buffer.size() > DEFAULT_BUFFER) {
			buffer.remove(0);
		}
		removeDeadSockets();
		sendUpdate(box);
	}

	private void sendUpdate(Box2<Integer, LogRecord> record) {
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