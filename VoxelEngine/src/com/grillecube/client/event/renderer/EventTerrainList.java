package com.grillecube.client.event.renderer;

import com.grillecube.client.renderer.world.factories.TerrainRendererFactory;
import com.grillecube.common.event.Event;

/**
 * an event which is called whenever a new terrain rendernig list is generated
 */
public class EventTerrainList extends Event {

	private final TerrainRendererFactory terrainRendererFactory;

	public EventTerrainList(TerrainRendererFactory terrainRendererFactory) {
		this.terrainRendererFactory = terrainRendererFactory;
	}

	public final TerrainRendererFactory getTerrainRendererFactory() {
		return (this.terrainRendererFactory);
	}

}
