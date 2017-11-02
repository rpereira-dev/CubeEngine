package com.grillecube.client.renderer.model;

import java.util.ArrayList;
import java.util.HashMap;

import com.grillecube.client.renderer.MainRenderer;
import com.grillecube.client.renderer.RendererFactory;
import com.grillecube.client.renderer.camera.CameraProjective;
import com.grillecube.client.renderer.model.instance.ModelInstance;
import com.grillecube.common.world.World;
import com.grillecube.common.world.entity.Entity;

/** a factory class which create model renderer lists */
public class ModelRendererFactory extends RendererFactory {

	/** the entity in frustum list */
	private HashMap<Model, ArrayList<ModelInstance>> renderingList;
	private ArrayList<ModelInstance> modelInstances;

	/** world and camera */
	private CameraProjective camera;

	public ModelRendererFactory(MainRenderer mainRenderer) {
		super(mainRenderer);
		this.renderingList = new HashMap<Model, ArrayList<ModelInstance>>();
		this.modelInstances = new ArrayList<ModelInstance>();
	}

	public final CameraProjective getCamera() {
		return (this.camera);
	}

	public final void setCamera(CameraProjective camera) {
		this.camera = camera;
	}

	@Override
	public final void update() {
		this.renderingList.clear();

		// for each entity of the world (maybe we can do better here?)
		for (ModelInstance modelInstance : this.modelInstances) {
			if (modelInstance == null || !modelInstance.getEntity().isVisible()) {
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
	}

	/** get the last calculated entities in frustum */
	public HashMap<Model, ArrayList<ModelInstance>> getRenderingList() {
		return (this.renderingList);
	}

	@Override
	public void render() {
		this.getMainRenderer().getModelRenderer().render(this.getCamera(), this.getRenderingList());
	}

	public final void addModelInstance(ModelInstance modelInstance) {
		this.modelInstances.add(modelInstance);
	}

	public final void removeModelInstance(ModelInstance modelInstance) {
		this.modelInstances.remove(modelInstance);
	}

	public final void clear() {
		this.modelInstances.clear();
		this.renderingList.clear();
	}

	public final void loadWorldModelInstance(World world) {
		for (Entity entity : world.getEntityStorage().getEntities()) {
			ModelInstance modelInstance = this.getResourceManager().getModelManager().getModelInstance(entity);
			if (modelInstance == null) {
				continue;
			}
			this.modelInstances.add(modelInstance);
		}
	}
}