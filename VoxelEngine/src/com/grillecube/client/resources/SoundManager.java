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

package com.grillecube.client.resources;

import com.grillecube.client.openal.ALH;
import com.grillecube.client.openal.ALSound;
import com.grillecube.client.openal.ALSourcePool;
import com.grillecube.client.renderer.camera.CameraView;
import com.grillecube.common.maths.Vector3f;
import com.grillecube.common.resources.GenericManager;
import com.grillecube.common.resources.ResourceManager;

/** sound manager register only file sound name */
public class SoundManager extends GenericManager<String> {
	private static SoundManager _instance = null;

	public static SoundManager instance() {
		return (_instance);
	}

	/** sources pool */
	private ALSourcePool soundPool;
	
	private boolean initialized;

	public SoundManager(ResourceManager manager) {
		super(manager);
		_instance = this;
		this.initialized = false;
	}

	@Override
	public void onInitialized() {
		this.initialized = ALH.alhInit();
		if (this.initialized) {
			this.soundPool = ALH.alhGenSourcePool(32);
		}
	}

	@Override
	public void onDeinitialized() {
		if (!this.initialized) {
			return ;
		}
		this.soundPool.stopAll();
		this.soundPool.destroy();
		ALH.alhStop();
	}

	@Override
	public void onLoaded() {
	}

	@Override
	public void onUnloaded() {
	}

	/**
	 * register a new sound
	 * 
	 * @param sound
	 *            : the filename (it has to be in folder './assets/sounds/')
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
		if (!this.initialized) {
			return ;
		}
		this.soundPool.play(sound);
	}

	/** play a sound relative to the listener */
	public void playSoundAt(ALSound sound, Vector3f pos) {
		if (!this.initialized) {
			return ;
		}
		this.soundPool.playAt(sound, pos);
	}

	/** play a sound relative to the listener */
	public void playSoundAt(ALSound sound, Vector3f pos, Vector3f velocity) {
		if (!this.initialized) {
			return ;
		}
		this.soundPool.playAt(sound, pos, velocity);
	}

	public void update(CameraView camera) {
		if (!this.initialized) {
			return ;
		}
		ALH.alhGetListener().setPosition(camera.getPosition());
		ALH.alhGetListener().setOrientation(camera.getViewVector());
	}

	@Override
	protected void onObjectRegistered(String object) {
	}
}
