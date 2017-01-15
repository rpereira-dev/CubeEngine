package com.grillecube.engine.event;

import com.grillecube.engine.VoxelEngine.Side;
import com.grillecube.engine.event.renderer.EventPostRender;
import com.grillecube.engine.event.renderer.EventPostRendererInitialisation;
import com.grillecube.engine.event.renderer.EventPreRender;
import com.grillecube.engine.event.world.EventEntityJump;
import com.grillecube.engine.event.world.EventEntitySpawn;
import com.grillecube.engine.event.world.EventWorldDespawnTerrain;
import com.grillecube.engine.event.world.EventWorldSpawnTerrain;
import com.grillecube.engine.mod.Mod;
import com.grillecube.engine.resources.IModResource;
import com.grillecube.engine.resources.ResourceManager;

public class Events implements IModResource {

	@Override
	public void load(Mod mod, ResourceManager manager) {
		// default events
		manager.getEventManager().registerEvent(EventEntityJump.class, Side.BOTH);
		manager.getEventManager().registerEvent(EventEntitySpawn.class, Side.BOTH);
		manager.getEventManager().registerEvent(EventWorldSpawnTerrain.class, Side.BOTH);
		manager.getEventManager().registerEvent(EventWorldDespawnTerrain.class, Side.BOTH);
		manager.getEventManager().registerEvent(EventPostRender.class, Side.CLIENT);
		manager.getEventManager().registerEvent(EventPreRender.class, Side.CLIENT);
		manager.getEventManager().registerEvent(EventPostRendererInitialisation.class, Side.CLIENT);
		manager.getEventManager().registerEvent(EventPreLoop.class, Side.BOTH);
		manager.getEventManager().registerEvent(EventPostLoop.class, Side.BOTH);

	}

	@Override
	public void unload(Mod mod, ResourceManager manager) {
	}

}
