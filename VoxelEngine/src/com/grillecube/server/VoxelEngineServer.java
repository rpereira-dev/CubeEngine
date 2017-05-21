package com.grillecube.server;

import java.util.ArrayList;

import com.grillecube.common.Taskable;
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
	protected void preLoaded() {
	}

	@Override
	protected void postLoaded() {
	}

	@Override
	protected void onLoopStart() {
	}

	@Override
	protected void onLoopEnd() {
	}

	@Override
	public void onLoop() {
	}

	@Override
	protected void onStopped() {
	}

	@SuppressWarnings("unchecked")
	@Override
	public ResourceManager getResourceManager() {
		return (super.resources);
	}

	@Override
	protected void getTasks(ArrayList<Callable<Taskable>> tasks) {
		// TODO Auto-generated method stub

	}
}
