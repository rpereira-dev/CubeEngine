package com.grillecube.client.renderer.model;

public class ModelInstance
{
	private ModelPartInstance[]	_parts_instances;
	
	public ModelInstance(Model model)
	{
		int	i;
		
		this._parts_instances = new ModelPartInstance[model.getPartsCount()];
		for (i = 0 ; i < this._parts_instances.length ; i++)
		{
			this._parts_instances[i] = new ModelPartInstance(model.getPartAt(i));
		}
	}
	
	/** start animation for this model instance */
	public void	startAnimation(int animationID)
	{
		for (ModelPartInstance parts : this._parts_instances)
		{
			parts.startAnimation(animationID);
		}
	}
	
	/** stop animation for this model instance */
	public void	stopAnimation(int animationID)
	{
		for (ModelPartInstance parts : this._parts_instances)
		{
			parts.stopAnimation(animationID);
		}
	}

	/** stop all animations for this model instance */
	public void	stopAnimations()
	{
		for (ModelPartInstance parts : this._parts_instances)
		{
			parts.stopAnimations();
		}
	}
}
