package com.grillecube.common.defaultmod;

import com.grillecube.common.mod.IMod;
import com.grillecube.common.mod.Mod;
import com.grillecube.common.mod.ModInfo;
import com.grillecube.common.world.block.Blocks;

@ModInfo(name = "Default Voxel engine mod package", author = "toss-dev", version = "1.0.0.a", clientProxy = "com.grillecube.client.defaultmod.VoxelEngineDefaultModClient")
public class VoxelEngineDefaultMod implements IMod {

	@Override
	public void initialize(Mod mod) {
		mod.addResource(new Blocks());
	}

	@Override
	public void deinitialize(Mod mod) {
	}
}
