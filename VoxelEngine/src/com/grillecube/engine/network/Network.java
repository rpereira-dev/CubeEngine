package com.grillecube.engine.network;

import com.grillecube.engine.VoxelEngine;

public interface Network
{
	/** stop the network */
	public void stop();

	/** get side of the network */
	public VoxelEngine.Side getSide();
}
