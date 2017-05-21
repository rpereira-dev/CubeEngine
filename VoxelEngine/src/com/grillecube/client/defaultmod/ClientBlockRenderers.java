package com.grillecube.client.defaultmod;

import com.grillecube.client.renderer.blocks.BlockRendererCube;
import com.grillecube.client.renderer.blocks.BlockRendererLiquid;
import com.grillecube.client.renderer.blocks.BlockRendererManager;
import com.grillecube.client.resources.ResourceManagerClient;
import com.grillecube.common.defaultmod.Blocks;
import com.grillecube.common.faces.Face;
import com.grillecube.common.mod.IModResource;
import com.grillecube.common.mod.Mod;
import com.grillecube.common.resources.BlockManager;
import com.grillecube.common.resources.R;
import com.grillecube.common.resources.ResourceManager;
import com.grillecube.common.world.block.Block;

public class ClientBlockRenderers implements IModResource {
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

		blockTextureManager.setBlockRenderer(Blocks.DIRT, new BlockRendererCube(ClientBlockRenderers.T_DIRT));

		blockTextureManager.setBlockRenderer(Blocks.GRASS,
				new BlockRendererCube(Face.LEFT, ClientBlockRenderers.T_GRASS_SIDE, Face.RIGHT,
						ClientBlockRenderers.T_GRASS_SIDE, Face.FRONT, ClientBlockRenderers.T_GRASS_SIDE, Face.BACK,
						ClientBlockRenderers.T_GRASS_SIDE, Face.TOP, ClientBlockRenderers.T_GRASS_TOP, Face.BOT,
						ClientBlockRenderers.T_DIRT));

		blockTextureManager.setBlockRenderer(Blocks.LEAVES, new BlockRendererCube(ClientBlockRenderers.T_LEAVES));
		blockTextureManager.setBlockRenderer(Blocks.LIGHT_TEST, new BlockRendererCube(ClientBlockRenderers.T_LEAVES));

		blockTextureManager.setBlockRenderer(Blocks.LOG,
				new BlockRendererCube(Face.LEFT, ClientBlockRenderers.T_LOG_SIDE, Face.RIGHT,
						ClientBlockRenderers.T_LOG_SIDE, Face.FRONT, ClientBlockRenderers.T_LOG_SIDE, Face.BACK,
						ClientBlockRenderers.T_LOG_SIDE, Face.TOP, ClientBlockRenderers.T_LOG_INSIDE, Face.BOT,
						ClientBlockRenderers.T_LOG_INSIDE));

		blockTextureManager.setBlockRenderer(Blocks.STONE, new BlockRendererCube(ClientBlockRenderers.T_STONE));
		blockTextureManager.setBlockRenderer(Blocks.LIQUID_WATER,
				new BlockRendererLiquid(ClientBlockRenderers.T_LIQUID));
	}

	@Override
	public void unload(Mod mod, ResourceManager manager) {
	}

	public static Block getBlockByID(short blockID) {
		return (BlockManager.instance().getBlockByID(blockID));
	}
}