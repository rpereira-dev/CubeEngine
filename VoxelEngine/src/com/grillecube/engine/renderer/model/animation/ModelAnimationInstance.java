package com.grillecube.engine.renderer.model.animation;

public class ModelAnimationInstance {

	/** the animation we are instancing */
	private final ModelAnimation _animation;

	/** part instances */
	private ModelPartAnimationInstance[] _parts_instances;

	/** timer which goes from a frame timer to another */
	private int _timer;
	private long _last_millis;

	private static final int PLAYING = 0;
	private static final int LOOPING = 1;
	private static final int FREEZING = 2;
	private static final int STOPPED = 3;
	private int _state;

	public ModelAnimationInstance(ModelAnimation animation) {
		this.set(animation);
		this._animation = animation;
		this.setTimer(0);
		this.stop();
	}

	public ModelAnimation getAnimation() {
		return (this._animation);
	}

	private void set(ModelAnimation animation) {

		int partcount = animation.getAnimationPartCount();

		// prepare each part instances
		this._parts_instances = new ModelPartAnimationInstance[partcount];
		for (int i = 0; i < this._parts_instances.length; i++) {
			this._parts_instances[i] = new ModelPartAnimationInstance(animation.getAnimationPart(i));
		}
	}

	public int getTimer() {
		return (this._timer);
	}

	public void update() {

		// update the timer
		this.updateTimer();

		// update parts
		this.updateParts();
	}

	private void updateParts() {

		if (this.isStopped()) {
			return;
		}

		for (ModelPartAnimationInstance instance : this._parts_instances) {
			instance.update(this._timer);
		}
	}

	private void updateTimer() {
		long now = System.currentTimeMillis();

		if (!this.isFreezing() && !this.isStopped()) {
			int duration = this.getAnimation().getDuration();
			if (this.getTimer() <= duration) {
				this._timer += (now - this._last_millis);
			} else {
				if (this.isLooping()) {
					this.startLoop(this.getTimer() % duration);
				} else {
					this.stopLoop();
				}
			}
		}
		this._last_millis = now;
	}

	public boolean isPlaying() {
		return (this._state == PLAYING);
	}

	public boolean isFreezing() {
		return (this._state == FREEZING);
	}

	public boolean isStopped() {
		return (this._state == STOPPED);
	}

	public boolean isLooping() {
		return (this._state == LOOPING);
	}

	public void startLoop(int timer) {
		this.setTimer(timer);
		this.loop();
	}

	public void startLoop() {
		this.startLoop(0);
	}

	public void loop() {
		this._state = LOOPING;
	}

	public void play() {
		this._state = PLAYING;
	}

	public void freeze() {
		this._state = FREEZING;
	}

	public void stop() {
		this._state = STOPPED;
	}

	public void startFreeze(int timer) {
		this.setTimer(timer);
		this.freeze();
	}

	public void stopFreeze() {
		this.startPlay(this.getTimer());
	}

	public void startFreeze() {
		this.startFreeze(this.getTimer());
	}

	public void stopLoop() {
		this.stop();
	}

	public void startPlay(int timer) {
		this.play();
		this.setTimer(timer);
	}

	public void startPlay() {
		this.startPlay(0);
	}

	public void stopPlay() {
		this.stop();
	}

	public ModelPartAnimationInstance getPart(int partID) {
		if (partID < 0 || partID >= this._parts_instances.length) {
			return (null);
		}
		return (this._parts_instances[partID]);
	}

	/** timer is in millis */
	public void setTimer(int time) {
		this._timer = time;
		this._last_millis = System.currentTimeMillis();
	}
}
