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

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;

import com.grillecube.engine.maths.Vector3f;

public class ALSource extends ALObject {
	private FloatBuffer _buffer;

	private int _id;

	public ALSource(int id) {
		this._id = id;
		this._buffer = BufferUtils.createFloatBuffer(3);
	}

	@Override
	public void onDestroy() {
		AL10.alDeleteBuffers(this._id);
	}

	public int getID() {
		return (this._id);
	}

	/** play a sound relative to the source (global) */
	public void play(ALSound sound, int relative) {
		this.play(sound, 1.0f, 1.0f, relative);
	}

	public void play(ALSound sound) {
		this.play(sound, 1.0f, 1.0f, AL10.AL_FALSE);
	}
	
	public void play(ALSound sound, float pitch, float gain) {
		this.play(sound, pitch, gain, AL10.AL_FALSE);
	}

	/** play a sound relative to the source (global) */
	public void play(ALSound sound, float pitch, float gain, int relative) {
		this.setInteger(AL10.AL_BUFFER, sound.getBuffer().getID());
		this.setFloat(AL10.AL_PITCH, pitch);
		this.setFloat(AL10.AL_GAIN, gain);
		this.setInteger(AL10.AL_SOURCE_RELATIVE, relative);
		ALH.alhSourcePlay(this);
	}

	/** play a sound relative to the listener */
	public void playAt(ALSound sound) {
		this.playAt(sound, new Vector3f(0, 0, 0), new Vector3f(0, 0, 0));
	}

	/** play a sound relative to the listener */
	public void playAt(ALSound sound, Vector3f pos) {
		this.playAt(sound, pos, new Vector3f(0, 0, 0));
	}

	/** play a sound relative to the listener */
	public void playAt(ALSound sound, Vector3f pos, Vector3f velocity) {
		this.setInteger(AL10.AL_DISTANCE_MODEL, AL11.AL_LINEAR_DISTANCE);
		this.setFloatVector(AL10.AL_POSITION, pos);
		this.setFloatVector(AL10.AL_VELOCITY, velocity);
		this.play(sound, AL10.AL_TRUE);
	}

	public void setMinGain(float value) {
		this.setFloat(AL10.AL_MIN_GAIN, value);
	}

	public void setMaxGain(float value) {
		this.setFloat(AL10.AL_MAX_GAIN, value);
	}

	public void stop() {
		ALH.alhSourceStop(this);
	}

	public boolean isPlaying() {
		return (this.getInteger(AL10.AL_SOURCE_STATE) == AL10.AL_PLAYING);
	}

	public void setFloat(int name, float value) {
		AL10.alSourcef(this.getID(), name, value);
	}

	public void setInteger(int name, int value) {
		AL10.alSourcei(this.getID(), name, value);
	}

	public void setFloatVector(int name, Vector3f vec) {
		this._buffer.put(vec.x);
		this._buffer.put(vec.y);
		this._buffer.put(vec.z);
		this._buffer.flip();
		AL10.alSourcefv(this.getID(), name, this._buffer);
		this._buffer.clear();
	}

	public int getInteger(int name) {
		return (ALH.alhGetSourcei(this, name));
	}

	/** set the source loops or not */
	public void loop(boolean value) {
		if (value) {
			this.setInteger(AL10.AL_LOOPING, AL10.AL_TRUE);
		} else {
			this.setInteger(AL10.AL_LOOPING, AL10.AL_FALSE);
			this.stop();
		}
	}

	/** set the source loops or not */
	public boolean isLooping(boolean value) {
		return (this.getInteger(AL10.AL_LOOPING) == AL10.AL_TRUE);
	}
}
