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

package com.grillecube.common.mod;

import com.grillecube.common.resources.ResourceManager;

public interface IModResource {
	/** load the resources */
	public void load(Mod mod, ResourceManager manager);

	/**
	 * unload resources. Useless most of the time because the program clean
	 * every resources at the program termination anyway :)
	 */
	public void unload(Mod mod, ResourceManager manager);
}
