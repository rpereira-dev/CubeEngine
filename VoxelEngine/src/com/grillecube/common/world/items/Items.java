package com.grillecube.common.world.items;

import com.grillecube.client.renderer.model.Model;
import com.grillecube.common.mod.IModResource;
import com.grillecube.common.mod.Mod;
import com.grillecube.common.resources.ItemManager;
import com.grillecube.common.resources.ModelManager;
import com.grillecube.common.resources.ResourceManager;

public class Items implements IModResource {

	// default items
	public static Item STICK;

	// texture id
	public static int T_STICK;

	// item models
	public static Model M_STICK;

	@Override
	public void load(Mod mod, ResourceManager manager) {
		this.loadTextures(manager.getItemManager());
		this.loadModels(manager.getModelManager());
		this.loadItems(manager.getItemManager());
	}

	private void loadTextures(ItemManager manager) {
	}

	private void loadModels(ModelManager manager) {
	}

	private void loadItems(ItemManager manager) {
	}

	@Override
	public void unload(Mod mod, ResourceManager manager) {
	}

	public static Item getItemByID(short itemID) {
		return (ResourceManager.instance().getItemManager().getItemByID(itemID));
	}
}