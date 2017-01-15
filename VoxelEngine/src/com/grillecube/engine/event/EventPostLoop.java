package com.grillecube.engine.event;

/** an event which is called right after the main loop ends */
public class EventPostLoop extends Event {
	@Override
	public String getName() {
		return (this.getClass().getSimpleName());
	}

}
