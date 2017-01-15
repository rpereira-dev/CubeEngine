package com.grillecube.engine.event.renderer;

import com.grillecube.engine.event.Event;
import com.grillecube.engine.opengl.GLFWWindow;
import com.grillecube.engine.renderer.MainRenderer;
import com.grillecube.engine.renderer.camera.Camera;

public abstract class EventRender extends Event {
	private final MainRenderer _renderer;

	public EventRender(MainRenderer renderer) {
		this._renderer = renderer;
	}

	public MainRenderer getRenderer() {
		return (this._renderer);
	}

	public GLFWWindow getGLFWWindow() {
		return (this._renderer.getGLFWWindow());
	}

	public Camera getCamera() {
		return (this._renderer.getCamera());
	}
}
