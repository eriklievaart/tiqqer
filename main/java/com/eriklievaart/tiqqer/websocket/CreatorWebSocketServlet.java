package com.eriklievaart.tiqqer.websocket;

import org.eclipse.jetty.websocket.servlet.WebSocketCreator;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

public class CreatorWebSocketServlet extends WebSocketServlet {

	private WebSocketCreator creator;

	public CreatorWebSocketServlet(WebSocketCreator creator) {
		this.creator = creator;
	}

	@Override
	public void configure(WebSocketServletFactory factory) {
		factory.setCreator(creator);
	}
}
