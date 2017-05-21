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

import org.lwjgl.openal.AL10;

public class ALBuffer extends ALObject {
	private int _id;

	public ALBuffer(int id) {
		this._id = id;
	}

	@Override
	public void onDestroy() {
		AL10.alDeleteBuffers(this._id);
	}

	public int getID() {
		return (this._id);
	}

	public void bufferData(ALSound file) {
		ALH.alhBufferData(this, file);
	}

}
