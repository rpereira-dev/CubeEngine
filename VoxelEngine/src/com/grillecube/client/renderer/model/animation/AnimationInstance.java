package com.grillecube.client.renderer.model.animation;

public class AnimationInstance {

	/** states */
	private static final int PLAYING = 0;
	private static final int LOOPING = 1;
	private static final int FREEZING = 2;
	private static final int STOPPED = 3;

	/** the state */
	private int state;

	/** the animation we are instancing */
	private final Animation animation;

	/** timer which goes from a frame timer to another */
	private int timer;
	private long lastMillis;

	public AnimationInstance(Animation animation) {
		this.animation = animation;
		this.setTimer(0);
		this.stop();
	}

	public Animation getAnimation() {
		return (this.animation);
	}

	public int getTimer() {
		return (this.timer);
	}

	public void update() {
		// update the timer
		this.updateTimer();
	}

	private void updateTimer() {
		long now = System.currentTimeMillis();

		if (!this.isFreezing() && !this.isStopped()) {
			int duration = this.getAnimation().getDuration();
			if (this.getTimer() <= duration) {
				this.timer += (now - this.lastMillis);
			} else {
				if (this.isLooping()) {
					this.startLoop(this.getTimer() % duration);
				} else {
					this.stopLoop();
				}
			}
		}
		this.lastMillis = now;
	}

	public boolean isPlaying() {
		return (this.state == PLAYING);
	}

	public boolean isFreezing() {
		return (this.state == FREEZING);
	}

	public boolean isStopped() {
		return (this.state == STOPPED);
	}

	public boolean isLooping() {
		return (this.state == LOOPING);
	}

	public void startLoop(int timer) {
		this.setTimer(timer);
		this.loop();
	}

	public void startLoop() {
		this.startLoop(0);
	}

	public void loop() {
		this.state = LOOPING;
	}

	public void play() {
		this.state = PLAYING;
	}

	public void freeze() {
		this.state = FREEZING;
	}

	public void stop() {
		this.state = STOPPED;
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

	/** timer is in millis */
	public void setTimer(int time) {
		this.timer = time;
		this.lastMillis = System.currentTimeMillis();
	}
}
