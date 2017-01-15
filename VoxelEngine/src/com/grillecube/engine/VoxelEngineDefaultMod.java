package com.grillecube.engine;

import com.grillecube.engine.event.Events;
import com.grillecube.engine.mod.IMod;
import com.grillecube.engine.mod.Mod;
import com.grillecube.engine.mod.ModInfo;
import com.grillecube.engine.resources.AssetsPack;
import com.grillecube.engine.world.block.Blocks;

@ModInfo(name = "Default Voxel engine mod package", author = "toss-dev", version = "1.0.0.a")
public class VoxelEngineDefaultMod implements IMod {
	
	@Override
	public void initialize(Mod mod) {
		String assets = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath() + "../assets.zip";
		Logger.get().log(Logger.Level.DEBUG, "Default assets path", assets);
		mod.addRessource(new AssetsPack("VoxelEngine", assets));
		mod.addRessource(new Blocks());
		mod.addRessource(new Events());
	}

	@Override
	public void deinitialize(Mod mod) {
	}

}
