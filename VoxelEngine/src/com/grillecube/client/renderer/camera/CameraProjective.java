package com.grillecube.client.renderer.camera;

import com.grillecube.client.opengl.GLFWListenerCursorPos;
import com.grillecube.client.opengl.GLFWListenerKeyPress;
import com.grillecube.client.opengl.GLFWListenerKeyRelease;
import com.grillecube.client.opengl.GLFWListenerMouseScroll;
import com.grillecube.client.opengl.GLFWWindow;
import com.grillecube.common.maths.BoundingBox;
import com.grillecube.common.maths.Matrix4f;
import com.grillecube.common.maths.Vector3f;

public abstract class CameraProjective extends CameraView
		implements GLFWListenerKeyPress, GLFWListenerKeyRelease, GLFWListenerCursorPos, GLFWListenerMouseScroll {

	/** the window linked with this camera */
	private GLFWWindow _window;

	/** matrices */
	private Matrix4f _projection_matrix;
	private Matrix4f _mvp_matrix;

	/** camera picker */
	private CameraPicker _picker;

	/** max render distance */
	private float _render_distance;
	private float _render_distance_squared;

	/** aspect */
	private float _aspect;

	public CameraProjective(GLFWWindow window) {
		super();
		this.setWindow(window);
		this._picker = new CameraPicker(this);
		this._projection_matrix = new Matrix4f();
		this._mvp_matrix = new Matrix4f();
	}

	@Override
	public void update() {
		super.update();

		this.createProjectionMatrix(this.getProjectionMatrix());
		Matrix4f.mul(this.getProjectionMatrix(), this.getViewMatrix(), this.getMVPMatrix());

		if (this.getWindow() != null) {
			this.setAspect(this.getWindow().getAspectRatio());
			this._picker.update();
		}		
	}
	
	public void removeWindowListener() {
		this.getWindow().removeCursorPosListener(this);
		this.getWindow().removeKeyPressListener(this);
		this.getWindow().removeKeyReleaseListener(this);
		this.getWindow().removeMouseScrollListener(this);
	}

	/** create the projection matrix for this camera */
	protected abstract void createProjectionMatrix(Matrix4f dst);

	public void setProjectionMatrix(Matrix4f matrix) {
		this._projection_matrix.set(matrix);
	}

	public void setMVPMatrix(Matrix4f matrix) {
		this._mvp_matrix.set(matrix);
	}

	public Matrix4f getProjectionMatrix() {
		return (this._projection_matrix);
	}

	public Matrix4f getMVPMatrix() {
		return (this._mvp_matrix);
	}

	public CameraPicker getPicker() {
		return (this._picker);
	}

	public GLFWWindow getWindow() {
		return (this._window);
	}

	public void setWindow(GLFWWindow window) {
		this._window = window;
		if (window != null) {
			window.addCursorPosListener(this);
			window.addKeyPressListener(this);
			window.addKeyReleaseListener(this);
			window.addMouseScrollListener(this);
		}
	}
	
	public void setAspect(float aspect) {
		this._aspect = aspect;
	}

	public float getAspect() {
		return (this._aspect);
	}

	public float getRenderDistance() {
		return (this._render_distance);
	}

	public float getSquaredRenderDistance() {
		return (this._render_distance_squared);
	}

	public void setRenderDistance(float dist) {
		this._render_distance = dist;
		this._render_distance_squared = dist * dist;
	}

	@Override
	public void invokeCursorPos(GLFWWindow window, double xpos, double ypos) {}

	@Override
	public void invokeKeyRelease(GLFWWindow glfwWindow, int key, int scancode, int mods) {}

	@Override
	public void invokeKeyPress(GLFWWindow glfwWindow, int key, int scancode, int mods) {}

	@Override
	public void invokeMouseScroll(GLFWWindow window, double xpos, double ypos) {}
	
	/** return true if this point is in this camera frustum */
	public abstract boolean isPointInFrustum(float x, float y, float z);
		
	/** return true if this point is in this camera frustum */
	public boolean isPointInFrustum(Vector3f point) {
		return (this.isPointInFrustum(point.x, point.y, point.z));
	}
	
	/** return true if this box is in this camera frustum */
	public abstract boolean isBoxInFrustum(float x, float y, float z, float sizex, float sizey, float sizez);

	/** return true if this box is in this camera frustum */
	public boolean isBoxInFrustum(BoundingBox box) {
		return (this.isBoxInFrustum(box.getMin(), box.getSize()));
	}
	
	/** return true if this box is in this camera frustum */
	public boolean isBoxInFrustum(Vector3f min, Vector3f size) {
		return (this.isBoxInFrustum(min.x, min.y, min.z, size.x, size.y, size.z));
	}
	
	/** return true if this box is in this camera frustum */
	public boolean isBoxInFrustum(Vector3f min, float sx, float sy, float sz) {
		return (this.isBoxInFrustum(min.x, min.y, min.z, sx, sy, sz));
	}
	
	/** return true if this box is in this camera frustum */
	public boolean isBoxInFrustum(Vector3f min, float size) {
		return (this.isBoxInFrustum(min.x, min.y, min.z, size, size, size));
	}
	
	/** return true if this box is in this camera frustum */
	public boolean isBoxInFrustum(float x, float y, float z, Vector3f size) {
		return (this.isBoxInFrustum(x, y, z, size.x, size.y, size.z));
	}
	
	/** return true if the sphere is in this camera frustum */
	public abstract boolean isSphereInFrustum(Vector3f center, float radius);
}
