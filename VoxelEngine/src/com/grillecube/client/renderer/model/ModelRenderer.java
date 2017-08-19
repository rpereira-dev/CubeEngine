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
import java.util.HashMap;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import com.grillecube.client.opengl.GLH;
import com.grillecube.client.renderer.MainRenderer;
import com.grillecube.client.renderer.camera.CameraProjective;
import com.grillecube.client.renderer.camera.CameraProjectiveWorld;
import com.grillecube.client.renderer.model.instance.ModelInstance;
import com.grillecube.client.renderer.world.RendererWorld;
import com.grillecube.common.Taskable;
import com.grillecube.common.VoxelEngine;
import com.grillecube.common.world.World;

public class ModelRenderer extends RendererWorld {

	/** the rendering program */
	private ProgramModel programModel;

	/** the model factory */
	private ModelRendererFactory factory;

	/** model instances to be rendered, ordered by model */
	private HashMap<Model, ArrayList<ModelInstance>> renderingList;

	public ModelRenderer(MainRenderer mainRenderer) {
		super(mainRenderer);
	}

	@Override
	public void initialize() {
		this.programModel = new ProgramModel();
		this.renderingList = new HashMap<Model, ArrayList<ModelInstance>>();
		this.factory = new ModelRendererFactory(this.getParent());
	}

	@Override
	public void deinitialize() {

		GLH.glhDeleteObject(this.programModel);
		this.programModel = null;

		this.renderingList = null;

		this.factory.deinitialize();
		this.factory = null;
	}

	@Override
	public void onWorldSet(World world) {
		this.factory.onWorldSet(world);
	}

	@Override
	public void onWorldUnset(World world) {
		this.factory.onWorldUnset(world);
	}

	/**
	 * here we basically catch every visible entities and add them to a
	 * rendering list
	 */
	@Override
	public void preRender() {
		// get the next model rendering list
		this.renderingList = this.factory.getRenderingList();
	}

	/** render world terrains */
	@Override
	public void render() {
		this.render(super.getCamera());
	}

	private void render(CameraProjective camera) {

		if (this.renderingList == null) {
			return;
		}

		if (this.getParent().getGLFWWindow().isKeyPressed(GLFW.GLFW_KEY_F)) {
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
		}

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		GL11.glEnable(GL11.GL_DEPTH_TEST);

		// enable model program
		this.programModel.useStart();
		{
			// load global uniforms
			this.programModel.loadCamera(camera);

			// for each entity to render
			for (ArrayList<ModelInstance> models : this.renderingList.values()) {
				if (models.size() > 0) {
					Model model = models.get(0).getModel();
					ModelMesh mesh = model.getMesh();
					this.programModel.loadModel(model);
					mesh.bind();
					for (ModelInstance instance : models) {
						this.programModel.loadModelInstance(instance);
						mesh.drawElements();
					}
				}
			}
		}

		this.programModel.useStop();
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);

		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);

	}

	@Override
	public void getTasks(VoxelEngine engine, ArrayList<com.grillecube.common.VoxelEngine.Callable<Taskable>> tasks,
			World world, CameraProjectiveWorld camera) {
		tasks.add(engine.new Callable<Taskable>() {

			@Override
			public Taskable call() throws Exception {
				factory.update(world, camera);
				return (ModelRenderer.this);
			}

			@Override
			public String getName() {
				return ("ModelRendererFactory update");
			}
		});
	}
}