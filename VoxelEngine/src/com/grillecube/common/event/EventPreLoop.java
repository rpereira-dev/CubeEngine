package com.grillecube.common.event;

/** an event which is called right before the main loop ends */
public class EventPreLoop extends Event {
	@Override
	public String getName() {
		return (this.getClass().getSimpleName());
	}

}
