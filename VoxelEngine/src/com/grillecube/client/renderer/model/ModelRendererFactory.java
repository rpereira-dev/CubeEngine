package com.grillecube.client.renderer.model;

import java.util.ArrayList;
import java.util.Collection;

import com.grillecube.client.VoxelEngineClient;
import com.grillecube.client.renderer.camera.CameraProjective;
import com.grillecube.client.renderer.model.instance.ModelInstance;
import com.grillecube.client.renderer.world.lines.LineRenderer;
import com.grillecube.common.maths.BoundingBox;
import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.maths.Vector4f;
import com.grillecube.common.resources.ModelManager;
import com.grillecube.common.world.World;
import com.grillecube.common.world.entity.Entity;
import com.grillecube.common.world.entity.EntityModeled;

/** a factory class which create model renderer lists */
public class ModelRendererFactory {

	private ModelRenderer _renderer;

	// instances sorted by model and skin ID
	// TODO: remove the two lists to keep only one as we are no longer figuring
	// thread issues
	private ArrayList<ModelInstance> _models;

	public ModelRendererFactory(ModelRenderer renderer) {
		this._renderer = renderer;
	}

	public void initialize() {
		this._models = new ArrayList<ModelInstance>();
	}

	public void deinitialize() {
	}

	public void onWorldSet(World world) {
		// TODO
	}

	public void onWorldUnset(World world) {
		// TODO
	}

	public void update(World world, CameraProjective camera) {

		this.updateEntitiesInFrustum(world, camera);
	}

	// private HashMap<Entity, Line[]> boxids = new HashMap<Entity, Line[]>();

	private void updateEntitiesInFrustum(World world, CameraProjective camera) {

		this._models.clear();

		// for each entity of the world (maybe we can do better here?)
		Collection<Entity> entities = world.getEntityStorage().getEntities();
		for (Entity entity : entities) {

			if (!(entity instanceof EntityModeled)) {
				continue;
			}

			// get it model instance
			ModelInstance instance = ((EntityModeled) entity).getModelInstance();
			if (instance.getModel() == null) {
				continue;
			}

			// get it bounding box
			BoundingBox box = instance.getGlobalBoundingBox();

			// if entities is not too far and in frustum, then add it to the
			// list
			double distance = Vector3f.distance(entity.getPosition(), camera.getPosition());

			if (distance > camera.getRenderDistance() || !camera.isBoxInFrustum(box)) {
				continue;
			}

			this._models.add(instance);
			box.setColor(new Vector4f(0.0f, 1.0f, 0.0f, 1.0f));

			// TODO
			LineRenderer renderer = VoxelEngineClient.instance().getRenderer().getWorldRenderer().getLineRenderer();

			// Line[] lines = boxids.get(entity);
			// renderer.removeLines(lines);
			// lines = renderer.addBox(box);
			// boxids.put(entity, lines);

			renderer.addBox(box);
		}
	}

	public ModelManager getModelManager() {
		return (this._renderer.getParent().getResourceManager().getModelManager());
	}

	public ArrayList<ModelInstance> getRenderingList() {
		return (this._models);
	}

}
