package com.grillecube.common.world;

public class Timer {
	private static final double NANO_TO_SECOND = 1000000000;
	private double time;
	private long lastCheck;

	public Timer() {
		this.restart();
	}

	/** update the timer value */
	public void update() {
		long t = System.nanoTime();
		this.time += (t - this.lastCheck) / NANO_TO_SECOND;
		this.lastCheck = t;
	}

	/** return the time for this timer. 1.0d is 1 second, 3.5d is 3s500ms */
	public double getTime() {
		return (this.time);
	}

	@Override
	public String toString() {
		return ("Timer: " + this.getTime());
	}

	public void restart() {
		this.time = 0;
		this.lastCheck = System.nanoTime();
	}
}
