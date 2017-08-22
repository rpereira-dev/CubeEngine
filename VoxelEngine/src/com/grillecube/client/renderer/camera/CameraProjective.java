package com.grillecube.client.renderer.camera;

import com.grillecube.client.opengl.window.GLFWWindow;
import com.grillecube.client.opengl.window.event.GLFWEventKeyPress;
import com.grillecube.client.opengl.window.event.GLFWEventKeyRelease;
import com.grillecube.client.opengl.window.event.GLFWEventMouseCursor;
import com.grillecube.client.opengl.window.event.GLFWEventMousePress;
import com.grillecube.client.opengl.window.event.GLFWEventMouseRelease;
import com.grillecube.client.opengl.window.event.GLFWEventMouseScroll;
import com.grillecube.client.opengl.window.event.GLFWListener;
import com.grillecube.common.maths.BoundingBox;
import com.grillecube.common.maths.Matrix4f;
import com.grillecube.common.maths.Vector3f;

public abstract class CameraProjective extends CameraView {

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

	private GLFWListener<GLFWEventKeyPress> keyPressListener;
	private GLFWListener<GLFWEventKeyRelease> keyReleaseListener;
	private GLFWListener<GLFWEventMouseCursor> cursorListener;
	private GLFWListener<GLFWEventMouseScroll> scrollListener;
	private GLFWListener<GLFWEventMousePress> mousePressListener;
	private GLFWListener<GLFWEventMouseRelease> mouseReleaseListener;

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
		this.keyPressListener = new GLFWListener<GLFWEventKeyPress>() {
			@Override
			public void invoke(GLFWEventKeyPress event) {
				invokeKeyPress(event.getWindow(), event.getKey(), event.getScancode(), event.getMods());
			}
		};

		this.keyReleaseListener = new GLFWListener<GLFWEventKeyRelease>() {
			@Override
			public void invoke(GLFWEventKeyRelease event) {
				invokeKeyRelease(event.getWindow(), event.getKey(), event.getScancode(), event.getMods());
			}
		};

		this.cursorListener = new GLFWListener<GLFWEventMouseCursor>() {
			@Override
			public void invoke(GLFWEventMouseCursor event) {
				invokeCursorPos(event.getWindow(), event.getMouseX(), event.getMouseY());
			}
		};

		this.scrollListener = new GLFWListener<GLFWEventMouseScroll>() {
			@Override
			public void invoke(GLFWEventMouseScroll event) {
				invokeMouseScroll(event.getWindow(), event.getScrollX(), event.getScrollY());
			}
		};

		this.mousePressListener = new GLFWListener<GLFWEventMousePress>() {
			@Override
			public void invoke(GLFWEventMousePress event) {
				invokeMousePress(event.getWindow(), event.getButton(), event.getMods());
			}
		};

		this.mouseReleaseListener = new GLFWListener<GLFWEventMouseRelease>() {
			@Override
			public void invoke(GLFWEventMouseRelease event) {
				invokeMouseRelease(event.getWindow(), event.getButton(), event.getMods());
			}
		};

		this.getWindow().addListener(this.keyPressListener);
		this.getWindow().addListener(this.keyReleaseListener);
		this.getWindow().addListener(this.cursorListener);
		this.getWindow().addListener(this.scrollListener);
		this.getWindow().addListener(this.mousePressListener);
		this.getWindow().addListener(this.mouseReleaseListener);
	}

	public void removeWindowListeners() {
		this.getWindow().removeListener(this.keyPressListener);
		this.getWindow().removeListener(this.keyReleaseListener);
		this.getWindow().removeListener(this.cursorListener);
		this.getWindow().removeListener(this.scrollListener);
		this.getWindow().removeListener(this.mousePressListener);
		this.getWindow().removeListener(this.mouseReleaseListener);
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

	public void invokeCursorPos(GLFWWindow window, double xpos, double ypos) {
	}

	public void invokeKeyRelease(GLFWWindow glfwWindow, int key, int scancode, int mods) {
	}

	public void invokeKeyPress(GLFWWindow glfwWindow, int key, int scancode, int mods) {
	}

	public void invokeMouseScroll(GLFWWindow window, double xpos, double ypos) {
	}

	public void invokeMouseRelease(GLFWWindow window, int button, int mods) {
	}

	public void invokeMousePress(GLFWWindow window, int button, int mods) {
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
