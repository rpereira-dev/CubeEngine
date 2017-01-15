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

package com.grillecube.engine.resources;

import com.grillecube.engine.VoxelEngine;
import com.grillecube.engine.VoxelEngine.Side;
import com.grillecube.engine.maths.Vector3f;
import com.grillecube.engine.renderer.camera.CameraView;
import com.grillecube.engine.sound.ALH;
import com.grillecube.engine.sound.ALSound;
import com.grillecube.engine.sound.ALSourcePool;

/** sound manager register only file sound name */
public class SoundManager extends GenericManager<String> {
	private static SoundManager _instance = null;

	public static SoundManager instance() {
		return (_instance);
	}

	/** sources pool */
	private ALSourcePool _sound_pool;

	public SoundManager(ResourceManager manager) {
		super(manager);
		_instance = this;
	}

	@Override
	public void onInitialized() {

	}

	@Override
	public void onLoaded() {
		if (VoxelEngine.instance().getSide() == Side.CLIENT) {
			this._sound_pool = ALH.alhGenSourcePool(32);
		}
	}

	@Override
	public void onCleaned() {
		if (VoxelEngine.instance().getSide() == Side.CLIENT) {
			this._sound_pool.stopAll();
			this._sound_pool.destroy();
		}
	}

	@Override
	public void onStopped() {
		if (VoxelEngine.instance().getSide() == Side.CLIENT) {
			ALH.alhStop();
		}
	}
	
	/**
	 * register a new sound
	 * @param sound : the filename (it has to be in folder './assets/sounds/')
	 * @return
	 */
	public int registerSound(String sound) {
		return (super.registerObject(sound));
	}

	/**
	 * play a sound relative to source, meaning you will hear it wherever you
	 * are
	 */
	public void playSound(ALSound sound) {
		this._sound_pool.play(sound);
	}

	/** play a sound relative to the listener */
	public void playSoundAt(ALSound sound, Vector3f pos) {
		this._sound_pool.playAt(sound, pos);
	}

	/** play a sound relative to the listener */
	public void playSoundAt(ALSound sound, Vector3f pos, Vector3f velocity) {
		this._sound_pool.playAt(sound, pos, velocity);
	}

	public void update(CameraView camera) {
		ALH.alhGetListener().setPosition(camera.getPosition());
		ALH.alhGetListener().setOrientation(camera.getViewVector());
	}

	@Override
	protected void onObjectRegistered(String object) {}
}
