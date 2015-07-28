package com.grillecube.client.ressources;

import java.util.ArrayList;

public class ResourceManager
{
	/** Resources */
	private ArrayList<IResource> _resources;

	public ResourceManager()
	{
		this._resources = new ArrayList<IResource>();
	}
	
	/** inject resources */
	public void	injectResources(IResource resource)
	{
		this._resources.add(resource);
	}

	/** load every game resources */
	public void	start()
	{
		this._resources.add(new BlockTextures());
		for (IResource res : _resources)
		{
			res.load();
		}
	}
}
