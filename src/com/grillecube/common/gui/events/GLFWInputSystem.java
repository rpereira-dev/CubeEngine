package com.grillecube.common.gui.events;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCharCallback;
import org.lwjgl.glfw.GLFWCharModsCallback;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.glfw.GLFW.*;

public class GLFWInputSystem extends InputSystem {
	public GLFWInputSystem(long window) {
		init(window);
	}
	
	public void init(long window) {
		initMouse(window);
		initKeyboard(window);
	}
	
	public void initMouse(long window) {
		GLFW.glfwSetCursorPosCallback(window, new GLFWCursorPosCallback() {
			@Override
			public void invoke(long window, double xpos, double ypos) {
				notifyCursorPosListeners(xpos, ypos);
			}
		});
		
		GLFW.glfwSetMouseButtonCallback(window, new GLFWMouseButtonCallback() {
			@Override
			public void invoke(long window, int button, int action, int mods) {
				
			}
		});
		
		GLFW.glfwSetScrollCallback(window, new GLFWScrollCallback() {
			@Override
			public void invoke(long window, double xoffset, double yoffset) {
				
			}
		});
		
	}
	
	public void initKeyboard(long window) {
		GLFW.glfwSetCharCallback(window, new GLFWCharCallback() {
			@Override
			public void invoke(long window, int codepoint) {
				
			}
		});
		
		GLFW.glfwSetCharModsCallback(window, new GLFWCharModsCallback() {
			@Override
			public void invoke(long window, int codepoint, int mods) {
				
			}
		});
		GLFW.glfwSetKeyCallback(window, new GLFWKeyCallback() {
			@Override
			public void invoke(long window, int key, int scancode, int action, int mods) {
				
			}
		});
	}
}
