package com.grillecube.common.defaultmod;

import com.grillecube.common.event.EventPostLoop;
import com.grillecube.common.event.EventPreLoop;
import com.grillecube.common.mod.IModResource;
import com.grillecube.common.mod.Mod;
import com.grillecube.common.resources.EventManager;
import com.grillecube.common.resources.ResourceManager;
import com.grillecube.common.world.events.EventEntityDespawn;
import com.grillecube.common.world.events.EventEntityJump;
import com.grillecube.common.world.events.EventEntitySpawn;
import com.grillecube.common.world.events.EventWorldDespawnTerrain;
import com.grillecube.common.world.events.EventWorldSpawnTerrain;

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
		eventManager.registerEvent(EventPreLoop.class);
		eventManager.registerEvent(EventPostLoop.class);
	}

	@Override
	public void unload(Mod mod, ResourceManager manager) {
	}
}
