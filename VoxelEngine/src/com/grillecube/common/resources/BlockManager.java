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

package com.grillecube.common.resources;

import com.grillecube.common.Logger;
import com.grillecube.common.world.block.Block;

public class BlockManager extends GenericManager<Block> {

	/** singleton */
	private static BlockManager BLOCK_MANAGER_INSTANCE;

	public BlockManager(ResourceManager manager) {
		super(manager);
		BLOCK_MANAGER_INSTANCE = this;
	}

	/** singleton */
	public static BlockManager instance() {
		return (BLOCK_MANAGER_INSTANCE);
	}

	/**
	 * register the given block to the engine
	 */
	public Block registerBlock(Block block) {
		Logger.get().log(Logger.Level.FINE, "Registering a block: " + block.toString());
		super.registerObject(block);
		return (block);
	}

	/** get block by id */
	public Block getBlockByID(int id) {
		return (super.getObjectByID(id));
	}

	public int getBlockCount() {
		return (super.getObjectCount());
	}

	@Override
	public void onInitialized() {

	}

	@Override
	public void onStopped() {
	}

	@Override
	public void onLoaded() {

	}

	@Override
	public void onCleaned() {
		super.clean();
	}

	@Override
	protected void onObjectRegistered(Block object) {
	}
}
