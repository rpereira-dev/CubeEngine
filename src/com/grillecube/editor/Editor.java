package com.grillecube.editor;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import com.grillecube.client.window.GLWindow;
import com.grillecube.common.gui.Scene;
import com.grillecube.common.gui.events.GLFWInputSystem;
import com.grillecube.common.gui.events.InputSystem;
import com.grillecube.common.logger.Logger;
import com.grillecube.common.logger.Logger.Level;

public class Editor {
	private static final int WIDTH = 1024;
	private static final int HEIGHT = 768;
	private GLWindow window;
	private Scene scene;
	private InputSystem inputSystem;

	public Editor() {
		new Logger(System.out);
		window = new GLWindow();
		scene = new Scene();
		inputSystem = new GLFWInputSystem(window.getPointer());
	}
	
	private void start() {
		window.start();
		loop();
	}
	
	private void stop() {
		window.stop();
	}
	
	public void loop() {
		while(!window.shouldClose()) {
			window.prepareScreen();
			/**
			 * Do GUI stuff
			 */
			scene.update();
			scene.render();
			window.getPointer(); //Window pointer
			window.flushScreen();
		}
	}
	
	public void run() {
		start();
		loop();
		stop();
	}
	
	public static void main(String[] args) {
		new Editor().run();
	}
}