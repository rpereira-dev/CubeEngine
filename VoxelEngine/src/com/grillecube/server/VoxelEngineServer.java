package com.grillecube.server;

import com.grillecube.common.VoxelEngine;
import com.grillecube.common.resources.ResourceManager;
import com.grillecube.server.resources.ResourceManagerServer;

public class VoxelEngineServer extends VoxelEngine {

	public VoxelEngineServer() {
		super(Side.SERVER);
	}

	@Override
	protected ResourceManager instanciateResourceManager() {
		return (new ResourceManagerServer(this));
	}

	@Override
	protected void onInitialized() {
	}

	@Override
	protected void onDeinitialized() {
	}

	@SuppressWarnings("unchecked")
	@Override
	public ResourceManager getResourceManager() {
		return (super.resources);
	}
}
