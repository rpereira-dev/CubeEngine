package com.grillecube.common.event;

/** an event which is called right after the main loop ends */
public class EventGetTasks extends Event {
	@Override
	public String getName() {
		return (this.getClass().getSimpleName());
	}

}
