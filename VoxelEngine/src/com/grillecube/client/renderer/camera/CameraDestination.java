package com.grillecube.client.renderer.camera;

public abstract class CameraDestination<T extends Camera> {

	private final float duration;
	private float accumulator;

	public CameraDestination(float duration) {
		this.duration = duration;
	}

	public final void init(T camera) {
		this.accumulator = 0;
		this.onInit(camera);
	}

	protected abstract void onInit(T camera);

	/**
	 * 
	 * @param cameraView
	 * @param dt
	 * @return true if the destination is reached
	 */
	public boolean update(T camera, float dt) {
		this.accumulator += dt;
		if (this.accumulator >= this.duration) {
			this.onReached(camera);
			return (true);
		}
		this.onUpdate(camera, this.accumulator / this.duration);
		return (false);
	}

	/** a callback on destination update (ratio is in [0, 1] */
	protected abstract void onUpdate(T camera, float ratio);

	/** a callback whenever the destination is reached */
	protected abstract void onReached(T camera);
}
