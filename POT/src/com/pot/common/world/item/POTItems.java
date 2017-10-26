package com.pot.common.world.item;

import com.grillecube.client.renderer.model.Model;
import com.grillecube.common.mod.IModResource;
import com.grillecube.common.mod.Mod;
import com.grillecube.common.resources.ResourceManager;
import com.grillecube.common.world.items.Item;

public class POTItems implements IModResource {
	// items
	public static Item I_STICK;

	// items model
	public static Model M_STICK;

	// items textures id
	public static int T_STICK;

	@Override
	public void load(Mod mod, ResourceManager manager) {

	}

	@Override
	public void unload(Mod mod, ResourceManager manager) {
	}

}
