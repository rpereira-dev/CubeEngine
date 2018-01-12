package com.grillecube.client.renderer.camera;

import com.grillecube.client.opengl.window.GLFWWindow;
import com.grillecube.common.maths.Maths;
import com.grillecube.common.maths.Vector3f;

/** a camera which follow the given entity, 3rd perso view */
public class CameraPerspectiveWorldCentered extends CameraPerspectiveWorld {

	private Vector3f center;
	private float r;
	private float theta;
	private float phi;

	public CameraPerspectiveWorldCentered(GLFWWindow window) {
		super(window);
		this.center = new Vector3f();
		this.r = 16;
		this.phi = 0;
		this.theta = 0;
	}

	@Override
	public void update() {
		super.update();
		this.calculateCameraPosition();
	}

	public final void setCenter(Vector3f center) {
		this.center.set(center);
	}

	public final void setCenter(float x, float y, float z) {
		this.center.set(x, y, z);
	}

	public final float getTheta() {
		return (this.theta);
	}

	public final void setTheta(float x) {
		this.theta = x;
	}

	public final void increaseTheta(float x) {
		this.theta += x;
	}

	public final float getPhi() {
		return (this.phi);
	}

	public final void setPhi(float y) {
		this.phi = y;
	}

	public final void increasePhi(float y) {
		this.phi += y;
	}

	public final void setR(float r) {
		this.r = r;
	}

	public final float getR() {
		return (this.r);
	}

	public final void increaseR(float dr) {
		this.r += dr;
	}

	public final Vector3f getCenter() {
		return (this.center);
	}

	/**
	 * calculate camera world position depending on r, theta and phi (which are
	 * relative to the center)
	 */
	private final void calculateCameraPosition() {
		this.setCenter(0, 0, 0);

		double h = (this.r * Math.cos(this.getTheta()));
		double v = (this.r * Math.sin(this.getTheta()));

		float offx = (float) (h * Math.sin(this.getPhi()));
		float offy = (float) (h * Math.cos(this.getPhi()));

		float x = this.center.x + offx;
		float y = this.center.y + offy;
		float z = (float) (center.z + v);
		super.setPosition(x, y, z);

		// this.setRotX(this.theta + Maths.PI);
		// this.setRotY(0);
		// this.setRotZ(this.phi + Maths.PI);

		this.setRotX(this.theta - Maths.PI_2);
		this.setRotY(0);
		this.setRotZ(this.phi - Maths.PI);
	}
}
