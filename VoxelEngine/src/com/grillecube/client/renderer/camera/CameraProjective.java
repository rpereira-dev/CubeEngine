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
	private GLFWWindow window;

	/** matrices */
	private Matrix4f projectionMatrix;
	private Matrix4f mvpMatrix;

	/** camera picker */
	private CameraPicker cameraPicker;

	/** max render distance */
	private float renderDistance;
	private float renderDistanceSquared;

	/** aspect */
	private float aspect;

	public CameraProjective(GLFWWindow window) {
		super();
		this.setWindow(window);
		this.cameraPicker = new CameraPicker(this);
		this.projectionMatrix = new Matrix4f();
		this.mvpMatrix = new Matrix4f();
		this.setAspect(16 / 9.0f);
	}

	@Override
	public void update() {
		super.update();

		this.createProjectionMatrix(this.getProjectionMatrix());
		Matrix4f.mul(this.getProjectionMatrix(), this.getViewMatrix(), this.getMVPMatrix());
		this.cameraPicker.update();
	}

	public final void addWindowListeners() {
		this.getWindow().addListener((GLFWListenerKeyPress) this);
		this.getWindow().addListener((GLFWListenerKeyRelease) this);
		this.getWindow().addListener((GLFWListenerCursorPos) this);
		this.getWindow().addListener((GLFWListenerMouseScroll) this);
	}

	public void removeWindowListeners() {
		this.getWindow().removeListener((GLFWListenerKeyPress) this);
		this.getWindow().removeListener((GLFWListenerKeyRelease) this);
		this.getWindow().removeListener((GLFWListenerCursorPos) this);
		this.getWindow().removeListener((GLFWListenerMouseScroll) this);
	}

	/** create the projection matrix for this camera */
	protected abstract void createProjectionMatrix(Matrix4f dst);

	public void setProjectionMatrix(Matrix4f matrix) {
		this.projectionMatrix.set(matrix);
	}

	public void setMVPMatrix(Matrix4f matrix) {
		this.mvpMatrix.set(matrix);
	}

	public Matrix4f getProjectionMatrix() {
		return (this.projectionMatrix);
	}

	public Matrix4f getMVPMatrix() {
		return (this.mvpMatrix);
	}

	public CameraPicker getPicker() {
		return (this.cameraPicker);
	}

	public GLFWWindow getWindow() {
		return (this.window);
	}

	public void setWindow(GLFWWindow window) {
		if (this.window != null) {
			this.removeWindowListeners();
		}
		this.window = window;
		if (window != null) {
			this.addWindowListeners();
		}
	}

	public void setAspect(float aspect) {
		this.aspect = aspect;
	}

	public float getAspect() {
		return (this.aspect);
	}

	public float getRenderDistance() {
		return (this.renderDistance);
	}

	public float getSquaredRenderDistance() {
		return (this.renderDistanceSquared);
	}

	public void setRenderDistance(float dist) {
		this.renderDistance = dist;
		this.renderDistanceSquared = dist * dist;
	}

	@Override
	public void invokeCursorPos(GLFWWindow window, double xpos, double ypos) {
	}

	@Override
	public void invokeKeyRelease(GLFWWindow glfwWindow, int key, int scancode, int mods) {
	}

	@Override
	public void invokeKeyPress(GLFWWindow glfwWindow, int key, int scancode, int mods) {
	}

	@Override
	public void invokeMouseScroll(GLFWWindow window, double xpos, double ypos) {
	}

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
