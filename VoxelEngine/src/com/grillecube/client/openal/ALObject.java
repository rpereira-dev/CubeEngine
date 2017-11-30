/**
**	This file is part of the project https://github.com/toss-dev/VoxelEngine
**
**	License is available here: https://raw.githubusercontent.com/toss-dev/VoxelEngine/master/LICENSE.md
**
**	PEREIRA Romain
**                                       4-----7          
**                                      /|    /|
**                                     0-----3 |
**                                     | 5___|_6
**                                     |/    | /
**                                     1-----2
*/

package com.grillecube.client.openal;

public abstract class ALObject
{
	private boolean _destroyed = false;

	/** free resources */
	public void destroy()
	{
		if (this._destroyed)
		{
			return ;
		}
		this.onDestroy();
		this._destroyed = true;
	}

	protected abstract void onDestroy();
}
