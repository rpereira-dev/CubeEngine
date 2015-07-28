package com.grillecube.client.world.blocks;

import com.grillecube.client.ressources.BlockTextures;
import com.grillecube.server.Game;

import fr.toss.lib.Logger;

public class Blocks
{
	/** default blocks ID */
	public static final short AIR		= 0;
	public static final short DIRT		= 1;
	public static final short GRASS		= 2;
	public static final short STONE		= 3;
	
	/** block ID counter */
	
	/** block array */
	private static Block[] _blocks = null;
	private static final int DEFAULT_CAPACITY = 512;
	
	/**
	 * create block and add it to the blocks array.
	 * if the adding fails (due to wrong block formatting), null is returned
	 * else, if the block was correcly added, a reference to the block is returned
	 */
	public static void	registerBlock(Block block)
	{
		Game.getLogger().log(Logger.Level.FINE, "Adding a block: " + block.toString());
		_blocks[block.getID()] = block;
	}
	
	/** initialize blocks, should be called after opengl initialization (for textures) ! */
	public static void	initBlocks()
	{		
		_blocks = new Block[DEFAULT_CAPACITY];
		
		registerBlock(new Block("air", Blocks.AIR, BlockTextures.NONE));
		registerBlock(new Block("dirt", Blocks.DIRT, BlockTextures.DIRT));
		registerBlock(new Block("grass", Blocks.GRASS, BlockTextures.GRASS_SIDE, Block.FACE_TOP, BlockTextures.GRASS_TOP, Block.FACE_BOT, BlockTextures.DIRT));
		registerBlock(new Block("stone", Blocks.STONE, BlockTextures.STONE));
	}
	
	/** return the next free ID for this block */
	public static int	getNextID()
	{
		int	length = _blocks.length;
		
		int i;
		for (i = 0 ; i < length ; i++)
		{
			if (_blocks[i] == null)
			{
				return (i);
			}
		}
		return (i);
	}
	
	/** get block by name */
	public static Block	getBlockByName(String name)
	{
		for (Block block : _blocks)
		{
			if (block.getName().equals(name))
			{
				return (block);
			}
		}
		return (null);
	}
	
	public static Block	getBlockByID(short id)
	{
		return (_blocks[id]);
	}
}
