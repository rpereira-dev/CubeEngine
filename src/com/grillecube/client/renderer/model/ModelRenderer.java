package com.grillecube.client.renderer.model;

import org.lwjgl.opengl.GL11;

import com.grillecube.client.Game;
import com.grillecube.client.renderer.ARenderer;
import com.grillecube.client.world.entity.EntityModeled;

public class ModelRenderer extends ARenderer
{	
	public ModelRenderer(Game game)
	{
		super(game);
	}

	private ProgramModel _program_model;
	
	/** render world terrains */
	@Override
	public void render()
	{
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		this._program_model.useStart();
		{
			this._program_model.loadUniforms(this.getCamera());
			for (EntityModeled entity : this.getWorld().getEntities())
			{
				ModelInstance instance = entity.getModelInstance();
				
				for (ModelPartInstance part : instance.getPartInstances())
				{
					ModelPart model_part = part.getModelPart();
					AnimationInstance animation_instance = part.getAnimationInstance();

					if (part.isPlayingAnimation() == false)
					{
						part.startAnimation(0);
					}
					animation_instance.update();
					this._program_model.loadAnimationUniforms(entity, animation_instance);
					
					model_part.render();
					
				}
			}
		}
		this._program_model.useStop();
	}

	/** start the renderer */
	@Override
	public void start()
	{
		this._program_model = new ProgramModel();
		Models.initializeModels();
	}
}
