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
import org.lwjgl.opengl.GL13;

import com.grillecube.client.opengl.GLH;
import com.grillecube.client.renderer.MainRenderer;
import com.grillecube.client.renderer.Renderer;
import com.grillecube.client.renderer.camera.CameraProjective;
import com.grillecube.client.renderer.model.instance.ModelInstance;
import com.grillecube.common.Taskable;
import com.grillecube.common.VoxelEngine;
import com.grillecube.common.VoxelEngine.Callable;

public class ModelRenderer extends Renderer {

	/** the rendering program */
	private ProgramModel programModel;

	public ModelRenderer(MainRenderer mainRenderer) {
		super(mainRenderer);
	}

	@Override
	public void initialize() {
		this.programModel = new ProgramModel();
	}

	@Override
	public void deinitialize() {

		GLH.glhDeleteObject(this.programModel);
		this.programModel = null;
	}

	/** render world terrains */
	public void render(CameraProjective camera, HashMap<Model, ArrayList<ModelInstance>> renderingList) {

		if (this.getMainRenderer().getGLFWWindow().isKeyPressed(GLFW.GLFW_KEY_F)) {
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
			for (ArrayList<ModelInstance> models : renderingList.values()) {
				if (models.size() > 0) {
					Model model = models.get(0).getModel();
					model.preRender();
					ModelMesh mesh = model.getMesh();
					this.programModel.loadModel(model);
					mesh.bind();
					for (ModelInstance instance : models) {
						model.getSkin(instance.getSkinID()).bind(GL13.GL_TEXTURE0, GL11.GL_TEXTURE_2D);
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
	public void getTasks(VoxelEngine engine, ArrayList<Callable<Taskable>> tasks) {
	}
}