package com.eriklievaart.tiqqer.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.function.Function;

import com.eriklievaart.jl.bundle.api.ResponseBuilder;
import com.eriklievaart.jl.bundle.api.page.PageController;
import com.eriklievaart.jl.bundle.api.render.InputStreamRenderer;
import com.eriklievaart.toolkit.io.api.RuntimeIOException;

public class StaticPageControllerFactory {

	private final Function<String, PageController> function;

	public StaticPageControllerFactory(File hot) {
		if (hot == null) {
			function = resource -> createController(getClass().getResourceAsStream(resource));

		} else {
			function = resource -> {
				try {
					return hotOrFallback(hot, resource);
				} catch (FileNotFoundException e) {
					throw new RuntimeIOException("This exception should never occur!", e);
				}
			};
		}
	}

	private PageController hotOrFallback(File root, String resource) throws FileNotFoundException {
		File hot = new File(root, resource);
		System.out.println("hot file " + hot + " exists=" + hot.exists());
		if (hot.exists()) {
			return createController(new FileInputStream(hot));
		} else {
			return createController(getClass().getResourceAsStream(resource));
		}
	}

	public PageController createController(String resource) {
		return function.apply(resource);
	}

	private PageController createController(InputStream is) {
		return new PageController() {
			@Override
			public void invoke(ResponseBuilder response) throws Exception {
				response.setRenderer(new InputStreamRenderer(is));
			}
		};
	}
}
