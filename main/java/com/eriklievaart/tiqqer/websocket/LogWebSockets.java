package com.eriklievaart.tiqqer.websocket;

import java.util.Iterator;
import java.util.List;
import java.util.logging.LogRecord;

import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;

import com.eriklievaart.tiqqer.api.TiqqerService;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;

public class LogWebSockets implements WebSocketCreator, TiqqerService {
	public static final int DEFAULT_BUFFER = 10000;

	private DebugLogger debug;
	private List<LogWebSocket> sockets = NewCollection.concurrentList();
	private List<LogRecord> buffer = NewCollection.concurrentList();

	public LogWebSockets(DebugLogger debug) {
		this.debug = debug;
	}

	@Override
	public Object createWebSocket(ServletUpgradeRequest request, ServletUpgradeResponse response) {
		LogWebSocket socket = new LogWebSocket(buffer, debug);
		sockets.add(socket);
		return socket;
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
