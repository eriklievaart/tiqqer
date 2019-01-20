package com.eriklievaart.tiqqer.ws;

import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

public class MyWebSocketServlet extends WebSocketServlet {
	@Override
	public void configure(WebSocketServletFactory factory) {
		factory.register(EchoWebSocket.class);
	}
}
