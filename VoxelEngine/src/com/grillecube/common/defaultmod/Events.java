package com.grillecube.common.defaultmod;

import com.grillecube.common.event.EventPostLoop;
import com.grillecube.common.event.EventPreLoop;
import com.grillecube.common.event.world.EventEntityJump;
import com.grillecube.common.event.world.EventEntitySpawn;
import com.grillecube.common.event.world.EventWorldDespawnTerrain;
import com.grillecube.common.event.world.EventWorldSpawnTerrain;
import com.grillecube.common.mod.IModResource;
import com.grillecube.common.mod.Mod;
import com.grillecube.common.resources.EventManager;
import com.grillecube.common.resources.ResourceManager;

public class Events implements IModResource {

	@Override
	public void load(Mod mod, ResourceManager manager) {
		// default events
		EventManager eventManager = manager.getEventManager();
		eventManager.registerEvent(EventEntityJump.class);
		eventManager.registerEvent(EventEntitySpawn.class);
		eventManager.registerEvent(EventWorldSpawnTerrain.class);
		eventManager.registerEvent(EventWorldDespawnTerrain.class);
		eventManager.registerEvent(EventPreLoop.class);
		eventManager.registerEvent(EventPostLoop.class);
	}

	@Override
	public void unload(Mod mod, ResourceManager manager) {
	}
}
