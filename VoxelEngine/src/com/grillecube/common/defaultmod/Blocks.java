package com.grillecube.common.defaultmod;

import com.grillecube.common.mod.IModResource;
import com.grillecube.common.mod.Mod;
import com.grillecube.common.resources.BlockManager;
import com.grillecube.common.resources.ResourceManager;
import com.grillecube.common.world.block.Block;
import com.grillecube.common.world.block.BlockAir;
import com.grillecube.common.world.block.BlockDirt;
import com.grillecube.common.world.block.BlockGrass;
import com.grillecube.common.world.block.BlockLeaves;
import com.grillecube.common.world.block.BlockLight;
import com.grillecube.common.world.block.BlockLiquidWater;
import com.grillecube.common.world.block.BlockLog;
import com.grillecube.common.world.block.BlockStone;

public class Blocks implements IModResource {
	public static final int AIR_ID = 0;

	/** load default blocks */
	public static Block AIR;
	public static Block DIRT;
	public static Block GRASS;
	public static Block STONE;
	public static Block LIGHT_TEST;
	public static Block LOG;
	public static Block LEAVES;
	public static Block LIQUID_WATER;

	@Override
	public void load(Mod mod, ResourceManager manager) {
		BlockManager blockmanager = manager.getBlockManager();
		this.loadBlocks(blockmanager);
	}

	private void loadBlocks(BlockManager blockmanager) {
		Blocks.AIR = blockmanager.registerBlock(new BlockAir());
		Blocks.DIRT = blockmanager.registerBlock(new BlockDirt(blockmanager.getNextID()));
		Blocks.GRASS = blockmanager.registerBlock(new BlockGrass(blockmanager.getNextID()));
		Blocks.STONE = blockmanager.registerBlock(new BlockStone(blockmanager.getNextID()));
		Blocks.LIGHT_TEST = blockmanager.registerBlock(new BlockLight(blockmanager.getNextID()) {
			public byte getLightValue() {
				return (15);
			}
		});
		Blocks.LOG = blockmanager.registerBlock(new BlockLog(blockmanager.getNextID()));
		Blocks.LEAVES = blockmanager.registerBlock(new BlockLeaves(blockmanager.getNextID()));
		Blocks.LIQUID_WATER = blockmanager.registerBlock(new BlockLiquidWater(blockmanager.getNextID()));
	}

	@Override
	public void unload(Mod mod, ResourceManager manager) {
	}

	public static Block getBlockByID(short blockID) {
		return (BlockManager.instance().getBlockByID(blockID));
	}
}