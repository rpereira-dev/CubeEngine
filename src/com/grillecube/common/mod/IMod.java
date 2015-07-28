package com.grillecube.common.mod;

import com.grillecube.client.Game;

/**
 *	Main mod interface
 */

public interface IMod
{
	/** called when the mod should be initialized */
	public void	initialize(Game game);
	
	/** called when the mod should be deinitialized */
	public void	deinitialize(Game game);
}
