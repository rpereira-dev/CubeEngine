package com.grillecube.common.event;

import com.grillecube.common.VoxelEngine;

/** an event which is called right after the main loop ends */
public class EventPostLoop extends EventEngine {

	public EventPostLoop(VoxelEngine engine) {
		super(engine);
	}
}