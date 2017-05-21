/**
**	This file is part of the project https://github.com/toss-dev/VoxelEngine
**
**	License is available here: https://raw.githubusercontent.com/toss-dev/VoxelEngine/master/LICENSE.md
**
**	PEREIRA Romain
**                                       4-----7          
**                                      /|    /|
**                                     0-----3 |
**                                     | 5___|_6
**                                     |/    | /
**                                     1-----2
*/

package com.grillecube.client.renderer.model;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import com.grillecube.client.opengl.GLH;
import com.grillecube.client.renderer.MainRenderer;
import com.grillecube.client.renderer.camera.CameraProjective;
import com.grillecube.client.renderer.camera.CameraProjectiveWorld;
import com.grillecube.client.renderer.model.instance.ModelInstance;
import com.grillecube.client.renderer.model.instance.ModelPartInstance;
import com.grillecube.client.renderer.world.RendererWorld;
import com.grillecube.common.Taskable;
import com.grillecube.common.VoxelEngine;
import com.grillecube.common.world.World;

/**
 * Instanced draws are implemented here, but only used when there is a lot of
 * models to draw, else way, multiple draw calls is faster
 * 
 * @author Romain
 *
 */

public class ModelRenderer extends RendererWorld {

	private ProgramModel _program_model;
	private ProgramModelShadow _program_model_shadow;

	private ModelRendererFactory _factory;
	private ArrayList<ModelInstance> _models;

	public ModelRenderer(MainRenderer main_renderer) {
		super(main_renderer);
	}

	@Override
	public void initialize() {
		this._program_model = new ProgramModel();
		this._program_model_shadow = new ProgramModelShadow();
		this._models = new ArrayList<ModelInstance>();
		this._factory = new ModelRendererFactory(this);
		this._factory.initialize();
	}

	@Override
	public void deinitialize() {

		GLH.glhDeleteObject(this._program_model);
		this._program_model = null;

		GLH.glhDeleteObject(this._program_model_shadow);
		this._program_model_shadow = null;

		this._models = null;

		this._factory.deinitialize();
		this._factory = null;
	}

	@Override
	public void onWorldSet(World world) {
		this._factory.onWorldSet(world);
	}

	@Override
	public void onWorldUnset(World world) {
		this._factory.onWorldUnset(world);
	}

	/**
	 * here we basically catch every visible entities and add them to a
	 * rendering list
	 */
	@Override
	public void preRender() {
		// get the next model rendering list
		this._models = this._factory.getRenderingList();
	}

	/** render world terrains */
	@Override
	public void render() {

		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);

		this.render(super.getCamera());

		GL11.glDisable(GL11.GL_CULL_FACE);
	}

	private void render(CameraProjective camera) {

		if (this._models == null) {
			return;
		}

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		GL11.glEnable(GL11.GL_DEPTH_TEST);

		// enable model program
		this._program_model.useStart();
		{
			// load global uniforms
			this._program_model.loadUniforms(camera);

			// for each model instance to render
			for (ModelInstance instance : this._models) {

				// the skin to use
				ModelSkin skin = instance.getModel().getSkin(instance.getSkinID());

				if (skin == null) {
					continue;
				}

				// render each of it parts
				ModelPartInstance[] instances = instance.getPartInstances();

				for (int i = 0; i < instances.length; i++) {

					ModelPartInstance part = instances[i];

					// load uniforms
					this._program_model.loadInstanceUniforms(part.getTransformationMatrix());
					// bind the part
					part.getModelPart().bind();
					// unable skin
					ModelPartSkin partskin = skin.getPart(i);
					part.getModelPart().toggleSkin(partskin);
					// render
					part.getModelPart().render();
					//
				}
				//
				// render equipment
				// if (instance.getEntity() instanceof EntityModeledLiving) {
				// EntityModeledLiving entity = (EntityModeledLiving)
				// instance.getEntity();
				// Item[] items = entity.getEquipments();
				//
				// // if the entity actually has equipmment
				// if (items != null) {
				// // for each of it equipments
				// for (Item item : items) {
				// // get it model
				// Model model = item.getModel();
				// // unable the skin
				// // model.toggleSkin(item.getSkinID());
				//
				// // TODO RENDER IT
				// }
				// }
				// }
			}
		}
		this._program_model.useStop();
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
	}

	@Override
	public void getTasks(VoxelEngine engine, ArrayList<com.grillecube.common.VoxelEngine.Callable<Taskable>> tasks,
			World world, CameraProjectiveWorld camera) {
		tasks.add(engine.new Callable<Taskable>() {

			@Override
			public Taskable call() throws Exception {
				_factory.update(world, camera);
				return (ModelRenderer.this);
			}

			@Override
			public String getName() {
				return ("ModelRendererFactory update");
			}
		});
	}
}