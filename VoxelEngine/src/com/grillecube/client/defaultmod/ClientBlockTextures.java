package com.grillecube.client.defaultmod;

import com.grillecube.client.resources.BlockRendererManager;
import com.grillecube.client.resources.ResourceManagerClient;
import com.grillecube.common.defaultmod.Blocks;
import com.grillecube.common.faces.Face;
import com.grillecube.common.mod.IModResource;
import com.grillecube.common.mod.Mod;
import com.grillecube.common.resources.BlockManager;
import com.grillecube.common.resources.R;
import com.grillecube.common.resources.ResourceManager;
import com.grillecube.common.world.block.Block;

public class ClientBlockTextures implements IModResource {
	public static final int AIR_ID = 0;

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
		BlockRendererManager blockTextureManager = ((ResourceManagerClient) manager).getBlockTextureManager();
		this.loadTextures(blockTextureManager);
	}

	private void loadTextures(BlockRendererManager blockTextureManager) {
		T_EMPTY = blockTextureManager.registerBlockTexture(R.getResPath("textures/blocks/empty.png"));
		T_DIRT = blockTextureManager.registerBlockTexture(R.getResPath("textures/blocks/dirt.png"));
		T_GRASS_TOP = blockTextureManager.registerBlockTexture(R.getResPath("textures/blocks/grass_top.png"));
		T_GRASS_SIDE = blockTextureManager.registerBlockTexture(R.getResPath("textures/blocks/grass_side.png"));
		T_STONE = blockTextureManager.registerBlockTexture(R.getResPath("textures/blocks/stone.png"));
		T_LIQUID = blockTextureManager.registerBlockTexture(R.getResPath("textures/blocks/liquid.png"));
		T_LOG_SIDE = blockTextureManager.registerBlockTexture(R.getResPath("textures/blocks/log_side.png"));
		T_LOG_INSIDE = blockTextureManager.registerBlockTexture(R.getResPath("textures/blocks/log_inside.png"));
		T_LEAVES = blockTextureManager.registerBlockTexture(R.getResPath("textures/blocks/leaves.png"));

		blockTextureManager.setBlockTexture(Blocks.DIRT, ClientBlockTextures.T_DIRT);

		blockTextureManager.setBlockTextureFaces(Blocks.GRASS, Face.LEFT, ClientBlockTextures.T_GRASS_SIDE, Face.RIGHT,
				ClientBlockTextures.T_GRASS_SIDE, Face.FRONT, ClientBlockTextures.T_GRASS_SIDE, Face.BACK, ClientBlockTextures.T_GRASS_SIDE,
				Face.TOP, ClientBlockTextures.T_GRASS_TOP, Face.BOT, ClientBlockTextures.T_DIRT);

		blockTextureManager.setBlockTexture(Blocks.LEAVES, ClientBlockTextures.T_LEAVES);

		blockTextureManager.setBlockTexture(Blocks.LIGHT_TEST, ClientBlockTextures.T_LEAVES);
		blockTextureManager.setBlockTextureFaces(Blocks.LOG, Face.LEFT, ClientBlockTextures.T_LOG_SIDE, Face.RIGHT,
				ClientBlockTextures.T_LOG_SIDE, Face.FRONT, ClientBlockTextures.T_LOG_SIDE, Face.BACK, ClientBlockTextures.T_LOG_SIDE,
				Face.TOP, ClientBlockTextures.T_LOG_INSIDE, Face.BOT, ClientBlockTextures.T_LOG_INSIDE);

		blockTextureManager.setBlockTexture(Blocks.STONE, ClientBlockTextures.T_STONE);

		blockTextureManager.setBlockTexture(Blocks.LIQUID_WATER, ClientBlockTextures.T_LIQUID);

	}

	@Override
	public void unload(Mod mod, ResourceManager manager) {
	}

	public static Block getBlockByID(short blockID) {
		return (BlockManager.instance().getBlockByID(blockID));
	}
}