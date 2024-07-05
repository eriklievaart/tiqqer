package com.eriklievaart.tiqqer.web;

import com.eriklievaart.toolkit.lang.api.str.Str;

public class HtmlMessage {

	public static String format(String message) {
		String shortened = message.length() < 200 ? message : message.substring(0, 200) + " ...";
		String html = Str.join(Str.splitLines(shortened), "<br/>");
		return html.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
	}
}