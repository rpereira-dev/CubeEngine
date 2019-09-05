package com.grillecube.client.renderer.world;

import com.grillecube.client.event.renderer.model.EventModelInstanceAdded;
import com.grillecube.client.event.renderer.model.EventModelInstanceRemoved;
import com.grillecube.client.renderer.MainRenderer;
import com.grillecube.client.renderer.model.ModelRendererFactory;
import com.grillecube.client.renderer.sky.SkyRendererFactory;
import com.grillecube.common.event.Listener;
import com.grillecube.common.resources.EventManager;
import com.grillecube.common.world.WorldFlat;

public class WorldFlatRenderer extends WorldRenderer<WorldFlat> {

	private SkyRendererFactory skyFactory;
	private TerrainRendererFactory terrainFactory;
	private final ModelRendererFactory modelFactory;

	private Listener<EventModelInstanceAdded> modelInstanceAddCallback;
	private Listener<EventModelInstanceRemoved> modelInstanceRemoveCallback;

	public WorldFlatRenderer(MainRenderer mainRenderer) {
		super(mainRenderer);
		this.skyFactory = new SkyRendererFactory(mainRenderer);
		this.terrainFactory = new TerrainRendererFactory(mainRenderer);
		this.modelFactory = new ModelRendererFactory(mainRenderer);

		super.addFactory(this.skyFactory, 0);
		super.addFactory(this.modelFactory, 1);
		// line renderer here
		super.addFactory(this.terrainFactory, 3);
		// particle renderer here

		this.modelInstanceAddCallback = new Listener<EventModelInstanceAdded>() {
			@Override
			public void pre(EventModelInstanceAdded event) {
			}

			@Override
			public void post(EventModelInstanceAdded event) {
				if (event.getModelInstance().getEntity().getWorld() == getWorld()) {
					modelFactory.addModelInstance(event.getModelInstance());
				}
			}
		};
		this.modelInstanceRemoveCallback = new Listener<EventModelInstanceRemoved>() {

			@Override
			public void pre(EventModelInstanceRemoved event) {
			}

			@Override
			public void post(EventModelInstanceRemoved event) {
				if (event.getModelInstance().getEntity().getWorld() == getWorld()) {
					modelFactory.removeModelInstance(event.getModelInstance());
				}

			}
		};

		// callback to add every model instances to the renderer, if they are in
		// the correct world
		EventManager eventManager = this.getMainRenderer().getResourceManager().getEventManager();

		eventManager.addListener(this.modelInstanceAddCallback);
		eventManager.addListener(this.modelInstanceRemoveCallback);
	}

	@Override
	protected void onInitialized() {
		super.onInitialized();
	}

	@Override
	protected void onDeinitialized() {
		super.onDeinitialized();

		EventManager eventManager = this.getMainRenderer().getResourceManager().getEventManager();
		eventManager.removeListener(this.modelInstanceAddCallback);
		eventManager.removeListener(this.modelInstanceRemoveCallback);
	}

	@Override
	protected final void onWorldSet() {
		this.terrainFactory.setWorld(super.getWorld());
		this.skyFactory.setSky(super.getWorld().getSky());
		this.modelFactory.clear();
		this.modelFactory.loadWorldModelInstance(super.getWorld());
	}

	@Override
	protected final void onCameraSet() {
		this.terrainFactory.setCamera(super.getCamera());
		this.skyFactory.setCamera(super.getCamera());
		this.modelFactory.setCamera(super.getCamera());
	}

	public final SkyRendererFactory getSkyRendererFactory() {
		return (this.skyFactory);
	}

	public final TerrainRendererFactory getTerrainRendererFactory() {
		return (this.terrainFactory);
	}

	public final ModelRendererFactory getModelRendererFactory() {
		return (this.modelFactory);
	}
}
