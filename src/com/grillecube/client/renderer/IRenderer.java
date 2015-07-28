package com.grillecube.client.renderer;

import com.grillecube.client.world.WorldClient;

public interface IRenderer
{
	/** call on initialization : load your shaders / and buffer heres */
	public void	start();
	
	/** call on ending : clear your data ! */
	public void	stop();
	
	/** main rendering function : draw your stuff here */
	public void	render(WorldClient world, Camera camera);
}
