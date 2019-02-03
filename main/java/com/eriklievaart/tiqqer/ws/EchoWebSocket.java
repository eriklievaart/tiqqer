package com.eriklievaart.tiqqer.ws;

import java.util.Date;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

@WebSocket
@SuppressWarnings("unused")
public class EchoWebSocket {

	@OnWebSocketConnect
	public void onConnect(Session session) {
		System.out.println("new socket");
	}

	@OnWebSocketClose
	public void onClose(Session session, int status, String message) {
		System.out.println("socked closed");
	}

	@OnWebSocketMessage
	public void onText(Session session, String message) {
		System.out.println("message received: " + message);
		if (session.isOpen()) {
			session.getRemote().sendString(message + " " + new Date(), null);
		}
	}
}
