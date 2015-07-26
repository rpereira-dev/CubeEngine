package com.grillecube.client.world.blocks;

public class Blocks
{
	public static final byte AIR		= 0;
	public static final byte DIRT		= 1;
	public static final byte GRASS		= 2;
	public static final byte STONE		= 3;
	public static final byte MAX_ID		= 4;
	
	private static Block[] _blocks = null;
	
	/** create block and add it to the blocks array */
	private static void	createBlock(String name, byte blockID, int textureID, int ... faces)
	{
		_blocks[blockID] = new Block(name, blockID, textureID, faces);
	}
	
	/** initialize blocks, should be called after opengl initialization (for textures) ! */
	public static void	initBlocks()
	{
		BlockTextures.initTextures();
		
		_blocks = new Block[MAX_ID];
		
		createBlock("air",	Blocks.AIR,	BlockTextures.NONE);
		createBlock("dirt",	Blocks.DIRT, BlockTextures.DIRT);
		createBlock("grass", Blocks.GRASS, BlockTextures.GRASS_SIDE, Block.FACE_TOP, BlockTextures.GRASS_TOP, Block.FACE_BOT, BlockTextures.DIRT);
		createBlock("stone", Blocks.STONE, BlockTextures.STONE);
	}
	
	public static Block	getBlockByID(byte id)
	{
		return (_blocks[id]);
	}
}
