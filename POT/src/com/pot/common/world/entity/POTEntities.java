package com.pot.common.world.entity;

import com.grillecube.common.mod.IModResource;
import com.grillecube.common.mod.Mod;
import com.grillecube.common.resources.ResourceManager;

public class POTEntities implements IModResource {

	// entities id
	public static int TEST;
	public static int BIPED_TEST;

	@Override
	public void load(Mod mod, ResourceManager manager) {
		TEST = manager.getEntityManager().registerEntity(EntityTest.class);
		BIPED_TEST = manager.getEntityManager().registerEntity(EntityBipedTest.class);
	}

	@Override
	public void unload(Mod mod, ResourceManager manager) {
	}

}
