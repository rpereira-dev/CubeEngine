package com.grillecube.common.event;

import com.grillecube.common.event.world.EventWorldDespawnTerrain;
import com.grillecube.common.event.world.EventWorldSpawnTerrain;
import com.grillecube.common.event.world.entity.EventEntityDespawn;
import com.grillecube.common.event.world.entity.EventEntityJump;
import com.grillecube.common.event.world.entity.EventEntitySpawn;
import com.grillecube.common.mod.IModResource;
import com.grillecube.common.mod.Mod;
import com.grillecube.common.resources.EventManager;
import com.grillecube.common.resources.ResourceManager;

public class Events implements IModResource {

	@Override
	public void load(Mod mod, ResourceManager manager) {
		// default events
		EventManager eventManager = manager.getEventManager();

		// entities event
		eventManager.registerEvent(EventEntityJump.class);
		eventManager.registerEvent(EventEntitySpawn.class);
		eventManager.registerEvent(EventEntityDespawn.class);

		// world event
		eventManager.registerEvent(EventWorldSpawnTerrain.class);
		eventManager.registerEvent(EventWorldDespawnTerrain.class);

		// engine events
		eventManager.registerEvent(EventGetTasks.class);
		eventManager.registerEvent(EventPostLoop.class);
		eventManager.registerEvent(EventPostLoop.class);
	}

	@Override
	public void unload(Mod mod, ResourceManager manager) {
	}
}
