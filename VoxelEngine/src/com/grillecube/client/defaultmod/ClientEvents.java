package com.grillecube.client.defaultmod;

import com.grillecube.client.event.renderer.EventPostRender;
import com.grillecube.client.event.renderer.EventPostRendererInitialisation;
import com.grillecube.client.event.renderer.EventPreRender;
import com.grillecube.client.event.renderer.EventTerrainList;
import com.grillecube.client.event.world.EventEntityList;
import com.grillecube.common.mod.IModResource;
import com.grillecube.common.mod.Mod;
import com.grillecube.common.resources.EventManager;
import com.grillecube.common.resources.ResourceManager;

public class ClientEvents implements IModResource {

	@Override
	public void load(Mod mod, ResourceManager manager) {
		// default events
		EventManager eventManager = manager.getEventManager();
		
		//rendering events
		eventManager.registerEvent(EventPostRender.class);
		eventManager.registerEvent(EventPreRender.class);
		eventManager.registerEvent(EventPostRendererInitialisation.class);
		eventManager.registerEvent(EventEntityList.class);
		eventManager.registerEvent(EventTerrainList.class);

	}

	@Override
	public void unload(Mod mod, ResourceManager manager) {
	}
}
