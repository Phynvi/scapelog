package com.scapelog.client.loader;

import java.applet.AppletContext;
import java.applet.AppletStub;
import java.net.URL;

public final class ClientAppletStub implements AppletStub {

	private final JavConfig config;

	private final URL codebase;

	public ClientAppletStub(JavConfig config, URL codebase) {
		this.config = config;
		this.codebase = codebase;
	}

	@Override
	public boolean isActive() {
		return true;
	}

	@Override
	public URL getDocumentBase() {
		return codebase;
	}

	@Override
	public URL getCodeBase() {
		return codebase;
	}

	@Override
	public String getParameter(String name) {
		return config.getParameter(name);
	}

	@Override
	public AppletContext getAppletContext() {
		return null;
	}

	@Override
	public void appletResize(int width, int height) {

	}
}