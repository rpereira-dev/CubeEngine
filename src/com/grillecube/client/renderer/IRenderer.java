package com.grillecube.client.renderer;

public interface IRenderer
{
	/** call on initialization : load your shaders / and buffer heres */
	public void	start();
	
	/** main rendering function : draw your stuff here */
	public void	render();
}
