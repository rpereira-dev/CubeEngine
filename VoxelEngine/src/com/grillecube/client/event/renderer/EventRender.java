package com.grillecube.client.event.renderer;

import com.grillecube.client.opengl.window.GLFWWindow;
import com.grillecube.client.renderer.MainRenderer;
import com.grillecube.common.VoxelEngine;
import com.grillecube.common.event.Event;

public abstract class EventRender extends Event {
	private final MainRenderer renderer;

	public EventRender(MainRenderer renderer) {
		super();
		this.renderer = renderer;
	}

	public MainRenderer getRenderer() {
		return (this.renderer);
	}

	public GLFWWindow getGLFWWindow() {
		return (this.renderer.getGLFWWindow());
	}

}
