package com.grillecube.client.defaultmod;

import com.grillecube.client.renderer.event.EventPostRender;
import com.grillecube.client.renderer.event.EventPostRendererInitialisation;
import com.grillecube.client.renderer.event.EventPreRender;
import com.grillecube.common.mod.IModResource;
import com.grillecube.common.mod.Mod;
import com.grillecube.common.resources.EventManager;
import com.grillecube.common.resources.ResourceManager;

public class ClientEvents implements IModResource {

	@Override
	public void load(Mod mod, ResourceManager manager) {
		// default events
		EventManager eventManager = manager.getEventManager();
		eventManager.registerEvent(EventPostRender.class);
		eventManager.registerEvent(EventPreRender.class);
		eventManager.registerEvent(EventPostRendererInitialisation.class);
	}

	@Override
	public void unload(Mod mod, ResourceManager manager) {
	}
}
