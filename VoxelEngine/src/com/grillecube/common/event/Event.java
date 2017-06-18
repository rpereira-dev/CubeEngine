package com.grillecube.common.event;

public abstract class Event {

	public String getName() {
		return (this.getClass().getSimpleName());
	}
}
