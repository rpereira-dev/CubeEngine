package com.grillecube.client.defaultmod;

import com.grillecube.client.renderer.event.ClientEvents;
import com.grillecube.common.defaultmod.VoxelEngineDefaultMod;
import com.grillecube.common.mod.Mod;

public class VoxelEngineDefaultModClient extends VoxelEngineDefaultMod {

	@Override
	public void initialize(Mod mod) {
		super.initialize(mod);
		mod.addResource(new ClientEvents());
	}

	@Override
	public void deinitialize(Mod mod) {
	}
}
