package com.grillecube.client.renderer.world.flat;

import com.grillecube.client.renderer.MainRenderer;
import com.grillecube.client.renderer.factories.SkyRendererFactory;
import com.grillecube.client.renderer.factories.TerrainRendererFactory;
import com.grillecube.client.renderer.world.WorldRenderer;
import com.grillecube.common.world.WorldFlat;

public class WorldFlatRenderer extends WorldRenderer<WorldFlat> {

	private SkyRendererFactory skyFactory;
	private TerrainRendererFactory terrainFactory;

	public WorldFlatRenderer(MainRenderer mainRenderer) {
		super(mainRenderer);
		this.skyFactory = new SkyRendererFactory(mainRenderer);
		this.terrainFactory = new TerrainRendererFactory(mainRenderer);
		super.addFactory(this.skyFactory, 0);
		super.addFactory(this.terrainFactory, 1);
	}

	@Override
	protected void onInitialized() {
		super.onInitialized();
	}

	@Override
	protected final void onWorldSet() {
		this.terrainFactory.setWorld(super.getWorld());
		this.skyFactory.setSky(super.getWorld().getSky());
	}

	@Override
	protected final void onCameraSet() {
		this.terrainFactory.setCamera(super.getCamera());
		this.skyFactory.setCamera(super.getCamera());
	}

	public final SkyRendererFactory getSkyRendererFactory() {
		return (this.skyFactory);
	}

	public final TerrainRendererFactory getTerrainRendererFactory() {
		return (this.terrainFactory);
	}
}
