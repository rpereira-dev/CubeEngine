package com.grillecube.engine.resources;

import com.grillecube.engine.VoxelEngineServer;

public class ResourceManagerServer extends ResourceManager {
	public ResourceManagerServer(VoxelEngineServer engine) {
		super(engine);
	}

	@Override
	public void update() {
		for (GenericManager<?> manager : super._managers) {
			manager.updateServerSide();
		}
	}
}
