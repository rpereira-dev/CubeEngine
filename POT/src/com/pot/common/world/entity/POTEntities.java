package com.pot.common.world.entity;

import com.grillecube.common.mod.IModResource;
import com.grillecube.common.mod.Mod;
import com.grillecube.common.resources.ResourceManager;

public class POTEntities implements IModResource {

	// entities id
	public static int PLANT;
	public static int BIPED_TEST;

	@Override
	public void load(Mod mod, ResourceManager manager) {
		PLANT = manager.getEntityManager().registerEntity(EntityPlant.class);
		BIPED_TEST = manager.getEntityManager().registerEntity(WorldEntityBipedTest.class);
	}

	@Override
	public void unload(Mod mod, ResourceManager manager) {
	}

}
