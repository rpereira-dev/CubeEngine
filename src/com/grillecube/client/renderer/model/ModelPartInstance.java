package com.grillecube.client.renderer.model;

public class ModelPartInstance
{
	private ModelPart			_model_part;
	private AnimationInstance	_animation_instance;
	
	public ModelPartInstance(ModelPart part)
	{
		this._model_part = part;
		this._animation_instance = new AnimationInstance();
	}
	
	/** start the animation for this model part */
	public void	startAnimation(int animationID)
	{
		this._animation_instance.start(this._model_part.getAnimationByID(animationID), false);
	}
	
	/** stop animation for this model instance */
	public void	stopAnimation(int animationID)
	{
		if (this._animation_instance.getAnimation().getID() == animationID)
		{
			this._animation_instance.stop();
		}
	}

	/** stop all animations for this model instance */
	public void	stopAnimations()
	{
		 this._animation_instance.stop();
	}
	
	/** return true if the model part is playing an animation */
	public boolean	isPlayingAnimation()
	{
		return (this._animation_instance.isPlaying());
	}
	
	/** return model part which is used by this instance */
	public ModelPart	getModelPart()
	{
		return (this._model_part);
	}

	public AnimationInstance	getAnimationInstance()
	{
		return (this._animation_instance);
	}
}
