package com.grillecube.client.world.entity;

import com.grillecube.client.renderer.model.Model;
import com.grillecube.client.renderer.model.ModelInstance;
import com.grillecube.common.world.World;
import com.grillecube.common.world.entity.Entity;

public abstract class EntityModeled extends Entity
{
	private ModelInstance	_model_instance;
	
	public EntityModeled(World world, Model model)
	{
		super(world);
		this._model_instance = new ModelInstance(model);
	}
	
	/** start the animation for this entity */
	public void	startAnimation(int animationID)
	{
		this._model_instance.startAnimation(animationID);
	}
	
	/** stop the animation for this entity */
	public void	stopAnimation(int animationID)
	{
		this._model_instance.stopAnimation(animationID);
	}
	
	/** stop all animations for this entity */
	public void	stopAnimations()
	{
		this._model_instance.stopAnimations();
	}
}
