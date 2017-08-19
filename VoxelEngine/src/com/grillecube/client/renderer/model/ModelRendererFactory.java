package com.grillecube.client.renderer.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import com.grillecube.client.VoxelEngineClient;
import com.grillecube.client.event.world.EventEntityList;
import com.grillecube.client.renderer.MainRenderer;
import com.grillecube.client.renderer.camera.CameraProjective;
import com.grillecube.client.renderer.model.instance.ModelInstance;
import com.grillecube.client.resources.ModelManager;
import com.grillecube.common.world.World;
import com.grillecube.common.world.entity.Entity;

/** a factory class which create model renderer lists */
public class ModelRendererFactory {

	/** the main renderer */
	private MainRenderer mainRenderer;

	/** model manager */
	private ModelManager modelManager;

	/** the entity in frustum list */
	private HashMap<Model, ArrayList<ModelInstance>> renderingList;

	/** event */
	private EventEntityList eventEntityList;

	public ModelRendererFactory(MainRenderer mainRenderer) {
		this.mainRenderer = mainRenderer;
		this.modelManager = mainRenderer.getResourceManager().getModelManager();
		this.renderingList = new HashMap<Model, ArrayList<ModelInstance>>();
		this.eventEntityList = new EventEntityList();
	}

	public void onWorldSet(World world) {
	}

	public void onWorldUnset(World world) {
	}

	public void update(World world, CameraProjective camera) {
		this.updateEntitiesInFrustum(world, camera);
	}

	// private HashMap<Entity, Line[]> boxids = new HashMap<Entity, Line[]>();

	private void updateEntitiesInFrustum(World world, CameraProjective camera) {

		this.renderingList.clear();

		// for each entity of the world (maybe we can do better here?)
		Collection<Entity> entities = world.getEntityStorage().getEntities();
		for (Entity entity : entities) {

			ModelInstance modelInstance = this.modelManager.getModelInstance(entity);
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
			VoxelEngineClient.instance().getRenderer().getWorldRenderer().getLineRenderer()
					.addBox(modelInstance.getEntity().getBoundingBox());
			instances.add(modelInstance);

			// VoxelEngineClient.instance().getRenderer().getWorldRenderer().getLineRenderer()
			// .addBox(entity.getBoundingBox());
		}

		this.mainRenderer.getResourceManager().getEventManager().invokeEvent(this.eventEntityList);
	}

	/** get the last calculated entities in frustum */
	public HashMap<Model, ArrayList<ModelInstance>> getRenderingList() {
		return (this.renderingList);
	}

	public void deinitialize() {
		// TODO Auto-generated method stub

	}

}