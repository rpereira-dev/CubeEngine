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

/**
 * Main mod interface
 */

public interface IMod {
	/** called when the mod should be initialized */
	public void initialize(Mod mod);

	/** called when the mod should be deinitialized */
	public void deinitialize(Mod mod);
}
