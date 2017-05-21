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

package com.grillecube.client.sound;

import com.grillecube.common.maths.Vector3f;

public class ALListener {
	public ALListener() {
	}

	public void setPosition(Vector3f vec) {
		ALH.alhSetListenerPosition(vec);
	}

	public void setOrientation(Vector3f vec) {
		ALH.alhSetListenerOrientation(vec);
	}
}
