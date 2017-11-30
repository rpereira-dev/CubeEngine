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

import org.lwjgl.openal.ALC10;

import com.grillecube.common.Logger;
import com.grillecube.common.Logger.Level;

public class ALDevice extends ALObject {
	private long _id;

	public ALDevice(long id) {
		this._id = id;
	}

	@Override
	public void onDestroy() {
		if (ALC10.alcCloseDevice(this._id)) {
			Logger.get().log(Level.FINE, "Device closed properly: " + this._id);
		} else {
			Logger.get().log(Level.ERROR, "Failed to close device: " + this._id);
		}
	}

	public long getID() {
		return (this._id);
	}

}
