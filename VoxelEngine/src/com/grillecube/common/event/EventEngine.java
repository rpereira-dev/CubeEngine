package com.grillecube.common.event;

import com.grillecube.common.VoxelEngine;

public abstract class EventEngine extends Event {
	private VoxelEngine engine;

	public EventEngine(VoxelEngine engine) {
		super();
		this.engine = engine;
	}

	public final VoxelEngine getEngine() {
		return (this.engine);
	}
}
