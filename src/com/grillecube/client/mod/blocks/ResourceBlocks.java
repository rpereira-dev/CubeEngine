package com.grillecube.client.mod.blocks;

import com.grillecube.client.ressources.BlockManager;
import com.grillecube.client.ressources.IResource;
import com.grillecube.client.ressources.ResourceManager;

public class ResourceBlocks implements IResource
{
	// blocks id
	public static short AIR;
	public static short DIRT;
	public static short GRASS;
	public static short STONE;
	public static short ICE;
	
	// texture id
	public static int T_DIRT;
	public static int T_GRASS_TOP;
	public static int T_GRASS_SIDE;
	public static int T_STONE;
	public static int T_ICE;
	
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
		T_ICE = manager.registerBlockTexture("./assets/textures/blocks/ice.png");		
	}

	private void registerBlocks(BlockManager manager)
	{
		AIR = manager.registerBlock(new BlockAir());
		DIRT = manager.registerBlock(new BlockDirt());
		GRASS = manager.registerBlock(new BlockGrass());
		STONE = manager.registerBlock(new BlockStone());
		ICE = manager.registerBlock(new BlockIce());
	}
	
	@Override
	public void unload(ResourceManager manager) {}

}
