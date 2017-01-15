package com.grillecube.engine.world;

public class Timer {
	private static final double NANO_TO_SECOND = 1000000000;
	private double _time;
	private long _last_check;

	public Timer() {
		this.restart();
	}

	/** update the timer value */
	public void update() {
		long t = System.nanoTime();
		this._time += (t - this._last_check) / NANO_TO_SECOND;
		this._last_check = t;
	}

	/** return the time for this timer. 1.0d is 1 second, 3.5d is 3s500ms */
	public double getTime() {
		return (this._time);
	}

	@Override
	public String toString() {
		return ("Timer: " + this.getTime());
	}
	
	public void restart() {
		this._time = 0;
		this._last_check = System.nanoTime();
	}
}
