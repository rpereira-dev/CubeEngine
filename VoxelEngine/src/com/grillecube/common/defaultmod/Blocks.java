package com.grillecube.common.defaultmod;

import com.grillecube.common.mod.IModResource;
import com.grillecube.common.mod.Mod;
import com.grillecube.common.resources.BlockManager;
import com.grillecube.common.resources.R;
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

	// texture id
	public static int T_EMPTY;
	public static int T_DIRT;
	public static int T_GRASS_TOP;
	public static int T_GRASS_SIDE;
	public static int T_STONE;
	public static int T_LIQUID;
	public static int T_LOG_SIDE;
	public static int T_LOG_INSIDE;
	public static int T_LEAVES;

	@Override
	public void load(Mod mod, ResourceManager manager) {
		BlockManager blockmanager = manager.getBlockManager();
		this.loadTextures(blockmanager);
		this.loadBlocks(blockmanager);
	}

	private void loadBlocks(BlockManager manager) {
		Blocks.AIR = manager.registerBlock(new BlockAir());
		Blocks.DIRT = manager.registerBlock(new BlockDirt(manager.getNextID()));
		Blocks.GRASS = manager.registerBlock(new BlockGrass(manager.getNextID()));
		Blocks.STONE = manager.registerBlock(new BlockStone(manager.getNextID()));
		Blocks.LIGHT_TEST = manager.registerBlock(new BlockLight(manager.getNextID()) {
			public byte getLightValue() {
				return (15);
			}
		});
		Blocks.LOG = manager.registerBlock(new BlockLog(manager.getNextID()));
		Blocks.LEAVES = manager.registerBlock(new BlockLeaves(manager.getNextID()));
		Blocks.LIQUID_WATER = manager.registerBlock(new BlockLiquidWater(manager.getNextID()));
	}

	private void loadTextures(BlockManager manager) {
		Blocks.T_EMPTY = manager.registerBlockTexture(R.getResPath("textures/blocks/empty.png"));
		Blocks.T_DIRT = manager.registerBlockTexture(R.getResPath("textures/blocks/dirt.png"));
		Blocks.T_GRASS_TOP = manager.registerBlockTexture(R.getResPath("textures/blocks/grass_top.png"));
		Blocks.T_GRASS_SIDE = manager.registerBlockTexture(R.getResPath("textures/blocks/grass_side.png"));
		Blocks.T_STONE = manager.registerBlockTexture(R.getResPath("textures/blocks/stone.png"));
		Blocks.T_LIQUID = manager.registerBlockTexture(R.getResPath("textures/blocks/liquid.png"));
		Blocks.T_LOG_SIDE = manager.registerBlockTexture(R.getResPath("textures/blocks/log_side.png"));
		Blocks.T_LOG_INSIDE = manager.registerBlockTexture(R.getResPath("textures/blocks/log_inside.png"));
		Blocks.T_LEAVES = manager.registerBlockTexture(R.getResPath("textures/blocks/leaves.png"));
	}

	@Override
	public void unload(Mod mod, ResourceManager manager) {
	}

	public static Block getBlockByID(short blockID) {
		return (BlockManager.instance().getBlockByID(blockID));
	}
}