package com.grillecube.common.event;

import com.grillecube.common.VoxelEngine;

/** an event which is called during the main game loop */
public class EventOnLoop extends EventEngine {

	public EventOnLoop(VoxelEngine engine) {
		super(engine);
	}
}