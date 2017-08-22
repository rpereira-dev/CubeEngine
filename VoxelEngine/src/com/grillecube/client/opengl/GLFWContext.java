package com.grillecube.client.opengl;

import java.util.ArrayList;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;

import com.grillecube.client.opengl.object.GLObject;
import com.grillecube.client.opengl.window.GLFWWindow;

public class GLFWContext {

	/** lists of every allocated gl objects */
	private ArrayList<GLObject> _gl_objects;

	private GLFWWindow _window;
	private GLCapabilities _capabilities;

	private int _draw_calls;
	private int _vertices_drawn;

	public GLFWContext(GLFWWindow window) {
		this._window = window;
		this._gl_objects = new ArrayList<GLObject>();
	}

	public void createCapabilities() {
		if (this._capabilities != null) {
			return;
		}
		this._capabilities = GL.createCapabilities();
	}

	public GLCapabilities getCapabilities() {
		return (this._capabilities);
	}

	public GLFWWindow getWindow() {
		return (this._window);
	}

	public void addObject(GLObject buffer) {
		_gl_objects.add(buffer);
	}

	public void removeObject(GLObject buffer) {
		_gl_objects.remove(buffer);
	}

	public void destroy() {
		for (GLObject object : this._gl_objects) {
			object.delete();
		}
		this._window.close();
		this._window = null;
		this._gl_objects = null;
	}

	/**
	 * increment the number of draw calls (should be call when using a gl draw
	 * function)
	 */
	public void incrementDrawCalls() {
		this._draw_calls++;
	}

	/** return the number of gl draw calls since last 'resetDrawCalls()' */
	public int getDrawCalls() {
		return (this._draw_calls);
	}

	/**
	 * reset the number of draw calls
	 * 
	 * @return the number of draw calls before the reset
	 */
	public int resetDrawCalls() {
		int i = this._draw_calls;
		this._draw_calls = 0;
		return (i);
	}

	/** increase the amount of vertices drawn */
	public void increaseVerticesDrawn(int inc) {
		this._vertices_drawn += inc;
	}

	/** get the number of vertices drawn since last 'resetDrawVertices()' */
	public int getVerticesDrawn() {
		return (this._vertices_drawn);
	}

	/**
	 * reset the number of vertices drawn
	 * 
	 * @return the number of vertices drawn before the reset
	 */
	public int resetDrawVertices() {
		int i = this._vertices_drawn;
		this._vertices_drawn = 0;
		return (i);
	}
}
