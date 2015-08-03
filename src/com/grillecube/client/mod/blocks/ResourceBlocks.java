package com.grillecube.client.mod.blocks;

import com.grillecube.client.ressources.BlockManager;
import com.grillecube.client.ressources.IResource;
import com.grillecube.client.ressources.ResourceManager;
import com.grillecube.client.world.blocks.Block;

public class ResourceBlocks implements IResource
{
	// blocks id
	public static short AIR;
	public static short DIRT;
	public static short GRASS;
	public static short STONE;
	
	// texture id
	public static int T_DIRT;
	public static int T_GRASS_TOP;
	public static int T_GRASS_SIDE;
	public static int T_STONE;
	
	@Override
	public void load(ResourceManager manager) 
	{
		this.registerBlockTextures(manager.getBlockManager());
		this.registerBlocks(manager.getBlockManager());
	}

	private void registerBlockTextures(BlockManager manager)
	{
		T_DIRT = manager.registerBlockTexture("./assets/textures/blocks/dirt.png");
		T_GRASS_TOP = manager.registerBlockTexture("./assets/textures/blocks/grass_top.png");
		T_GRASS_SIDE = manager.registerBlockTexture("./assets/textures/blocks/grass_side.png");
		T_STONE = manager.registerBlockTexture("./assets/textures/blocks/stone.png");		
	}

	private void registerBlocks(BlockManager manager)
	{
		AIR = manager.registerBlock(new BlockAir());
		DIRT = manager.registerBlock(new Block("dirt", T_DIRT));
		GRASS = manager.registerBlock(new Block("grass", T_GRASS_SIDE, Block.FACE_TOP, T_GRASS_TOP, Block.FACE_BOT, T_DIRT));
		STONE = manager.registerBlock(new Block("stone", T_STONE));
	}
	
	@Override
	public void unload(ResourceManager manager) {}

}
