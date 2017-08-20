package com.grillecube.client.renderer.world.factories;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import com.grillecube.client.event.world.EventEntityList;
import com.grillecube.client.renderer.model.Model;
import com.grillecube.client.renderer.model.instance.ModelInstance;
import com.grillecube.client.renderer.world.WorldRenderer;
import com.grillecube.common.world.entity.Entity;

/** a factory class which create model renderer lists */
public class ModelRendererFactory extends WorldRendererFactory {

	/** the entity in frustum list */
	private HashMap<Model, ArrayList<ModelInstance>> renderingList;

	/** event */
	private EventEntityList eventEntityList;

	public ModelRendererFactory(WorldRenderer worldRenderer) {
		super(worldRenderer);
		this.renderingList = new HashMap<Model, ArrayList<ModelInstance>>();
		this.eventEntityList = new EventEntityList(this);
	}

	@Override
	public final void update() {

		this.renderingList.clear();

		// for each entity of the world (maybe we can do better here?)
		Collection<Entity> entities = this.getWorld().getEntityStorage().getEntities();
		for (Entity entity : entities) {

			ModelInstance modelInstance = super.getResourceManager().getModelManager().getModelInstance(entity);
			if (modelInstance == null || modelInstance.getEntity().isVisible()) {
				continue;
			}
			modelInstance.update();

			// if entities is not too far and in frustum, then add it to the
			// list
			// if (Vector3f.distance(entity.getPosition(), camera.getPosition())
			// > camera.getRenderDistance()
			// || !camera.isBoxInFrustum(entity.getBoundingBox())) {
			// continue;
			// }

			ArrayList<ModelInstance> instances = this.renderingList.get(modelInstance.getModel());
			if (instances == null) {
				instances = new ArrayList<ModelInstance>(1);
				this.renderingList.put(modelInstance.getModel(), instances);
			}
			// VoxelEngineClient.instance().getRenderer().getWorldRenderer().getLineRenderer()
			// .addBox(modelInstance.getEntity().getBoundingBox());
			instances.add(modelInstance);
		}

		super.getResourceManager().getEventManager().invokeEvent(this.eventEntityList);
	}

	@Override
	public void render() {
		this.getMainRenderer().getModelRenderer().render(this.getCamera(), this.renderingList);
	}

	/** get the last calculated entities in frustum */
	public HashMap<Model, ArrayList<ModelInstance>> getRenderingList() {
		return (this.renderingList);
	}
}