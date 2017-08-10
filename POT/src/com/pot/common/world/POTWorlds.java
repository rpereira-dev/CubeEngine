package com.pot.common.world;

import com.grillecube.common.mod.IModResource;
import com.grillecube.common.mod.Mod;
import com.grillecube.common.resources.ResourceManager;

public class POTWorlds implements IModResource {
	/** default world id */
	public static int DEFAULT;
	public static int MAIN_MENU;

	@Override
	public void load(Mod mod, ResourceManager manager) {
		MAIN_MENU = manager.getWorldManager().registerWorld(new ViewMainMenuWorld());
		DEFAULT = manager.getWorldManager().registerWorld(new WorldDefault());
	}

	@Override
	public void unload(Mod mod, ResourceManager manager) {
	}

}
