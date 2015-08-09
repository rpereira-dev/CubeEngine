package com.grillecube.client.renderer.model;

//TODO : change this with a dynamic way
public class Models
{
	/** every models array */
	private static Model[] _models = null;
	
	/** every models ID */
	public static final int	PIG	= 0;
	
	private static final int MAX_ID = 1;
	
	/** should be called after opengl initialization */
	public static void	initializeModels()
	{
		_models = new Model[MAX_ID];
		_models[PIG] = ModelLoader.loadModel("pig");
	}
	
	/** return the model with the given ID */
	public static Model	getModel(int id)
	{
		return (_models[id]);
	}
}
