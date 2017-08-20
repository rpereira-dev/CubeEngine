package com.grillecube.common.event;

import com.grillecube.common.VoxelEngine;

/** an event which is called right before the main loop ends */
public class EventPreLoop extends EventEngine {

	public EventPreLoop(VoxelEngine engine) {
		super(engine);
	}
}