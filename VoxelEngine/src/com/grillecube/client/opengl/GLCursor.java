package com.grillecube.client.opengl;

import org.lwjgl.glfw.GLFW;

public class GLCursor implements GLObject {

	private final long id;

	public GLCursor(long id) {
		this.id = id;
	}

	public GLCursor() {
		this(GLFW.glfwCreateStandardCursor(GLFW.GLFW_ARROW_CURSOR));
	}

	public GLCursor(GLIcon glIcon, int xhot, int yhot) {
		this(GLFW.glfwCreateCursor(glIcon.getGLFWImage(), xhot, yhot));
	}

	@Override
	public void delete() {
		GLFW.glfwDestroyCursor(this.id);
	}

	public final long getID() {
		return (this.id);
	}

}
