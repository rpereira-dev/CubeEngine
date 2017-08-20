package com.pot.common.world;

import com.grillecube.common.mod.IModResource;
import com.grillecube.common.mod.Mod;
import com.grillecube.common.resources.ResourceManager;

public class POTWorlds implements IModResource {
	/** default world id */
	public static int DEFAULT;

	@Override
	public void load(Mod mod, ResourceManager manager) {
		DEFAULT = manager.getWorldManager().registerWorld(new WorldDefault());
	}

	@Override
	public void unload(Mod mod, ResourceManager manager) {
	}

}
