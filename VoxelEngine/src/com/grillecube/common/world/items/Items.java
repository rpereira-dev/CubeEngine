package com.grillecube.common.world.items;

import com.grillecube.common.mod.IModResource;
import com.grillecube.common.mod.Mod;
import com.grillecube.common.resources.ResourceManager;

public class Items implements IModResource {

	@Override
	public void load(Mod mod, ResourceManager manager) {
	}

	@Override
	public void unload(Mod mod, ResourceManager manager) {
	}

	public static Item getItemByID(short itemID) {
		return (ResourceManager.instance().getItemManager().getItemByID(itemID));
	}
}