package com.grillecube.client.renderer;

import org.lwjgl.opengl.GL11;

import com.grillecube.client.renderer.model.AnimationInstance;
import com.grillecube.client.renderer.model.ModelInstance;
import com.grillecube.client.renderer.model.ModelPart;
import com.grillecube.client.renderer.model.ModelPartInstance;
import com.grillecube.client.renderer.model.Models;
import com.grillecube.client.renderer.program.ProgramModel;
import com.grillecube.client.world.WorldClient;
import com.grillecube.client.world.entity.EntityModeled;

public class ModelRenderer
{	
	private ProgramModel	_program_model;
	
	public ModelRenderer()
	{
	}
	
	/** render world terrains */
	public void render(WorldClient world, Camera camera)
	{
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		this._program_model.useStart();
		{
			this._program_model.loadUniforms(camera);
			for (EntityModeled entity : world.getEntities())
			{
				ModelInstance instance = entity.getModelInstance();
				
				for (ModelPartInstance part : instance.getPartInstances())
				{
					ModelPart model_part = part.getModelPart();
					AnimationInstance animation_instance = part.getAnimationInstance();

					animation_instance.update();
					this._program_model.loadAnimationUniforms(entity, animation_instance);
					
					model_part.render();
					
				}
			}
		}
		this._program_model.useStop();
	}

	/** start the renderer */
	public void start()
	{
		this._program_model = new ProgramModel();
		Models.initializeModels();
	}
	
	/** stop the renderer */
	public void stop()
	{
		this._program_model.stop();
		this._program_model = null;
	}
}
