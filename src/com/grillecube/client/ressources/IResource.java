package com.grillecube.client.ressources;

public interface IResource
{
	/** load the resources */
	public void	load(ResourceManager manager);
	
	/** unload resources */
	public void	unload(ResourceManager manager);
}
