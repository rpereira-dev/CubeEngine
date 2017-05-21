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

import java.util.ArrayList;

import com.grillecube.client.VoxelEngineClient;
import com.grillecube.client.renderer.blocks.BlockRendererManager;
import com.grillecube.common.resources.GenericManager;
import com.grillecube.common.resources.ResourceManager;

public class ResourceManagerClient extends ResourceManager {

	/** block renderer manager */
	private BlockRendererManager blockTextureManager;

	/** Sound manager */
	private SoundManager soundManager;

	public ResourceManagerClient(VoxelEngineClient engine) {
		super(engine);
	}

	@Override
	protected void addResources(ArrayList<GenericManager<?>> managers) {
		super.addResources(managers);
		this.blockTextureManager = new BlockRendererManager(this);
		managers.add(this.blockTextureManager);

		this.soundManager = new SoundManager(this);
		managers.add(this.soundManager);

	}

	public final BlockRendererManager getBlockTextureManager() {
		return (this.blockTextureManager);
	}

	/** get the sound manager */
	public final SoundManager getSoundManager() {
		return (this.soundManager);
	}

}
