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

import com.grillecube.client.VoxelEngineClient;
import com.grillecube.common.resources.ResourceManager;

public class ResourceManagerClient extends ResourceManager {

	/** block renderer manager */
	private BlockRendererManager blockTextureManager;

	/** Sound manager */
	private SoundManager soundManager;

	/** Models manager */
	private ModelManager modelManager;

	public ResourceManagerClient(VoxelEngineClient engine) {
		super(engine);
	}

	@Override
	protected void addManagers() {
		super.addManagers();

		this.blockTextureManager = new BlockRendererManager(this);
		super.addManager(this.blockTextureManager);

		this.soundManager = new SoundManager(this);
		super.addManager(this.soundManager);

		this.modelManager = new ModelManager(this);
		super.addManager(this.modelManager);
	}

	/** the block renderer manager */
	public final BlockRendererManager getBlockTextureManager() {
		return (this.blockTextureManager);
	}

	/** get the sound manager */
	public final SoundManager getSoundManager() {
		return (this.soundManager);
	}

	/** get the model manager */
	public final ModelManager getModelManager() {
		return (this.modelManager);
	}

}
