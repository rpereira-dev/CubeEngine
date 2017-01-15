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

package com.grillecube.engine.sound;

import java.nio.IntBuffer;

import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALCCapabilities;

public class ALContext extends ALObject {
	private long _id;
	private ALDevice _device;
	private ALListener _listener;
	private ALCCapabilities _capac;

	public static ALContext create() {
		ALContext context = new ALContext();
		// AL.createCapabilities(context._device.getID());

		context._device = ALH.alhGetDefaultDevice();
		context._id = ALC10.alcCreateContext(context._device.getID(), (IntBuffer) null);
		context._listener = new ALListener();

		context._capac = ALC.createCapabilities(context._device.getID());

		return (context);
	}

	public ALContext makeCurrent() {
		ALC10.alcMakeContextCurrent(this._id);
		AL.createCapabilities(this._capac);
		return (this);
	}

	@Override
	public void onDestroy() {
		ALC10.alcDestroyContext(this._id);
		this._device.destroy();
	}

	public long getID() {
		return (this._id);
	}

	public ALListener getListener() {
		return (this._listener);
	}
}
