package com.grillecube.common.defaultmod;

import com.grillecube.common.event.Events;
import com.grillecube.common.mod.IMod;
import com.grillecube.common.mod.Mod;
import com.grillecube.common.mod.ModInfo;
import com.grillecube.common.resources.AssetsPack;
import com.grillecube.common.world.block.Blocks;

@ModInfo(name = "Default Voxel engine mod package", author = "toss-dev", version = "1.0.0.a", clientProxy = "com.grillecube.client.defaultmod.VoxelEngineDefaultModClient")
public class VoxelEngineDefaultMod implements IMod {

	@Override
	public void initialize(Mod mod) {
		String assets = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath() + "../assets.zip";
		mod.addResource(new AssetsPack("VoxelEngine", assets));
		mod.addResource(new Blocks());
		mod.addResource(new Events());
	}

	@Override
	public void deinitialize(Mod mod) {
	}
}
